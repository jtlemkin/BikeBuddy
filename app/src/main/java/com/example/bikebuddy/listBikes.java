package com.example.bikebuddy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.bikebuddy.MainActivity.getCounter;
import static com.example.bikebuddy.MainActivity.getRegisteredDevices;
import static com.example.bikebuddy.MainActivity.setCurrDevice;

class MyOnClickListener implements View.OnClickListener {
    private Context context;
    private String selectedBike;

    public MyOnClickListener(String sb, Context c) {
        this.selectedBike = sb;
        this.context = c;
    }

    @Override
    public void onClick(View v)
    {
        setCurrDevice(selectedBike);
        Log.d(Main.class.getSimpleName(), selectedBike + " SELECTED");
        CharSequence text = selectedBike + " SELECTED";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
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
        ArrayList<SettingItem> bikeList = new ArrayList<>();

        for (int i = 0; i < getCounter(); i ++) {
            MyOnClickListener bikeListener = new MyOnClickListener(bikes[i], context);
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
