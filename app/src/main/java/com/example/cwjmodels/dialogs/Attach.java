package com.example.cwjmodels.dialogs;

import android.app.Activity;

import com.example.basepop.BasePopAttach;
import com.example.cwjmodels.R;

public class Attach extends BasePopAttach {
    public Attach(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_attch;
    }
}
