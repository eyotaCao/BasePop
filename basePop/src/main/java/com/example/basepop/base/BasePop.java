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
 * @desc 所有弹窗基类
 * @author cwj
 */
public abstract class BasePop <T extends ViewGroup> {

    protected Activity activity;  //依附的活动

    protected Background mBase;  //父容器

    protected int layout;  //弹窗内容布局文件

    protected T mContainer; //内容父容器

    protected View mContent;  //内容

    protected BackgroundView mBaseView; //阴影背景

    protected ViewGroup mParent; //Activity的布局

    protected static final int animationDuration = 280; //弹窗打开/关闭动画时长

    protected int shadowBgColor = Color.parseColor("#7F000000");  //弹窗背景颜色

    protected boolean dismissTouchOutside = true;  //点击弹窗外是否关闭弹窗

    protected boolean dismissOnBack = true; //返回键是否关闭弹窗

    protected boolean isClickThrough = false;

    protected boolean isCreate = false;

    protected BasePopListener myPopListener; //弹窗行为监听

    protected int showState = 0; //弹窗显示状态   0未显示，1正在显示，2已显示 3正在消失 4.已消失

    protected int maxHeight=0;  //弹窗最大高度

    public BasePop(@NonNull Activity activity) {
        this.activity = activity;
        setLayout(getImplLayoutId());
        if (PxTool.mContext==null){
            PxTool.initContext(activity);
        }
    }

    protected void onCreate() {
        mBaseView=new BackgroundView(activity);
        maxHeight=getMaxHeight();
        mBase=new Background(activity,()->{
            if (myPopListener !=null){
                myPopListener.onBack();
            }
            if (dismissOnBack){
                dismiss();
            }
        });
        mBase.setClickThrough(isClickThrough);

        mBaseView=new BackgroundView(activity);
        mBaseView.setOnback(()->{
            if (dismissTouchOutside){
                dismiss();
            }
        });
        FrameLayout.LayoutParams flp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseView.setLayoutParams(flp2);

        mBase.addView(mBaseView);  //背景
        mBase.addView(mContainer);  //弹窗内容

        mParent =(FrameLayout) activity.getWindow().getDecorView();
        mParent.addView(mBase);
        isCreate=true;
    }



    public void setLayout(int layout){
        this.layout=layout;
    }

    /**
     * 需要子类实现，获取需要显示内容的布局文件
     */
    protected abstract int getImplLayoutId();

    /**
     * 打开弹窗
     */
    public void show(){
        if (showState == SHOW_STATE_SHOWING||showState == SHOW_STATE_SHOW){
            if (showState == SHOW_STATE_SHOW){
                dismiss();
            }
            return;
        }
        showState = SHOW_STATE_SHOWING;
        if (!isCreate){
            onCreate();
        }else {
            try {
                mParent.addView(mBase);
                mBase.init();
            }catch (Exception ignored){}
        }
        beforeShow();
        animateShow();
    }

    /**
     * 关闭弹窗
     */
    public void dismiss(){
        if (showState == SHOW_STATE_DISMISSING || showState == SHOW_STATE_DISMISS){
            return;
        }
        showState = SHOW_STATE_DISMISSING;
        beforeDismiss();
        animateDismiss();
    }

    protected abstract void beforeShow();
    protected abstract void animateShow();
    protected abstract void beforeDismiss();
    protected abstract void animateDismiss();


    protected int getMaxHeight() {
        return 0;
    };

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

    /**
     * 添加加载弹窗
     */
    public void addLoading(View loadingView) {
        try {
            if (mContainer!=null && loadingView!=null){
                mContainer.addView(loadingView);
            }
        }catch (Exception ignored){}
    }

}
