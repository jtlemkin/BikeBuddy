package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.ParcelUuid;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static String [] registeredDevices = new String[10];
    private static int counter = 0;
    private static ParcelUuid currDevice = ParcelUuid.fromString("00000000-0000-0000-0000-000000000000");
    //private static ParcelUuid currDevice = ParcelUuid.fromString("19b10000-e8f2-537e-4f6c-d104768a1214");

    public final static String SHOULD_TOGGLE_ALARM = "com.example.bikebuddy.SHOULD_TOGGLE_ALARM";
    public final static String CONFIG_PASSWORD = "com.example.bikebuddy.CONFIG_PASSWORD";
    public final static String PREFERENCE_FILE_KEY = "com.example.bikebuddy.PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 10; i++) {
            registeredDevices[i] = "";
        }

        setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
    }

    public static ParcelUuid getCurrDevice() { return currDevice; }

    public static void setCurrDevice(String id) {
        currDevice = ParcelUuid.fromString(id);
    }

    public static int getCounter() {
        return counter;
    }

    public static void incCounter() {
        counter++;
        return;
    }

    public static String[] getRegisteredDevices() {
        return registeredDevices;
    }

    public static void addDevice(String id) {
        registeredDevices[getCounter()] = id;
        incCounter();
        return;
    }
}
