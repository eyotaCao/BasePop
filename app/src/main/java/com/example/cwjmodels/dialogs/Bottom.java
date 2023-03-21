package com.example.cwjmodels.dialogs;

import android.app.Activity;

import com.example.basepop.BasePopBottom;
import com.example.cwjmodels.R;

public class Bottom extends BasePopBottom {
    public Bottom(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_bottom;
    }
}
