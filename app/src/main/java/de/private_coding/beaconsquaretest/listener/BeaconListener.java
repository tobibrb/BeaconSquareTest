package de.private_coding.beaconsquaretest.listener;

import android.util.Log;

import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeaconservice.IBeacon;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Bartz, Tobias on 10.11.2015 at 17:17.
 */
public class BeaconListener implements OnyxBeaconsListener {

    private static final String LOGGER = BeaconListener.class.getSimpleName();
    private static BeaconListener sInstance;
    private boolean capture;
    private Table table;

    public BeaconListener() {
        super();
        this.capture = false;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public static BeaconListener getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconListener();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    @Override
    public void didRangeBeaconsInRegion(List<IBeacon> list) {
        if (capture) {
            Log.d(LOGGER, "Starting capture mode");
            List<String[]> data = new ArrayList<>();
            String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/scan.csv";
            Date now = new Date();
            for (IBeacon beacon : list) {
                Log.d(LOGGER, String.format("Adding data for Beacon: %s, %s", beacon.getMajor(), beacon.getMinor()));
                    data.add(new String[] {
                            this.table.toString(),
                            String.valueOf(now.getTime()),
                            String.valueOf(beacon.getMajor()),
                            String.valueOf(beacon.getMinor()),
                            String.valueOf(beacon.getRssi())
                    });
            }
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(csv, true));
                writer.writeAll(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(int i, Exception e) {

    }
}
