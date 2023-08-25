package com.example.basepop.base;

import static com.example.basepop.base.BasePopConstants.SHOW_STATE_DISMISS;
import static com.example.basepop.base.BasePopConstants.SHOW_STATE_DISMISSING;
import static com.example.basepop.base.BasePopConstants.SHOW_STATE_SHOW;
import static com.example.basepop.base.BasePopConstants.SHOW_STATE_SHOWING;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.basepop.utils.PxTool;

/**
 * 弹窗基类
 *
 * @since 2023-8-25
 */
public abstract class BasePop {
    protected Activity activity;  //依附的活动

    protected Background mBase;  //父容器

    protected int layout;  //弹窗内容布局文件

    protected View mContent;  //内容

    protected ViewGroup mParent; //Activity的布局

    protected static final int animationDuration = 280; //弹窗打开/关闭动画时长

    protected final int startColor = Color.TRANSPARENT;  //弹窗开始背景颜色

    protected int shadowBgColor = Color.parseColor("#7F000000");  //弹窗背景颜色

    protected boolean dismissTouchOutside = true;  //点击弹窗外是否关闭弹窗

    protected boolean dismissOnBack = true; //返回键是否关闭弹窗

    protected boolean isClickThrough = false;

    protected boolean isCreate = false;

    protected BasePopListener myPopListener; //弹窗行为监听

    protected int showState = 0; //弹窗显示状态   0未显示，1正在显示，2已显示 3正在消失 4.已消失

    protected int maxHeight = 0;  //弹窗最大高度

    public BasePop(@NonNull Activity activity) {
        this.activity = activity;
        setLayout(getImplLayoutId());
        if (PxTool.mContext == null) {
            PxTool.initContext(activity);
        }
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    /**
     * 打开弹窗
     */
    public void show() {
        if (showState == SHOW_STATE_SHOWING || showState == SHOW_STATE_SHOW) {
            if (showState == SHOW_STATE_SHOW) {
                dismiss();
            }
            return;
        }
        showState = SHOW_STATE_SHOWING;
        if (!isCreate) {
            create();
        } else {
            try {
                mParent.addView(mBase);
                mBase.init();
            } catch (Exception ignored) {
            }
        }
        if (myPopListener != null) {
            myPopListener.beforeShow();
        }
        beforeShow();
        animateShow();
        if (myPopListener != null) {
            myPopListener.onShow();
        }
    }

    /**
     * 关闭弹窗
     */
    public void dismiss() {
        if (showState == SHOW_STATE_DISMISSING || showState == SHOW_STATE_DISMISS) {
            return;
        }
        showState = SHOW_STATE_DISMISSING;
        if (myPopListener != null) {
            myPopListener.beforeDismiss();
        }
        beforeDismiss();
        animateDismiss();
        if (myPopListener != null) {
            myPopListener.onDismiss();
        }
    }

    public BasePop setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePop setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    public BasePop setPopListener(BasePopListener myPopListener) {
        this.myPopListener = myPopListener;
        return this;
    }

    public BasePop setDismissTouchOutside(boolean dismissTouchOutside) {
        this.dismissTouchOutside = dismissTouchOutside;
        return this;
    }

    public Background getBasePop() {
        return mBase;
    }

    public Activity getActivity() {
        return activity;
    }

    private void create() {
        maxHeight = getMaxHeight();
        mBase = new Background(activity, () -> {
            if (myPopListener != null) {
                myPopListener.onBack();
            }
            if (dismissOnBack) {
                dismiss();
            }
        });
        mBase.setClickThrough(isClickThrough);
        mParent = (FrameLayout) activity.getWindow().getDecorView();
        mParent.addView(mBase);
        isCreate = true;
        onCreate();
    }

    /**
     * 创建
     */
    protected abstract void onCreate();

    /**
     * 获取需要显示内容的布局文件
     */
    protected abstract int getImplLayoutId();

    /**
     * 显示之前回调
     */
    protected abstract void beforeShow();

    /**
     * 显示之后回调
     */
    protected abstract void animateShow();

    /**
     * 消失之前回调
     */
    protected abstract void beforeDismiss();

    /**
     * 消失回调
     */
    protected abstract void animateDismiss();

    /**
     * 最大高度
     */
    protected int getMaxHeight() {
        return 0;
    }
}
