package buddy.example.bikebuddy;

import android.view.View;

class SettingItem {
    private int mImageResource;
    private String mText;
    private View.OnClickListener mOnClickListener;

    SettingItem(int imageResource, String text, View.OnClickListener onClickListener) {
        mImageResource = imageResource;
        mText = text;
        mOnClickListener = onClickListener;
    }

    int getImageResource() {
        return mImageResource;
    }

    String getText() {
        return mText;
    }

    View.OnClickListener getmOnClickListener() {
        return mOnClickListener;
    }
}
