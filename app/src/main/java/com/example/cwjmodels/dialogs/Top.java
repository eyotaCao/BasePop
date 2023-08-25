package com.example.cwjmodels.dialogs;

import android.app.Activity;

import com.example.basepop.BasePopTop;
import com.example.cwjmodels.R;

public class Top extends BasePopTop {
    public Top(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_top_attch;
    }
}
