package buddy.example.bikebuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import buddy.example.bikebuddy.R;

import java.util.ArrayList;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    private ArrayList<SettingItem> mSettingItems;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        View mView;

        ViewHolder(View settingView) {
            super(settingView);
            mView = settingView;
            mImageView = settingView.findViewById(R.id.imageView);
            mTextView = settingView.findViewById(R.id.textView);
        }

        void setOnClickListener(View.OnClickListener onClickListener) {
            mView.setOnClickListener(onClickListener);
        }
    }

    SettingAdapter(ArrayList<SettingItem> settingItems) {
        mSettingItems = settingItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem currentItem = mSettingItems.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView.setText(currentItem.getText());
        holder.setOnClickListener(currentItem.getmOnClickListener());
    }

    @Override
    public int getItemCount() {
        return mSettingItems.size();
    }
}
