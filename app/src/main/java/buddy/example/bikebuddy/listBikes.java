package buddy.example.bikebuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import buddy.example.bikebuddy.R;

import java.util.ArrayList;

import static buddy.example.bikebuddy.MainActivity.changeName;
import static buddy.example.bikebuddy.MainActivity.getCounter;
import static buddy.example.bikebuddy.MainActivity.getNames;
import static buddy.example.bikebuddy.MainActivity.getRegisteredDevices;
import static buddy.example.bikebuddy.MainActivity.setCurrDevice;

class MyOnClickListener implements View.OnClickListener {
    private Context context;
    private String selectedBike;
    private String name;

    public MyOnClickListener(String sb, String n, Context c) {
        this.selectedBike = sb;
        this.name = n;
        this.context = c;
    }

    @Override
    public void onClick(View v)
    {
        final EditText taskEditText = new EditText(context);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Registered Bike");
        dialog.setMessage("You can select a bike to use or change the device's name.");
        dialog.setView(taskEditText);

        dialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCurrDevice(selectedBike);
                CharSequence text = name + " SELECTED";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        dialog.setNegativeButton("Change Name", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = String.valueOf(taskEditText.getText());
                changeName(selectedBike, newName);
                CharSequence text = "BIKE NAME CHANGED TO " + newName;
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
        dialog.create();
        dialog.show();
    }

};

public class listBikes extends Fragment {
    private View activity;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public listBikes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_bikes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getView();
        context = getContext();
        goBack();

        String[] bikes = getRegisteredDevices();
        String[] bikeNames = getNames();
        ArrayList<SettingItem> bikeList = new ArrayList<>();

        for (int i = 0; i < getCounter(); i ++) {
            MyOnClickListener bikeListener = new MyOnClickListener(bikes[i], bikeNames[i], context);
            bikeList.add(new SettingItem(R.drawable.ic_directions_bike_black_24dp, bikeNames[i], bikeListener));
        }

        mRecyclerView = activity.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mAdapter = new SettingAdapter(bikeList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void goBack() {
        Button backButton = activity.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }
}
