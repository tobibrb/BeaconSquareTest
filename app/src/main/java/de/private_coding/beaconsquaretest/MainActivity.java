package de.private_coding.beaconsquaretest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;

import java.io.File;
import java.util.Map;

import de.private_coding.beaconsquaretest.csvparser.BeaconCsvParser;
import de.private_coding.beaconsquaretest.fragment.SettingsDialogFragment;
import de.private_coding.beaconsquaretest.fragment.SizeDialogFragment;
import de.private_coding.beaconsquaretest.layout.CustomTable;
import de.private_coding.beaconsquaretest.listener.BeaconListener;
import de.private_coding.beaconsquaretest.receiver.BleReceiver;
import de.private_coding.beaconsquaretest.receiver.ContentReceiver;

public class MainActivity extends AppCompatActivity implements SizeDialogFragment.SizeFragmentListener {

    private static final String LOGGER = MainActivity.class.getSimpleName();
    private String bleIntentFilter;
    private String contentIntentFilter;

    private OnyxBeaconManager mBeaconManger;

    private ContentReceiver mContentReceiver;
    private BleReceiver mBleReceiver;
    private boolean bleRegistered;
    private boolean receiverRegistered;
    private BeaconCsvParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get CSV Parser
        this.parser = BeaconCsvParser.getInstance();

        // Show dialog for dimensions
        DialogFragment dialog = SizeDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "SizeDialogFragment");

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
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CSV Scan");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "See attachment");
                File attachment = new File(BeaconCsvParser.getCsvFile());
                try {
                    Uri uri = Uri.fromFile(attachment);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "CSV File not found",Toast.LENGTH_LONG).show();
                }
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
            DialogFragment settingsDialog = SettingsDialogFragment.newInstance();

            settingsDialog.show(getSupportFragmentManager(), "Settings");
            return true;
        } else if (id == R.id.action_delete_all) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure?")
                    .setMessage("This will delete all data for this test case!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!parser.removeTestData()) {
                                Toast.makeText(MainActivity.this, "No file to delete.", Toast.LENGTH_LONG).show();
                            }
                            Map<String, ImageButton> imageButtonMap = CustomTable.getImageButtons();
                            for (String key : imageButtonMap.keySet()) {
                                imageButtonMap.get(key).setImageResource(R.drawable.red);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    // Methods for FragmentListener
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int height, int width) {

        if (height > 15 || width > 15) {
            Toast.makeText(this, "Size > 15 not supported!", Toast.LENGTH_LONG).show();
            DialogFragment newDialog = SizeDialogFragment.newInstance();
            newDialog.show(getSupportFragmentManager(), "SizeDialogFragment");
        } else if (width < height) {
            Toast.makeText(this, "Height must be >= width!", Toast.LENGTH_LONG).show();
            DialogFragment newDialog = SizeDialogFragment.newInstance();
            newDialog.show(getSupportFragmentManager(), "SizeDialogFragment");
        } else {
            // CustomTable
            BeaconCsvParser.setCsvFile(height, width);
            CustomTable table = new CustomTable(this, this, height, width);
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.content_main);
            //CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
            rootLayout.addView(table);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // CustomTable
        BeaconCsvParser.setCsvFile(4, 4);
        CustomTable table = new CustomTable(this, this, 4, 4);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.content_main);
        //CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        rootLayout.addView(table);
    }
}
