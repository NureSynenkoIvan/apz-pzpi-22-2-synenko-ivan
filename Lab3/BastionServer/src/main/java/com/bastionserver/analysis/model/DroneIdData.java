package com.bastionserver.analysis.model;

//An information decoded from drone id signal.
public class DroneIdData {
    private int pkt_len;
    private int unk;
    private int version;
    private int sequence_number;
    private int state_info;
    private String serial_number;
    private double longitude;
    private double latitude;
    private double altitude;
    private double height;
    private int v_north;
    private int v_east;
    private int v_up;
    private int d_1_angle;
    private long gps_time;
    private double app_lat;
    private double app_lon;
    private double longitude_home;
    private double latitude_home;
    private String device_type;
    private int uuid_len;
    private String uuid;
    private String crc_packet;
    private String crc_calculated;

    private int receiverDeviceId;

    public DroneIdData() {}

    public DroneIdData(int pkt_len,
                       int unk,
                       int version,
                       int sequence_number,
                       int state_info,
                       String serial_number,
                       double longitude,
                       double latitude,
                       double altitude,
                       double height,
                       int v_north,
                       int v_east,
                       int v_up,
                       int d_1_angle,
                       long gps_time,
                       double app_lat,
                       double app_lon,
                       double longitude_home,
                       double latitude_home,
                       String device_type,
                       int uuid_len,
                       String uuid,
                       String crc_packet,
                       String crc_calculated,
                       int receiverDeviceId) {
        this.pkt_len = pkt_len;
        this.unk = unk;
        this.version = version;
        this.sequence_number = sequence_number;
        this.state_info = state_info;
        this.serial_number = serial_number;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.height = height;
        this.v_north = v_north;
        this.v_east = v_east;
        this.v_up = v_up;
        this.d_1_angle = d_1_angle;
        this.gps_time = gps_time;
        this.app_lat = app_lat;
        this.app_lon = app_lon;
        this.longitude_home = longitude_home;
        this.latitude_home = latitude_home;
        this.device_type = device_type;
        this.uuid_len = uuid_len;
        this.uuid = uuid;
        this.crc_packet = crc_packet;
        this.crc_calculated = crc_calculated;
        this.receiverDeviceId = receiverDeviceId;
    }

    public int getPkt_len() {
        return pkt_len;
    }

    public void setPkt_len(int pkt_len) {
        this.pkt_len = pkt_len;
    }

    public int getUnk() {
        return unk;
    }

    public void setUnk(int unk) {
        this.unk = unk;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSequence_number() {
        return sequence_number;
    }

    public void setSequence_number(int sequence_number) {
        this.sequence_number = sequence_number;
    }

    public int getState_info() {
        return state_info;
    }

    public void setState_info(int state_info) {
        this.state_info = state_info;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getV_north() {
        return v_north;
    }

    public void setV_north(int v_north) {
        this.v_north = v_north;
    }

    public int getV_east() {
        return v_east;
    }

    public void setV_east(int v_east) {
        this.v_east = v_east;
    }

    public int getV_up() {
        return v_up;
    }

    public void setV_up(int v_up) {
        this.v_up = v_up;
    }

    public int getD_1_angle() {
        return d_1_angle;
    }

    public void setD_1_angle(int d_1_angle) {
        this.d_1_angle = d_1_angle;
    }

    public long getGps_time() {
        return gps_time;
    }

    public void setGps_time(long gps_time) {
        this.gps_time = gps_time;
    }

    public double getApp_lat() {
        return app_lat;
    }

    public void setApp_lat(double app_lat) {
        this.app_lat = app_lat;
    }

    public double getApp_lon() {
        return app_lon;
    }

    public void setApp_lon(double app_lon) {
        this.app_lon = app_lon;
    }

    public double getLongitude_home() {
        return longitude_home;
    }

    public void setLongitude_home(double longitude_home) {
        this.longitude_home = longitude_home;
    }

    public double getLatitude_home() {
        return latitude_home;
    }

    public void setLatitude_home(double latitude_home) {
        this.latitude_home = latitude_home;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public int getUuid_len() {
        return uuid_len;
    }

    public void setUuid_len(int uuid_len) {
        this.uuid_len = uuid_len;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCrc_packet() {
        return crc_packet;
    }

    public void setCrc_packet(String crc_packet) {
        this.crc_packet = crc_packet;
    }

    public String getCrc_calculated() {
        return crc_calculated;
    }

    public void setCrc_calculated(String crc_calculated) {
        this.crc_calculated = crc_calculated;
    }

    public int getReceiverDeviceId() {
        return receiverDeviceId;
    }

    @Override
    public String toString() {
        return "DroneData{" +
                "pkt_len=" + pkt_len +
                ", unk=" + unk +
                ", version=" + version +
                ", sequence_number=" + sequence_number +
                ", state_info=" + state_info +
                ", serial_number='" + serial_number + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", height=" + height +
                ", v_north=" + v_north +
                ", v_east=" + v_east +
                ", v_up=" + v_up +
                ", d_1_angle=" + d_1_angle +
                ", gps_time=" + gps_time +
                ", app_lat=" + app_lat +
                ", app_lon=" + app_lon +
                ", longitude_home=" + longitude_home +
                ", latitude_home=" + latitude_home +
                ", device_type='" + device_type + '\'' +
                ", uuid_len=" + uuid_len +
                ", uuid='" + uuid + '\'' +
                ", crc_packet='" + crc_packet + '\'' +
                ", crc_calculated='" + crc_calculated + '\'' +
                '}';
    }
}