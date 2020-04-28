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


public class MainActivity extends AppCompatActivity {

    public final static String SHOULD_TOGGLE_ALARM = "com.example.bikebuddy.SHOULD_TOGGLE_ALARM";
    public final static String PREFERENCE_FILE_KEY = "com.example.bikebuddy.PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
    }

}
