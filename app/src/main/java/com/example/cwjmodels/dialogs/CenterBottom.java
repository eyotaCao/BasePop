package com.example.cwjmodels.dialogs;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.basepop.basepop.base.BasePop;
import com.example.basepop.basepop.base.BasePopCenter;
import com.example.cwjmodels.R;

public class CenterBottom extends BasePopCenter {
    public CenterBottom(@NonNull Activity context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_center_normal;
    }
}
