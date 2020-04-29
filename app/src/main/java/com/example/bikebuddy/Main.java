package com.example.bikebuddy;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
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
    e. Bluetooth connections in background
    f. Start bluetooth service with jobscheduler
   2. Change map annotation to be white and contain bike emoji
 */

public class Main extends Fragment implements OnMapReadyCallback {
    private MapView mMapView;

    private TextView mConnectionHeader;
    private TextView mArmedText;
    private TextView mBatteryText;
    private ToggleButton mArmButton;

    private SharedPreferences mPreferences;

    private View activity;
    private Context context;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int ARMING_UNKNOWN = -1;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = Main.class.getSimpleName();

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

    public Main() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getView();
        context = getContext();

        setupMapView(savedInstanceState);
        setupArmButton();
        mPreferences = context.getSharedPreferences(MainActivity.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        setupBluetooth();
        mConnectionHeader = activity.findViewById(R.id.connectionHeader);
        setupArmedText();
        mBatteryText = activity.findViewById(R.id.batteryText);
        updateBatteryText();
        enableNavigationToSettings();
    }

    private void enableNavigationToSettings() {
        Button settingsButton = activity.findViewById(R.id.settings_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_main_to_settings);
            }
        });
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
        mArmedText = activity.findViewById(R.id.armedText);

        if (mPreferences.getInt("isArmed", 0) == 0) {
            mArmedText.setText(R.string.alarm_active);
        } else {
            mArmedText.setText(R.string.alarm_inactive);
        }
    }

    private void setupBluetooth() {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Intent intent = new Intent(context, BluetoothLeService.class);
            context.startService(intent);
        }
    }

    private void setupArmButton() {
        mArmButton = activity.findViewById(R.id.armButton);
        mArmButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.SHOULD_TOGGLE_ALARM);
                intent.putExtra("isArmed", isChecked);
                context.sendBroadcast(intent);

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
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = activity.findViewById(R.id.mapView);
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
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
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
