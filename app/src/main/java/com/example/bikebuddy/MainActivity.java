package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static String [] registeredDevices = new String[10];
    private static int counter = 0;

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
