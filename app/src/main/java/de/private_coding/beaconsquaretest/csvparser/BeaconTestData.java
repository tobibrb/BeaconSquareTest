package de.private_coding.beaconsquaretest.csvparser;

import java.util.Date;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 22:26.
 */
public class BeaconTestData {

    private String rowColumn;
    private int major;
    private int minor;
    private int rssi;
    private Date date;

    public BeaconTestData(String rowColumn, int major, int minor, int rssi, long date) {
        this.rowColumn = rowColumn;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.date = new Date(date);
    }

    public String getRowColumn() {
        return rowColumn;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRssi() {
        return rssi;
    }

    public Date getDate() {
        return date;
    }
}
