package com.example.cwjmodels.dialogs;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.basepop.BasePopCenter;
import com.example.cwjmodels.R;

public class Center extends BasePopCenter {
    public Center(@NonNull Activity context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_center_normal;
    }
}
