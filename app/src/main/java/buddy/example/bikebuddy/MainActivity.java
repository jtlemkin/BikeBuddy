package buddy.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import buddy.example.bikebuddy.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static String [] stolenBikes = new String[100];
    private static String [] registeredDevices = new String[10];
    private static String [] names = new String[10];
    private static int counter = 0;
    private static int batteryLife = 100;
    private static ParcelUuid currDevice = ParcelUuid.fromString("00000000-0000-0000-0000-000000000000");
    //private static ParcelUuid currDevice = ParcelUuid.fromString("19b10000-e8f2-537e-4f6c-d104768a1214");
    private static String begID = "19b10000-";
    private static String endID = "-537e-4f6c-d104768a1214";

    public final static String SHOULD_TOGGLE_ALARM = "com.example.bikebuddy.SHOULD_TOGGLE_ALARM";
    public final static String CONFIG_PASSWORD = "com.example.bikebuddy.CONFIG_PASSWORD";
    public final static String PREFERENCE_FILE_KEY = "com.example.bikebuddy.PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 10; i++) {
            registeredDevices[i] = "";
            names[i] = "";
        }
        for (int i = 0; i < 100; i++) {
            stolenBikes[i] = "";
        }

        setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
    }

    public static int getBatteryLife() {
        return batteryLife;
    }

    public static void setBatteryLife(int bl) {
        batteryLife = bl;
    }

    public static ParcelUuid getCurrDevice() { return currDevice; }

    public static void setCurrDevice(String id) {
        currDevice = ParcelUuid.fromString(id);
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int c) { counter = c; }

    public static void incCounter() {
        counter++;
        return;
    }

    public static String[] getRegisteredDevices() {
        return registeredDevices;
    }

    public static String[] getNames() { return names; }

    public static void changeName(String uuid, String newName) {
        int index = 0;
        for (int i = 0; i < getCounter(); i++) {
            if (registeredDevices[i].equals(uuid)) {
                index = i;
                break;
            }
        }
        names[index] = newName;
    }

    public static void addDevice(String id) {
        boolean redund = false;
        for (int i = 0; i < getCounter(); i++) {
            if (registeredDevices[i].equals(begID + id + endID)) {
                redund = true;
            }
        }

        if (redund) {
            return;
        }

        registeredDevices[getCounter()] = begID + id + endID;
        names[getCounter()] = begID + id + endID;
        incCounter();
        return;
    }

    public static String [] getStolenBikes() { return stolenBikes; }

    public static void addStolenBike(int i, String s) {
        stolenBikes[i] = s;
    }
}
