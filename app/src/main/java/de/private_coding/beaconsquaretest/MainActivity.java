package de.private_coding.beaconsquaretest;

import android.app.DialogFragment;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;

import de.private_coding.beaconsquaretest.fragment.SizeDialogFragment;
import de.private_coding.beaconsquaretest.layout.CustomTable;
import de.private_coding.beaconsquaretest.listener.BeaconListener;
import de.private_coding.beaconsquaretest.receiver.BleReceiver;
import de.private_coding.beaconsquaretest.receiver.ContentReceiver;

public class MainActivity extends AppCompatActivity implements SizeDialogFragment.SizeFragmentListener{

    private static final String LOGGER = MainActivity.class.getSimpleName();
    private String bleIntentFilter;
    private String contentIntentFilter;

    private OnyxBeaconManager mBeaconManger;

    private ContentReceiver mContentReceiver;
    private BleReceiver mBleReceiver;
    private boolean bleRegistered;
    private boolean receiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show dialog for dimensions
        DialogFragment dialog = new SizeDialogFragment();
        dialog.show(getFragmentManager(), "SizeDialogFragment");

        // get BeaconManger instance and check for Bluetooth
        mBeaconManger = OnyxBeaconApplication.getOnyxBeaconManager(this);
        mBeaconManger.initSDK(AuthenticationMode.CLIENT_SECRET_BASED);
        if (!mBeaconManger.isBluetoothAvailable()) {
            mBeaconManger.enableBluetooth();
        }

        // get BeaconListener
        BeaconListener beaconListener = BeaconListener.getInstance();

        // Enable scanning for beacons
        mBeaconManger.setForegroundMode(true);
        mBeaconManger.setAPIContentEnabled(true);

        // Register for Onyx content
        mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setOnyxBeaconListener(beaconListener);

        // Register for BLE events
        mBleReceiver = BleReceiver.getInstance();

        // Register receivers
        bleIntentFilter = getPackageName() + ".scan";
        registerReceiver(mBleReceiver, new IntentFilter(bleIntentFilter));
        bleRegistered = true;

        contentIntentFilter = getPackageName() + ".content";
        registerReceiver(mContentReceiver, new IntentFilter(contentIntentFilter));
        receiverRegistered = true;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set scanner in background mode (power saving)
        mBeaconManger.setForegroundMode(false);
        // Unregister BLE receiver
        if (bleRegistered) {
            unregisterReceiver(mBleReceiver);
            bleRegistered = false;
        }

        // Unregister content receiver
        if (receiverRegistered) {
            unregisterReceiver(mContentReceiver);
            receiverRegistered = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register receivers again
        if (mBleReceiver == null) mBleReceiver = BleReceiver.getInstance();
        registerReceiver(mBleReceiver, new IntentFilter(bleIntentFilter));
        bleRegistered = true;

        if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();
        registerReceiver(mContentReceiver, new IntentFilter(contentIntentFilter));
        receiverRegistered = true;

        // check for Bluetooth
        if (mBeaconManger.isBluetoothAvailable()) {
            mBeaconManger.setForegroundMode(true);
        } else {
            Toast.makeText(this, "Please turn on bluetooth", Toast.LENGTH_LONG).show();
            mBeaconManger.enableBluetooth();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Methods for FragmentListener
    @Override
    public void onDialogPositiveClick(SizeDialogFragment dialog, int height, int width) {
        // CustomTable
        CustomTable table = new CustomTable(this, height, width);
        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        rootLayout.addView(table);
    }

    @Override
    public void onDialogNegativeClick(SizeDialogFragment dialog) {
        // CustomTable
        CustomTable table = new CustomTable(this, 4, 4);
        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        rootLayout.addView(table);
    }
}
