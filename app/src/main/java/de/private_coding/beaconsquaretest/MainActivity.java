package de.private_coding.beaconsquaretest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private RelativeLayout rootLayout;
    private boolean mailSent;

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_FINE_LOCATION = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get rootLayout for table

        rootLayout = (RelativeLayout) findViewById(R.id.content_main);

        // Get CSV Parser
        this.parser = BeaconCsvParser.getInstance();


        // Show dialog for dimensions
        DialogFragment dialog = SizeDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "SizeDialogFragment");


        // Check for write permission on external storage (Android 6.0 only)
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            // init Onyx SDK if permission is granted
            initOnyxSdk();
        }

        // Check for location permission (Android 6.0 only)
        hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }

        // get BeaconListener
        BeaconListener beaconListener = BeaconListener.getInstance();

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
                if (attachment.exists()) {
                    try {
                        Uri uri = Uri.fromFile(attachment);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
                        mailSent = true;
                    } catch (NullPointerException e) {
                        Log.e(LOGGER, "Failed with Exception: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "CSV File not found", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set scanner in background mode (power saving)
        if (mBeaconManger != null) {
            mBeaconManger.setForegroundMode(false);
        }
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
        if (mBeaconManger != null) {
            if (mBeaconManger.isBluetoothAvailable()) {
                mBeaconManger.setForegroundMode(true);
            } else {
                Toast.makeText(this, "Please turn on bluetooth", Toast.LENGTH_LONG).show();
                mBeaconManger.enableBluetooth();
            }
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
            return true;
        } else if (id == R.id.action_new_test) {
            if (!mailSent) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?")
                        .setMessage("It seems that your last test was not sent per mail! Create new test anyway?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rootLayout.removeAllViews();
                                DialogFragment newDialog = SizeDialogFragment.newInstance();
                                newDialog.show(getSupportFragmentManager(), "SizeDialogFragment");
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
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?")
                        .setMessage("Your test data is saved as CSV file on your external storage. Create new test?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rootLayout.removeAllViews();
                                DialogFragment newDialog = SizeDialogFragment.newInstance();
                                newDialog.show(getSupportFragmentManager(), "SizeDialogFragment");
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission is granted init Onyx SDK
                    initOnyxSdk();
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initOnyxSdk() {
        // get BeaconManger instance and check for Bluetooth
        mBeaconManger = OnyxBeaconApplication.getOnyxBeaconManager(this);

        mBeaconManger.initSDK(AuthenticationMode.CLIENT_SECRET_BASED);
        if (!mBeaconManger.isBluetoothAvailable()) {
            mBeaconManger.enableBluetooth();
        }

        // Enable scanning for beacons
        mBeaconManger.setForegroundMode(true);
        mBeaconManger.setAPIContentEnabled(true);
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
            rootLayout.addView(table);
            mailSent = false;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // CustomTable
        BeaconCsvParser.setCsvFile(4, 4);
        CustomTable table = new CustomTable(this, this, 4, 4);
        rootLayout.addView(table);
        mailSent = false;
    }
}
