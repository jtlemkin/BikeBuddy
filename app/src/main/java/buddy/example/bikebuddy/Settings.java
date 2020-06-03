package buddy.example.bikebuddy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import buddy.example.bikebuddy.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {
    private View activity;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getView();
        context = getContext();

        enableNavigationToMain();
        ArrayList<SettingItem> settingList = new ArrayList<>();

        /*View.OnClickListener passwordClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = "ENTER 5 MOVEMENTS TO CONFIGURE PASSWORD";

                Intent intent = new Intent(MainActivity.CONFIG_PASSWORD);
                intent.putExtra("configPass", 2);
                context.sendBroadcast(intent);

                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        };*/

        View.OnClickListener registerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_settings_to_qr);
            }
        };

        View.OnClickListener listClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_settings_to_listBikes);
            }
        };

        View.OnClickListener reportClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_settings_to_report);
            }
        };

        //settingList.add(new SettingItem(R.drawable.ic_motion_24dp, "Set Password", passwordClickListener));
        settingList.add(new SettingItem(R.drawable.ic_directions_bike_black_24dp, "Register New Device", registerClickListener));
        settingList.add(new SettingItem(R.drawable.ic_menu_black_24dp, "My Bikes", listClickListener));
        settingList.add(new SettingItem(R.drawable.ic_report_24dp, "Report Stolen Bike", reportClickListener));


        mRecyclerView = activity.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mAdapter = new SettingAdapter(settingList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void enableNavigationToMain() {
        Button backButton = activity.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

}
