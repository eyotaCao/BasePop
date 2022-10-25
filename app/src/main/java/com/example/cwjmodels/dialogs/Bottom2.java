package com.example.cwjmodels.dialogs;

import android.app.Activity;

import com.example.basepop.basepop.base.BasePopBottom;
import com.example.basepop.basepop.base.BasePopBottomFlex;
import com.example.cwjmodels.R;

public class Bottom2 extends BasePopBottomFlex {
    public Bottom2(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_bottom2;
    }
}
