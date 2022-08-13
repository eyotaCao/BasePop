package com.example.cwjmodels.dialogs;

import android.app.Activity;

import com.example.basepop.basepop.base.BasePopCenterEdit;
import com.example.cwjmodels.R;

public class CenterEdit extends BasePopCenterEdit {

    public CenterEdit(Activity activity) {
        super(activity);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_center_edit;
    }
}
