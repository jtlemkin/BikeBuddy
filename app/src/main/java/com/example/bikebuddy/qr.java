package com.example.bikebuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class qr extends AppCompatActivity {

    private TextView box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        box = findViewById(R.id.textView2);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
    }

    public void scanButton(View view) {
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intent = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intent != null) {
            if (intent.getContents() == null) {
                box.setText("Failure");
            } else {
                box.setText(intent.getContents());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
