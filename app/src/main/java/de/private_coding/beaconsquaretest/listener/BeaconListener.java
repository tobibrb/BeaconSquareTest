package de.private_coding.beaconsquaretest.listener;

import android.util.Log;

import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeaconservice.IBeacon;

import java.util.Date;
import java.util.List;

import de.private_coding.beaconsquaretest.csvparser.BeaconCsvParser;

/**
 * Created by Bartz, Tobias on 10.11.2015 at 17:17.
 */
public class BeaconListener implements OnyxBeaconsListener {

    private static final String LOGGER = BeaconListener.class.getSimpleName();
    private static BeaconListener sInstance;
    private BeaconCsvParser parser;
    private boolean capture;
    private int row;
    private int column;

    public BeaconListener() {
        super();
        this.capture = false;
        this.parser = BeaconCsvParser.getInstance();
    }

    public static BeaconListener getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconListener();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public void setCapture(boolean capture) {
        if (capture) {
            Log.d(LOGGER, "Starting capture mode");
        } else {
            Log.d(LOGGER, "Stopping capture mode");
        }
        this.capture = capture;
    }

    public void setRowColumn(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public void didRangeBeaconsInRegion(List<IBeacon> list) {
        if (capture) {
            Date now = new Date();
            for (IBeacon beacon : list) {
                Log.d(LOGGER, String.format("Adding data for Beacon: %s, %s", beacon.getMajor(), beacon.getMinor()));
                this.parser.createTestData(
                        this.row,
                        this.column,
                        beacon.getMajor(),
                        beacon.getMinor(),
                        beacon.getRssi(),
                        now);
            }
        }
    }

    @Override
    public void onError(int i, Exception e) {

    }
}
