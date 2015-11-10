package de.private_coding.beaconsquaretest;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;

import de.private_coding.beaconsquaretest.listener.BeaconListener;
import de.private_coding.beaconsquaretest.listener.Table;
import de.private_coding.beaconsquaretest.receiver.BleReceiver;
import de.private_coding.beaconsquaretest.receiver.ContentReceiver;
import de.private_coding.beaconsquaretest.task.CaptureTask;

public class MainActivity extends AppCompatActivity {

    private static final String LOGGER = MainActivity.class.getSimpleName();
    private String bleIntentFilter;
    private String contentIntentFilter;

    private OnyxBeaconManager mBeaconManger;

    private BeaconListener mBeaconListener;
    private ContentReceiver mContentReceiver;
    private BleReceiver mBleReceiver;
    private boolean bleRegistered;
    private boolean receiverRegistered;

    private ImageButton row0col0;
    private ImageButton row0col1;
    private ImageButton row0col2;
    private ImageButton row0col3;
    private ImageButton row1col0;
    private ImageButton row1col1;
    private ImageButton row1col2;
    private ImageButton row1col3;
    private ImageButton row2col0;
    private ImageButton row2col1;
    private ImageButton row2col2;
    private ImageButton row2col3;
    private ImageButton row3col0;
    private ImageButton row3col1;
    private ImageButton row3col2;
    private ImageButton row3col3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get BeaconManger instance and check for Bluetooth
        mBeaconManger = OnyxBeaconApplication.getOnyxBeaconManager(this);
        mBeaconManger.initSDK(AuthenticationMode.CLIENT_SECRET_BASED);
        if (!mBeaconManger.isBluetoothAvailable()) {
            mBeaconManger.enableBluetooth();
        }

        // get BeaconListener
        mBeaconListener = BeaconListener.getInstance();

        // Enable scanning for beacons
        mBeaconManger.setForegroundMode(true);
        mBeaconManger.setAPIContentEnabled(true);

        // Register for Onyx content
        mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setOnyxBeaconListener(mBeaconListener);

        // Register for BLE events
        mBleReceiver = BleReceiver.getInstance();

        // Register receivers
        bleIntentFilter = getPackageName() + ".scan";
        registerReceiver(mBleReceiver, new IntentFilter(bleIntentFilter));
        bleRegistered = true;

        contentIntentFilter = getPackageName() + ".content";
        registerReceiver(mContentReceiver, new IntentFilter(contentIntentFilter));
        receiverRegistered = true;

        // initalize ImageButtons
        row0col0 = (ImageButton) findViewById(R.id.row0_col0);
        row0col1 = (ImageButton) findViewById(R.id.row0_col1);
        row0col2 = (ImageButton) findViewById(R.id.row0_col2);
        row0col3 = (ImageButton) findViewById(R.id.row0_col3);
        row1col0 = (ImageButton) findViewById(R.id.row1_col0);
        row1col1 = (ImageButton) findViewById(R.id.row1_col1);
        row1col2 = (ImageButton) findViewById(R.id.row1_col2);
        row1col3 = (ImageButton) findViewById(R.id.row1_col3);
        row2col0 = (ImageButton) findViewById(R.id.row2_col0);
        row2col1 = (ImageButton) findViewById(R.id.row2_col1);
        row2col2 = (ImageButton) findViewById(R.id.row2_col2);
        row2col3 = (ImageButton) findViewById(R.id.row2_col3);
        row3col0 = (ImageButton) findViewById(R.id.row3_col0);
        row3col1 = (ImageButton) findViewById(R.id.row3_col1);
        row3col2 = (ImageButton) findViewById(R.id.row3_col2);
        row3col3 = (ImageButton) findViewById(R.id.row3_col3);

        initializeOnClickListener();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initializeOnClickListener() {
        // create OnClickListener for all ImageButtons
        row0col0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row0col0, Table.row0col0, mBeaconListener).execute();
            }
        });

        row0col1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row0col1, Table.row0col1, mBeaconListener).execute();
            }
        });

        row0col2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row0col2, Table.row0col2, mBeaconListener).execute();
            }
        });

        row0col3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row0col3, Table.row0col3, mBeaconListener).execute();
            }
        });

        row1col0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row1col0, Table.row1col0, mBeaconListener).execute();
            }
        });

        row1col1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row1col1, Table.row1col1, mBeaconListener).execute();
            }
        });

        row1col2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row1col2, Table.row1col2, mBeaconListener).execute();
            }
        });

        row1col3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row1col3, Table.row1col3, mBeaconListener).execute();
            }
        });

        row2col0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row2col0, Table.row2col0, mBeaconListener).execute();
            }
        });

        row2col1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row2col1, Table.row2col1, mBeaconListener).execute();
            }
        });

        row2col2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row2col2, Table.row2col2, mBeaconListener).execute();
            }
        });

        row2col3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row2col3, Table.row2col3, mBeaconListener).execute();
            }
        });

        row3col0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row3col0, Table.row3col0, mBeaconListener).execute();
            }
        });

        row3col1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row3col1, Table.row3col1, mBeaconListener).execute();
            }
        });

        row3col2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row3col2, Table.row3col2, mBeaconListener).execute();
            }
        });

        row3col3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CaptureTask(MainActivity.this, row3col3, Table.row3col3, mBeaconListener).execute();
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
}
