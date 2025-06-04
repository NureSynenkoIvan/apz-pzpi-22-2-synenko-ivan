import json
import os
import asyncio
import aiohttp
import configparser
from datetime import datetime

config = configparser.ConfigParser()
config.read('config.ini')

DRONE_ENDPOINT_URL = config['Server']['drone_endpoint']
SIGNAL_ENDPOINT_URL = config['Server']['signal_endpoint']
USERNAME = config['Credentials']['username']
PASSWORD = config['Credentials']['password']
RECEIVER_DEVICE_ID = int(config['Device']['receiver_device_id'])

async def parse_output_file(file_path):
    """Parse output.txt to extract Drone-ID and Signal Payloads."""
    drone_payloads = []
    signal_payloads = []
    current_json = {}
    in_payload = False
    current_signal = {}
    in_candidate_band = False
    
    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip()
            if line.startswith("## Drone-ID Payload ##"):
                in_payload = True
                current_json = {}
            if in_payload and line.startswith("{"):
                continue
            if in_payload and line.startswith("}"):
                if current_json.get("crc-packet") == current_json.get("crc-calculated"):
                    current_json["receiverDeviceId"] = RECEIVER_DEVICE_ID
                    drone_payloads.append(current_json)
                else:
                    signal_payload = handle_crc_error_payload(current_json, current_signal)
                    if signal_payload:
                        signal_payloads.append(signal_payload)
                in_payload = False
                current_signal = {}
                in_candidate_band = False
            if in_payload and line:
                try:
                    key, value = line.split(":", 1)
                    key = key.strip().strip('"')
                    value = value.strip().strip('",')
                    value = float(value) if '.' in value else int(value)
                    current_json[key] = value
                except ValueError:
                    continue
            if line.startswith("candidate band fstart:"):
                in_candidate_band = True
                current_signal = {}

                parts = line.split(',')
                fstart = float(parts[0].split(':')[1].strip())
                fend = float(parts[1].split(':')[1].strip())

                central_freq = (fstart + fend) / 2
                current_signal['frequency'] = central_freq  
            if in_candidate_band and "offset=" in line and "Fs=" in line:

                parts = line.split(',')
                for part in parts:
                    if "offset=" in part:
                        offset = float(part.split('=')[1].strip())
                        current_signal['frequency'] = abs(offset)  # Use absolute offset as frequency
                    if "Fs=" in part:
                        current_signal['signalStrength'] = 0.0  # Placeholder: no signal strength in output
                        current_signal['azimuth'] = None  # No azimuth data
                        current_signal['date'] = int(datetime.now().timestamp() * 1000)  # Current time in ms
                        current_signal['deviceId'] = RECEIVER_DEVICE_ID
                        in_candidate_band = False
                        break
    
    
    return drone_payloads, signal_payloads

def handle_crc_error_payload(drone_json, signal_data):
    """Handle CRC error payloads by creating a signal payload."""
    if not signal_data:
        return {}
    signal_payload = {
        "frequency": signal_data.get("frequency", 0.0),
        "signalStrength": signal_data.get("signalStrength", 0.0),
        "azimuth": signal_data.get("azimuth", None),
        "date": signal_data.get("date", int(datetime.now().timestamp() * 1000)),
        "deviceId": signal_data.get("deviceId", RECEIVER_DEVICE_ID)
    }
    return signal_payload
async def send_data(session, url, payload_type, payloads):
    """Send JSON data to HTTP endpoint."""
    if not payloads:
        print(f"No valid {payload_type} data to send")
        return True  # Treat as success, nothing to send

    message = {
        "type": payload_type,
        "payload": payloads
    }

    try:
        async with session.post(url, json=message) as resp:
            text = await resp.text()
            if resp.status == 200:
                print(f"Sent {len(payloads)} {payload_type} payloads. Server response: {resp.status} {text}")
                return True
            else:
                print(f"Failed to send {payload_type} data. Server response: {resp.status} {text}")
                return False
    except Exception as e:
        print(f"Error sending {payload_type} data: {e}")
        return False

async def main():
    """Main async entry point."""
    file_path = "output.txt"
    if not os.path.exists(file_path):
        print("File output.txt does not exist.")
        return

    drone_payloads, signal_payloads = await parse_output_file(file_path)

    async with aiohttp.ClientSession() as session:
        drone_success = await send_data(session, DRONE_ENDPOINT_URL, "drone", drone_payloads)
        signal_success = await send_data(session, SIGNAL_ENDPOINT_URL, "signal", signal_payloads)

        if drone_success and signal_success:
            try:
                os.remove(file_path)
                print("output.txt has been deleted after successful transmission.")
            except Exception as e:
                print(f"Failed to delete output.txt: {e}")
        else:
            print("Data not fully sent. File output.txt will not be deleted.")

if __name__ == "__main__":
    asyncio.run(main())
