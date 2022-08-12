package com.example.basepop.basepop.base;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.example.basepop.basepop.base.utils.PxTool;

public class SoftUtils {

    public static void addSoftListener(Activity activity, OnSoftChangeListener listener){
        FrameLayout content = (FrameLayout)activity.findViewById(android.R.id.content);
        View mChildOfContent = content.getChildAt(0);
        Rect rect = new Rect();

        final int heightScreen = PxTool.getWindowHeight();
        final boolean[] isShow = {false};
        final int[] heightBefor = {0};


        //界面出现变动都会调用这个监听事件
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

                int heightC=rect.bottom;
                if (heightScreen>0&&heightC>0){
                    if (heightScreen>heightC){
                        if (heightC!= heightBefor[0]&&heightBefor[0]>0){ //修复部分机型数字/字母软键盘高度不一致
                            isShow[0]=false;
                        }
                        if (!isShow[0]){
                            listener.onChange(heightScreen-heightC,true);
                        }
                        isShow[0] =true;
                    }else {
                        if (isShow[0]){
                            listener.onChange(heightScreen-heightC,false);
                        }
                        isShow[0]=false;
                    }
                }
                heightBefor[0] =rect.bottom;
            }
        });

    }

    public static void makeTime(){}

    public static interface OnSoftChangeListener{
        void onChange(int change,boolean isShow);
    }
}
