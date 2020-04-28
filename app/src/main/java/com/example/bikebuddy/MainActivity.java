package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

/*
   TASKS:
   1. Implement bluetooth capabilities
    e. Bluetooth connections in background
    f. Start bluetooth service with jobscheduler
   2. Change map annotation to be white and contain bike emoji
 */


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    private TextView mConnectionHeader;
    private TextView mArmedText;
    private TextView mBatteryText;
    private ToggleButton mArmButton;
    private SharedPreferences mPreferences;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = MainActivity.class.getSimpleName();
    public final static int REQUEST_ENABLE_BT = 1;
    public final static int ARMING_UNKNOWN = -1;
    public final static String SHOULD_TOGGLE_ALARM = "com.example.bikebuddy.SHOULD_TOGGLE_ALARM";
    public final static String PREFERENCE_FILE_KEY = "com.example.bikebuddy.PREFERENCE_FILE_KEY";

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "Connected to bluetooth");
                updateUIOnDeviceConnect();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "Disconnected from bluetooth");
                updateUIOnDeviceDisconnect();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "ACTION_DATA_AVAILABLE unimplemented");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        setupMapView(savedInstanceState);
        setupArmButton();
        mPreferences = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        setupBluetooth();
        mConnectionHeader = findViewById(R.id.connectionHeader);
        setupArmedText();
        mBatteryText = findViewById(R.id.batteryText);
        updateBatteryText();
    }

    private void updateUIOnDeviceConnect() {
        mConnectionHeader.setText(R.string.bike_connected);
        mArmButton.setVisibility(View.VISIBLE);
        mArmButton.setChecked(mPreferences.getInt("isArmed", 0) == 1);
        mArmedText.setVisibility(View.GONE);
    }

    private void updateUIOnDeviceDisconnect() {
        mConnectionHeader.setText(R.string.bike_disconnected);
        mArmButton.setVisibility(View.GONE);
        mArmedText.setVisibility(View.VISIBLE);
        updateBatteryText();
    }

    private void updateBatteryText() {
        int savedPercentage = mPreferences.getInt("batteryLife", -1);
        String percentageText;

        if (savedPercentage == -1) {
            percentageText = "Undefined";
        } else {
            percentageText = Integer.toString(savedPercentage);
        }

        String newLabel = getString(R.string.battery_life, percentageText);
        mBatteryText.setText(newLabel);
    }

    private void setupArmedText() {
        mArmedText = findViewById(R.id.armedText);

        if (mPreferences.getInt("isArmed", 0) == 0) {
            mArmedText.setText(R.string.alarm_active);
        } else {
            mArmedText.setText(R.string.alarm_inactive);
        }
    }

    private void setupBluetooth() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Intent intent = new Intent(this, BluetoothLeService.class);
            startService(intent);
        }
    }

    private void setupArmButton() {
        mArmButton = findViewById(R.id.armButton);
        mArmButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(SHOULD_TOGGLE_ALARM);
                intent.putExtra("isArmed", isChecked);
                sendBroadcast(intent);

                if (isChecked) {
                    buttonView.setBackgroundColor(Color.GRAY);
                    buttonView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_open_black_24dp, 0, 0, 0);
                } else {
                    buttonView.setBackgroundColor(Color.RED);
                    buttonView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_black_24dp, 0, 0, 0);
                }

            }
        });
    }

    private void setupMapView(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        double latitude = Double.longBitsToDouble(mPreferences.getLong("latitude", 0));
        double longitude = Double.longBitsToDouble(mPreferences.getLong("longitude", 0));

        //IconGenerator icg = new IconGenerator(this);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Last Seen Bike Location"));
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
