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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import buddy.example.bikebuddy.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static buddy.example.bikebuddy.MainActivity.getCounter;
import static buddy.example.bikebuddy.MainActivity.getRegisteredDevices;

class ReportListener implements View.OnClickListener {
    private Context context;
    private String selectedBike;

    public ReportListener(String sb, Context c) {
        this.selectedBike = sb;
        this.context = c;
    }

    @Override
    public void onClick(View v)
    {
        //write to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myID = database.getReference();

        final EditText taskEditText = new EditText(context);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Report Stolen Bike");
        dialog.setMessage("Please enter the description of the bike or any information about the robbery.");
        dialog.setView(taskEditText);

        dialog.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = String.valueOf(taskEditText.getText());
                myID.child("Stolen Bike UUIDs").child(selectedBike).setValue(description);

                Log.d(Main.class.getSimpleName(), selectedBike + " SELECTED");
                CharSequence text = selectedBike + " REPORTED AS STOLEN";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }

};

public class report extends Fragment {
    private View activity;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getView();
        context = getContext();
        goBack();

        String[] bikes = getRegisteredDevices();
        ArrayList<SettingItem> bikeList = new ArrayList<>();

        for (int i = 0; i < getCounter(); i ++) {
            ReportListener bikeListener = new ReportListener(bikes[i], context);
            bikeList.add(new SettingItem(R.drawable.ic_directions_bike_black_24dp, bikes[i], bikeListener));
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
