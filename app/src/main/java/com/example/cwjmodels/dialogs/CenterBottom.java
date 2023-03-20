package com.example.cwjmodels.dialogs;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.basepop.basepop.base.BasePopCenterBottom;
import com.example.cwjmodels.R;

public class CenterBottom extends BasePopCenterBottom {
    public CenterBottom(@NonNull Activity context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_center_normal;
    }
}
