package com.example.basepop.basepop.base.photoViewer;

import android.app.Activity;

import com.okk.myapplication.R;

public class ImageDialog extends BasePopImage{

    public ImageDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_image;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
