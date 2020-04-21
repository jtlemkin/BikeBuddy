package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/*
   TASKS:
   1. Implement bluetooth capabilities
    c. Implement app arming and disarming alarm
    d. Save user location when disconnected from bike
    e. Bluetooth connections in background
    f. Start bluetooth service with jobscheduler
   2. Make UI Responsive
    a. Button should change color, text, and lock image when pressed
    b. When out of range of device, no button, text stating whether alarm active or not
    c. Text should state whether bike in range or not
   3. Change map annotation to be white and contain bike emoji
 */


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    private TextView mConnectionHeader;
    private TextView mArmedText;
    private ToggleButton mArmButton;
    private SharedPreferences mPreferences;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = MainActivity.class.getSimpleName();
    public final static int REQUEST_ENABLE_BT = 1;
    public final static int ARMING_UNKNOWN = -1;

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnectionHeader.setText(R.string.bike_connected);
                mArmButton.setVisibility(View.VISIBLE);
                mArmButton.setChecked(mPreferences.getInt("isArmed", 0) == 1);
                mArmedText.setVisibility(View.GONE);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "Disconnected from bluetooth");
                mConnectionHeader.setText(R.string.bike_disconnected);
                mArmButton.setVisibility(View.GONE);
                mArmedText.setVisibility(View.VISIBLE);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                int isArmed = intent.getIntExtra("isArmed", ARMING_UNKNOWN);

                SharedPreferences.Editor editor = mPreferences.edit();

                if (isArmed == 0 || isArmed == 1) {
                    editor.putInt("isArmed", isArmed);
                    editor.apply();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMapView(savedInstanceState);

        mPreferences = getPreferences(Context.MODE_PRIVATE);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        mConnectionHeader = findViewById(R.id.connectionHeader);
        mArmButton = findViewById(R.id.armButton);
        mArmedText = findViewById(R.id.armedText);

        if (mPreferences.getInt("isArmed", 0) == 0) {
            mArmedText.setText(R.string.alarm_active);
        } else {
            mArmedText.setText(R.string.alarm_inactive);
        }

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
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
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
