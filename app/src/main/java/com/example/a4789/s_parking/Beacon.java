package com.example.a4789.s_parking;

/**
 * Created by Reynold on 2015/12/26.
 */
public class Beacon {

    private boolean state;
    private String deviceName, mac, uuid;
    private int major, minor, rssi, totalRssi, count;
    private float avgRssi;
    private double distance;

    public Beacon(int minor) {
        this.state = false;
        this.minor = minor;
        this.totalRssi = 0;
        this.avgRssi = 0;
        this.count = 0;
    }

    public void setBeacon(String deviceName, String mac, String uuid, int major, int rssi, int txPower) {
        this.state = true;
        this.deviceName = deviceName;
        this.mac = mac;
        this.uuid = uuid;
        this.major = major;
        this.rssi = rssi;
        if(count < 10) {
            totalRssi += rssi;
            count += 1;
            avgRssi = (float)totalRssi / count;
            distance = calculateAccuracy(txPower, avgRssi);
        }
        else{
            totalRssi = rssi;
            count = 1;
            avgRssi = (float)totalRssi / count;
            distance = calculateAccuracy(txPower, avgRssi);
        }
    }

    public void allClear(){
        this.rssi = 0;
    }

    public boolean getState() {
        return state;
    }
    public String getName() {
        return this.deviceName;
    }
    public String getMac() {
        return this.mac;
    }
    public String getUuid() {
        return this.uuid;
    }
    public int getMajor() {
        return this.major;
    }
    public int getMinor() {
        return this.minor;
    }
    public int getRssi() {
        return rssi;
    }
    public float getAvgRssi() {
        return avgRssi;
    }

    private double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0)
        {
            return -1.0D;
        }

        double ratio = rssi * 1.0D / txPower;
        double rssiCorrection = 0.96D + Math.pow((double)Math.abs(rssi), 3.0D) % 10.0D / 150.0D;

        if (ratio < 1.0D)
        {

            return Math.pow(ratio, 9.98D) * rssiCorrection;
        }
        else
        {
            double accuracy = (0.89978D) * Math.pow(ratio, 7.71D) + 0.103D;
            return accuracy * rssiCorrection;
        }
    }

}


