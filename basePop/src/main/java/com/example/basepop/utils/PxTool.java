package com.example.basepop.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;


/**
 * Created by cwj
 * QQ:957652774
 * 像素全局代理
 */

public class PxTool {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;
    public static float scale;
    public static int screenHeight;
    public static int screenWidth;

    @SuppressLint("PrivateApi")
    public static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }


    public static void initContext(Context context) {
        mContext = context;
        scale = mContext.getResources().getDisplayMetrics().density;
        int[] wh = getScreenSize(context);
        screenWidth = wh[0];
        screenHeight = wh[1];
    }

    public static int getWindowHeight(Activity activity) {
        return activity.getWindow().getDecorView().getMeasuredHeight();
    }

    /**
     * 判断是否显示了导航栏
     * (说明这里的context 一定要是activity的context 否则类型转换失败)
     *
     * @return 是否显示了导航栏
     */
    public static boolean isShowNavBar(Context context) {
        if (null == context) {
            return false;
        }
        /**
         * 获取应用区域高度
         */
        Rect outRect1 = new Rect();
        try {
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        }
        int activityHeight = outRect1.height();
        /**
         * 获取状态栏高度
         */
        int statuBarHeight = getStatusBarHeight(context);
        /**
         * 屏幕物理高度 减去 状态栏高度
         */
        int remainHeight = getRealHeight(context) - statuBarHeight;
        /**
         * 剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
         */
        if (activityHeight == remainHeight) {
            return false;
        } else {
            return true;
        }

    }

    public int getNavHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 获取真实屏幕高度
     */
    private static WindowManager wm;

    public static int getRealHeight(Context context) {
        if (null == wm) {
            wm = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }


    public static int dp2px(int dpVal) {
        return (int) (scale * dpVal + 0.5f);
    }


    public static int dpToPx(float value) {
        return (int) (scale * value + 0.5f);
    }


    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context)
            .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
            .deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey & !hasBackKey;
    }

    public static int getScreenHeight() {
        return (int) screenHeight;
    }

    public static int[] getWindowWidthAndHeight(Activity activity) {
        int[] wh = new int[2];
        ViewGroup mParent = (ViewGroup) activity.getWindow().getDecorView();
        wh[0] = mParent.getMeasuredWidth();
        wh[1] = mParent.getMeasuredHeight();
        return wh;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static void setMeasureMax(View view) {   //测量出最大尺寸
        view.measure(View.MeasureSpec.makeMeasureSpec(PxTool.dpToPx(1000), View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(PxTool.dpToPx(1000), View.MeasureSpec.AT_MOST));
    }


    public static int[] getScreenSize(Context context) {
        int[] size = new int[2];

        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        size[0] = metrics.widthPixels;
        size[1] = metrics.heightPixels;
        return size;
    }
}
