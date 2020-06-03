package buddy.example.bikebuddy;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static buddy.example.bikebuddy.MainActivity.getCurrDevice;
import static buddy.example.bikebuddy.MainActivity.setBatteryLife;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic alarmCharacteristic;
    private BluetoothGattCharacteristic batteryLifeCharacteristic;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean mScanning;
    private int connectionState;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences sharedPreferences;
    private boolean arm = false;

    // 10 second scan period
    private static final long SCAN_PERIOD = 10000;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bikebuddy.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bikebuddy.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bikebuddy.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bikebuddy.ACTION_DATA_AVAILABLE";

    //public final static ParcelUuid deviceUUID = ParcelUuid.fromString("19b10000-e8f2-537e-4f6c-d104768a1214");
    public static ParcelUuid deviceUUID = ParcelUuid.fromString("00000000-0000-0000-0000-000000000000");
    //public final static ParcelUuid alarmUUID = ParcelUuid.fromString("19b10001-e8f2-537e-4f6c-d104768a1214");
    //public final static ParcelUuid batteryLifeUUID = ParcelUuid.fromString("19b10002-e8f2-537e-4f6c-d104768a1214");

    private final BroadcastReceiver shouldWriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MainActivity.SHOULD_TOGGLE_ALARM.equals(action)) {

                int armed = -1;
                if (!arm) {
                    armed = 1;
                    arm = true;
                } else {
                    armed = 0;
                    arm = false;
                }
                int isArmed = intent.getIntExtra("isArmed", armed);
                writeToAlarmCharacteristic(isArmed);
            } else if (MainActivity.CONFIG_PASSWORD.equals(action)) {
                int config = intent.getIntExtra("configPass", 2);
                writeToAlarmCharacteristic(config);
            }


        }
    };

    private void registerMyReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MainActivity.SHOULD_TOGGLE_ALARM);
            intentFilter.addAction(MainActivity.CONFIG_PASSWORD);
            registerReceiver(shouldWriteReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void writeToAlarmCharacteristic(int isArmed) {
        if ((alarmCharacteristic != null) && (isArmed != -1)) {
            alarmCharacteristic.setValue(isArmed, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            boolean status = bluetoothGatt.writeCharacteristic(alarmCharacteristic);
        } else {
            if (isArmed == -1){
                Log.d(TAG, "Its not armed");
            } else {
                Log.d(TAG, "Characteristic is null");
            }
        }
    }

    private boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (bluetoothGatt != null) {
            return bluetoothGatt.readCharacteristic(characteristic);

        }
        return false;
    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences(MainActivity.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        Log.d(TAG, "Starting bluetooth service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        registerMyReceiver();
        scanLeDevice(true);

        return super.onStartCommand(intent, flags, startId);
    }

    private void scanLeDevice(final boolean enable) {
        //Stops bluetooth scanning after scan period
        if (enable) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    bluetoothLeScanner.flushPendingScanResults(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            List<ScanFilter> filters = configureScanFilters();
            ScanSettings scanSettings = configureScanSettings();
            bluetoothLeScanner.startScan(filters, scanSettings, leScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.startScan(leScanCallback);
        }
    }

    List<ScanFilter> configureScanFilters() {
        List<ScanFilter> filters = new ArrayList<>();
        //Use this to maybe better filter devices
        deviceUUID = getCurrDevice();
        ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(deviceUUID).build();
        filters.add(scanFilter);
        return filters;
    }

    ScanSettings configureScanSettings() {
        //This can also maybe be better
        return new ScanSettings.Builder().build();
    }

    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                bluetoothGatt = result.getDevice().connectGatt(getApplicationContext(),
                        false, gattCallback);
            }

        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                Log.d(TAG, "Connected to GATT Server");
                Log.d(TAG, "Attempting to start service discovery:"
                        + bluetoothGatt.discoverServices());
                broadcastUpdate(intentAction);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                updateBatteryLifePreference();
                updateLocationPreference();
                broadcastUpdate(intentAction);
            }

            super.onConnectionStateChange(gatt, status, newState);
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(deviceUUID.getUuid());
                alarmCharacteristic = service.getCharacteristics().get(0);
                batteryLifeCharacteristic = service.getCharacteristics().get(1);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                readCharacteristic(batteryLifeCharacteristic);
            } else {
                Log.w(TAG, "onServiceDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                updateBatteryLifePreference();
                updateLocationPreference();
            }
        }
    };

    private void updateBatteryLifePreference() {
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        int batteryLife = -1;

        //readCharacteristic(batteryLifeCharacteristic);
        if (batteryLifeCharacteristic == null) {
            Log.d(TAG, "Characteristic is null");
        }

        final byte [] data = batteryLifeCharacteristic.getValue();
        if (data == null) {
            Log.d(TAG, "fail");
            return;
        } else {
            batteryLife = batteryLifeCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            Log.d(TAG, "BL = " + batteryLife);
        }

        //editor.putInt("batteryLife", batteryLife);
        //editor.apply();
        setBatteryLife(batteryLife);
        broadcastUpdate("batteryLife");
    }

    private void updateArmingPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int isArmed = alarmCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        if (isArmed == 0 || isArmed == 1) {
            editor.putInt("isArmed", isArmed);
            editor.apply();
        }
    }

    private void updateLocationPreference() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("longitude", Double.doubleToRawLongBits(longitude));
                        editor.putLong("latitude", Double.doubleToRawLongBits(latitude));
                        editor.apply();
                    }
                }
            });
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
