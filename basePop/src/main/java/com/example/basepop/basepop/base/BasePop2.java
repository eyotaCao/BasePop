package com.example.basepop.basepop.base;



import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.basepop.R;

/*
* author:cwj
* phone:18879224388
* 若不想全面屏可以继承这个类
* 在onCreate()里面执行serContentView(mBase);而不是直接加到父容器
* 执行 show() dismiss();
* */

public abstract class BasePop2 extends Dialog {
    protected Backgroud mBase;  //父容器

    public BasePop2(@NonNull Context context) {
        super(context, com.example.basepop.R.style._XPopup_TransparentDialog);

    }

    protected abstract void beforeShow();
    protected abstract void beforeDismiss();
    protected abstract void onDismiss();
    protected abstract int getImplLayoutId();
    public void animateShow() {
        show();
    }
    public void animateDismiss() {
        dismiss();
    }

    public static class MyPopLis{
        protected void beforeShow(){};
        protected void beforeDismiss(){};
        protected void onShow(){};
        protected void onDismiss(){};
        protected void onBack(){};
    }

    public void destroy(){

    }

    public Backgroud getBasePop() {
        return mBase;
    }
}
