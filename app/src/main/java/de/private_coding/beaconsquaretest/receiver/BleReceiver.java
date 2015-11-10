package de.private_coding.beaconsquaretest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyxbeacon.OnyxBeaconApplication;

/**
 * Created by Bartz, Tobias on 10.11.2015 at 17:08.
 */
public class BleReceiver extends BroadcastReceiver {

    private static final String LOGGER = BleReceiver.class.getSimpleName();

    private static BleReceiver sInstance;

    public BleReceiver() {
    }

    public static BleReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new BleReceiver();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String event = intent.getStringExtra(OnyxBeaconApplication.SCAN_EVENT);
        switch (event) {
            case OnyxBeaconApplication.EMPTY_SCANS:
                Log.d(LOGGER, "Emtpy Scan");
                break;
            case OnyxBeaconApplication.INVALID_RSSI:
                Log.d(LOGGER, "Invalid Scan");
                break;
        }
    }
}
