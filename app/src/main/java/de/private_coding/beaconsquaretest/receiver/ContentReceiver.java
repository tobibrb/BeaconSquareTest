package de.private_coding.beaconsquaretest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;

/**
 * Created by Bartz, Tobias on 10.11.2015 at 17:10.
 */
public class ContentReceiver extends BroadcastReceiver {

    private static final String LOGGER = ContentReceiver.class.getSimpleName();
    private static ContentReceiver sInstance;
    private OnyxBeaconsListener mOnyxBeaconListener;

    public ContentReceiver() {
    }

    public static ContentReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new ContentReceiver();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public void setOnyxBeaconListener(OnyxBeaconsListener onyxBeaconListener) {
        this.mOnyxBeaconListener = onyxBeaconListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);
        switch (payloadType) {
            case OnyxBeaconApplication.BEACON_TYPE:
                ArrayList<IBeacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                if (mOnyxBeaconListener != null) {
                    mOnyxBeaconListener.didRangeBeaconsInRegion(beacons);
                } else {
                    // In background display notification
                }
                break;
            default:
                Log.d(LOGGER, "Got other Information then expected!");
                break;
        }
    }
}
