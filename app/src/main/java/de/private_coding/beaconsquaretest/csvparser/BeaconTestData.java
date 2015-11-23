package de.private_coding.beaconsquaretest.csvparser;

import java.util.Date;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 22:26.
 */
public class BeaconTestData {

    private int row;
    private int column;
    private int major;
    private int minor;
    private int rssi;
    private Date date;

    public BeaconTestData(int row, int column, int major, int minor, int rssi, long date) {
        this.row = row;
        this.column = column;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.date = new Date(date);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
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
