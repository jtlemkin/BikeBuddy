package buddy.example.bikebuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import buddy.example.bikebuddy.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class qr extends AppCompatActivity {
    private String dataRead = "";
    private TextView box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        box = findViewById(R.id.textView2);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

        goBack();
    }

    public void scanButton(View view) {
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.initiateScan();
    }

    public void manualEnter(View view) {
        final EditText taskEditText = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register New Bike");
        dialog.setMessage("Please enter your device's ID.");
        dialog.setView(taskEditText);

        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newID = String.valueOf(taskEditText.getText());
                boolean check = true;
                for (int i = 0; i < 4; i++) {
                    char c = newID.charAt(i);
                    if ((!Character.isDigit(c)) && (!Character.isLetter(c))) {
                        check = false;
                        break;
                    }
                }

                if (check) {
                    MainActivity.addDevice(newID);
                }
                finish();
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();

    }

    public void saveID(View view) {
        if (dataRead.equals("")) {
            return;
        } else {
            boolean check = true;
            for (int i = 0; i < 4; i++) {
                char c = dataRead.charAt(i);
                if ((!Character.isDigit(c)) && (!Character.isLetter(c))) {
                    check = false;
                    break;
                }
            }

            if (check) {
                MainActivity.addDevice(dataRead);
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intent = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intent != null) {
            if (intent.getContents() == null) {
                box.setText("Failure");
            } else {
                box.setText(intent.getContents());
                dataRead = intent.getContents();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goBack() {
        Button backButton = findViewById(R.id.back_button2);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}
