import configparser
import os
import time
import threading
import subprocess
from flask import Flask, jsonify

config = configparser.ConfigParser()
config.read('config.ini')

FREQUENCY = config['Radio']['frequency_to_tune_to']
SAMPLERATE = config['Radio']['samplerate']
NUMBER_OF_SAMPLES = config['Radio']['samplerate'] #Because we only record 1 second

app_status = "online"

app = Flask(__name__)

@app.route("/status", methods=["GET"])
def status():
    return jsonify({"status": app_status})

def run_flask():
    app.run(host="0.0.0.0", port=5000)

def run_main_loop():
    retries = 0
    max_retries = 2
    global app_status

    while True:
        print("[INFO] Starting rtl_sdr capture...")
        try:
            subprocess.run(["rtl_sdr", "-f", "100e3", "-s", "2400000", "-g", "20", "-n", "2400000", "output.iq"], check=True)
            retries = 0
        except subprocess.CalledProcessError as e:
            print(f"[ERROR] rtl_sdr failed: {e}, retry after 5 seconds, current retries: {retries}")
            if (retries <= max_retries):
                retries += 1
                time.sleep(5)
                continue
            else:
                print("[ERROR] Can't connect to rtl_sdr")
                app_status = "offline"

                print("[INFO] Will retry after 5 minutes...")
                time.sleep(300)
                retries = 0  
                continue


        if os.path.exists("output.iq"):
            print("[INFO] Running droneid_receiver_offline.py...")
            with open("output.txt", "w") as outfile:
                subprocess.run([
                    "python3", "./DroneSecurity/src/droneid_receiver_offline.py", 
                    "-i", "output.iq", "-i", "output.iq"
                ], stdout=outfile)

            with open("output.txt", "r") as f:
                if "Drone-ID Payload" in f.read():
                    print("[INFO] Sending data to server...")
                    subprocess.run(["python3", "send_to_server.py"])

            os.remove("output.iq")

        time.sleep(5)

if __name__ == "__main__":
    flask_thread = threading.Thread(target=run_flask, daemon=True)
    flask_thread.start()

    run_main_loop()