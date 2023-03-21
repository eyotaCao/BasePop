package com.example.basepop.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Background extends FrameLayout {
    private onBack mOnBack;
    private boolean isClickThrough;

    public Background(@NonNull Context context, onBack onBack) {
        super(context);
        this.mOnBack = onBack;
        init();
        init2();
    }

    public Background(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        init2();
    }

    public Background(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        init2();
    }


    public void init2(){

    }
    public void init(){   //拦截返回事件


        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                mOnBack.onBack();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    //private int disDownCount=0,disUpCount=0;
    private float x, y,x1,y1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();

                if (isClickThrough){
                    return super.onTouchEvent(event);
                }else {
                    return true;
                }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (x1==event.getX()&&y1==event.getY()){
                    mOnBack.onBack();
                }

                if (isClickThrough){
                    return super.onTouchEvent(event);
                }else {
                    return true;
                }

        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                post(()-> ((Activity) getContext()).dispatchTouchEvent(event));


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(x-event.getX())<8&&Math.abs(y-event.getY())<8){
                    mOnBack.onBack();
                    post(()-> ((Activity) getContext()).dispatchTouchEvent(event));
                }


                break;
        }
        return true;

    }

    public void setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
    }


    public static interface onBack {
        void onBack();
    }
}
