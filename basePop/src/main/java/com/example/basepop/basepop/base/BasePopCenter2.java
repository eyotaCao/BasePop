package com.example.basepop.basepop.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.R;
import com.example.basepop.basepop.base.utils.LoadingPopupView2;
import com.example.basepop.basepop.base.utils.PxTool;

//中心弹框  中心弹出动画
public abstract class BasePopCenter2 extends BasePop{
    protected int layout;
    protected BackgroudView mBaseView; //阴影背景
    protected ViewGroup mParent;
    protected View mContent;
    protected Container mContainer;
    protected Activity activity;
    protected boolean isShow=false,isCreate=false,isShowBg=true;
    protected boolean isShowing=false,isDismissing=false;
    protected boolean dismissTouchOutside=true,dismissOnBack=true;
    //contentAnimate
    float startScale = .75f;
    private int maxHeight=0;
    //private int needTop,screenHeight;
    private final int animationDuration = 350;
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final int startColor = Color.TRANSPARENT;
    private final boolean isZeroDuration = false;
    private boolean isClickThrough=false;
    private boolean isConScrollAble=false;
    private int shadowBgColor = Color.parseColor("#7F000000");
    private MyPopLis myPopLis;
    private LoadingPopupView2 loading;

    public BasePopCenter2(Activity activity){
        super(activity);
        this.activity =activity;
        setLayout(getImplLayoutId());
    }


    protected abstract int getImplLayoutId();

    public void setLayout(int layout){
        this.layout=layout;
    }

    protected void onCreate(){  //加入弹窗

        isCreate=true;
        mBase=new Backgroud(activity);
        mBase.setClickThrough(isClickThrough);
        mBase.setOnback(()->{
            if (myPopLis!=null){
                myPopLis.onBack();
            }
            if (dismissOnBack){
                dismissB();
            }
        });
        if (!isShowBg){
            shadowBgColor= R.color.transparent;
        }
        maxHeight=getMaxHeight();
        //初始高度
        int maxWidth= getMaxWidth();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                ,Gravity.CENTER));
        mContainer=new Container(activity,isConScrollAble);
        mContent= LayoutInflater.from(activity).inflate(layout,mContainer,false);
        try {
            FrameLayout.LayoutParams flp=(FrameLayout.LayoutParams) mContent.getLayoutParams();
            flp.gravity=Gravity.CENTER;
            mContainer.setLayoutParams(flp);
        }catch (Exception ignored){}
        FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity= Gravity.CENTER;
        mContainer.setLayoutParams(flp);
        //mContainer.setBackgroundColor(getResources().getColor(R.color.color2866FE));
        mContainer.setMaxHeight(maxHeight);
        mContainer.setMaxWidth(maxWidth);
        mContainer.addView(mContent);
        mBaseView=new BackgroudView(activity);
        mBaseView.setOnback(()->{
            if (dismissTouchOutside){
                dismissB();
            }
        });
        FrameLayout.LayoutParams flp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseView.setLayoutParams(flp2);
        mBase.addView(mBaseView);  //背景
        mBase.addView(mContainer);  //弹窗
        mParent =(FrameLayout) activity.getWindow().getDecorView();

        try {
            mParent.addView(mBase);
        }catch (Exception ignored){

        }

    }

    public void initAnimator() {
        mContainer.setAlpha(startScale);
        mContainer.setScaleX(startScale);
        mContainer.setScaleY(startScale);
    }

    public void animateShow() {
        if (myPopLis!=null){
            myPopLis.onShow();
        }
        mContainer.post(() -> mContainer.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(animationDuration)
                .setInterpolator(new OvershootInterpolator(1f))
//                .withLayer() 在部分6.0系统会引起crash
                .start());
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor,shadowBgColor );
        animator.addUpdateListener(animation -> {
            if (isShowBg){
                mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShow=true;
                isShowing=false;
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration?0:animationDuration).start();

    }

    public void animateDismiss() {

        if (myPopLis!=null){
            myPopLis.onDismiss();
        }
        mContainer.animate().scaleX(startScale).scaleY(startScale).alpha(0f).setDuration(animationDuration)
                .setInterpolator(new FastOutSlowInInterpolator())
//                .withLayer() 在部分6.0系统会引起crash
                .start();
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(animation -> {
            if (isShowBg){
                mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShow=false;
                isDismissing=false;
                try {
                    mParent.removeView(mBase);
                }catch (Exception ignored){}

            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration?0:animationDuration).start();
    }

    public <T extends View> T findViewById(int id){
        return mContent.findViewById(id);
    }


    //设置没有阴影的背景点击可穿透
    public BasePopCenter2 setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopCenter2 setMaxHeight(int max) {
        maxHeight=max;
        return this;
    }

    public BasePopCenter2 setShowBg(boolean isShowBg) {
        this.isShowBg=isShowBg;
        return this;
    }
    public BasePopCenter2 setDismissTouchOutside(boolean dismissTouchOutside) {
        this.dismissTouchOutside = dismissTouchOutside;
        return this;
    }

    public BasePopCenter2 setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources(){
        return mBase.getResources();
    }

    public BasePopCenter2 setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
        return this;
    }

    public boolean isShow(){
        return isShow;
    }

    public void beforeShow(){   //弹窗显示之前执行
        if (myPopLis!=null){
            myPopLis.beforeShow();
        }
        initAnimator();
    }

    public void beforeDismiss(){
        if (myPopLis!=null){
            myPopLis.beforeDismiss();
        }
    }
    public void showLoading() {
        try {
            if (loading==null){
                loading=new LoadingPopupView2(activity,"");
                ViewGroup.LayoutParams vlp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                loading.setLayoutParams(vlp);
                loading.setVisibility(View.GONE);
                mContainer.addView(loading);
            }
            if (loading.getVisibility()!=View.VISIBLE){
                loading.setShow();
            }
        }catch (Exception ignored){}
    }



    public void dismissLoading() {
        if (loading != null) {
            loading.dismiss();
        }
    }

    public void showB(){

        if (isShowing||isShow){
            if (isShow){
                dismissB();
            }
            return;
        }
        isShowing=true;
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
    public void dismissB(){
        if (isDismissing){
            return;
        }
        isDismissing=true;
        beforeDismiss();
        animateDismiss();
        onDismiss();
    }
    protected void onDismiss() {

    }

    public BasePopCenter2 setPopListener(MyPopLis myPopLis){
        this.myPopLis=myPopLis;
        return this;
    }

    public static class MyPopLis extends BasePop.MyPopLis {
    }
    //获取弹窗最大高度
    protected int getMaxHeight(){return 0;}
    //获取弹窗最大宽度
    protected int getMaxWidth(){return (int) (PxTool.getScreenWidth()*0.85);}



}
