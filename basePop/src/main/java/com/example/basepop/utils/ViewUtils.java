package com.example.basepop.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ViewUtils {
    public static View getViewFromLayout(int layout, Context context) {
        return LayoutInflater.from(context).inflate(layout, null);
    }

    public static int[] getLocation(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    public static int getMaxHeight(View view) {   //获取view未显示前的高度
        view.measure(View.MeasureSpec.makeMeasureSpec(PxTool.dpToPx(2000), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(PxTool.dpToPx(2000), View.MeasureSpec.AT_MOST));
        return view.getMeasuredHeight();
    }


    public static int getNavigationHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }
}
