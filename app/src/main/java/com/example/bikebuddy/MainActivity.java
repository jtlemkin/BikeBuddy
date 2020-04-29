package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Objects;

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
