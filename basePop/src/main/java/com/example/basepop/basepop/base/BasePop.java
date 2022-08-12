package com.example.basepop.basepop.base;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.basepop.basepop.base.utils.PxTool;
/*
* author:cwj
* phone:18879224388
* */
public abstract class BasePop {
    protected Backgroud mBase;  //父容器

    public BasePop(@NonNull Context context) {
        if (PxTool.mContext==null){
            PxTool.initContext(context);
        }
    }

    protected abstract void beforeShow();
    protected abstract void beforeDismiss();
    protected abstract void onDismiss();
    protected abstract int getImplLayoutId();

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
