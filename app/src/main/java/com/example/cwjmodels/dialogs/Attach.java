package com.example.cwjmodels.dialogs;

import android.app.Activity;

import com.example.basepop.basepop.base.BasePopAttach;
import com.example.basepop.basepop.base.BasePopBottom;
import com.example.basepop.basepop.base.BasePopTop;
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
