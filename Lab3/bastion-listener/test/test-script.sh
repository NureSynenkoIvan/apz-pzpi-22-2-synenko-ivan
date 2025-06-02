cd ../src
echo "Drone-ID Frame Detection
Packet Type: droneid
/home/ivan/.local/lib/python3.10/site-packages/scipy/signal/_spectral_py.py:1814: UserWarning: Input data is complex, switching to return_onesided=False

################## Decoding Frame 1/10 ##################
get_packet_samples pkt=0
candidate band fstart: 219726.56, fend: -244140.62, bw: 0.46 MHz
candidate band fstart: 14233398.44, fend: 5004882.81, bw: 9.23 MHz
36725, offset=-9619140.625, Fs=50000000.0
FFO: -5791.512766
10580, offset=5791.512765718189, Fs=15360000.0
Found ZC sequences: 600 147
10580, offset=5791.512765718189, Fs=15360000.0
ZC Offset: -4.189189
10580, offset=5791.512765718189, Fs=15360000.0
10580, offset=5791.512765718189, Fs=15360000.0
10580, offset=5791.512765718189, Fs=15360000.0
## Drone-ID Payload ##
{
    "pkt_len": 88,
    "unk": 16,
    "version": 2,
    "sequence_number": 878,
    "state_info": 8179,
    "serial_number": "SecureStorage?",
    "longitude": 43.0,
    "latitude": 43.0,
    "altitude": 39.32,
    "height": 5.49,
    "v_north": 0,
    "v_east": -7,
    "v_up": 0,
    "d_1_angle": 16900,
    "gps_time": 1650894901980,
    "app_lat": 43.26826445428658,
    "app_lon": 6.640125363111847,
    "longitude_home": 57.26794359805882,
    "latitude_home": 1.446883970366635,
    "device_type": "Mini 2",
    "uuid_len": 0,
    "uuid": "",
    "crc-packet": "c935",
    "crc-calculated": "c935"
}" >> output.txt
python3 send_to_server.py output.txt