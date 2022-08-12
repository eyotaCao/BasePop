package com.example.basepop.basepop.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.R;
import com.example.basepop.basepop.base.utils.PxTool;
import com.example.basepop.basepop.base.utils.ViewUtils;


//依附于某个view弹窗
public abstract class BasePopAttach extends BasePop{
    protected int layout;
    protected BackgroudView mBaseView; //阴影背景
    protected ViewGroup mParent;
    protected View mContent,mAttachView;
    protected Container mContainer;
    protected Activity activity;
    protected boolean isShow=false,isCreate=false,isShowBg=true;
    protected boolean isShowing=false,isDismissing=false;
    protected boolean dismissTouchOutside=true,dismissOnBack=true;
    //contentAnimate
    float startScale = .75f;
    private int maxHeight=0,offsetX;
    private Animate animType= Animate.rightTop;
    //private int needTop,screenHeight;
    private static final int animationDuration = 350;
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final int startColor = Color.TRANSPARENT;
    private final boolean isZeroDuration = false;
    private boolean isClickThrough=false;
    private boolean isConScrollAble=false;
    private int shadowBgColor = Color.parseColor("#7F000000");
    private MyPopLis myPopLis;

    public enum Animate{  //动画开始位置
        center,rightTop,rightBottom,leftTop,leftBottom,auto
    }

    public BasePopAttach(Activity activity){
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
                dismiss();
            }
        });
        if (!isShowBg){
            shadowBgColor= R.color.transparent;
        }
        maxHeight=getMaxHeight();
        //初始高度
        int maxWidth = getMaxWidth();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent= LayoutInflater.from(activity).inflate(layout,mBase,false);
        mContainer=new Container(activity,isConScrollAble);

        FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.setMaxWidth(maxWidth);
        mContainer.addView(mContent);
        mBaseView=new BackgroudView(activity);
        mBaseView.setOnback(()->{
            if (dismissTouchOutside){
                dismiss();
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
        applyPivot();
    }

    private void applyPivot() {
        if (mAttachView==null){
            return;
        }
        int []loc= ViewUtils.getLocation(mAttachView);

        int screenH= PxTool.getWindowHeight();
        int screenW=PxTool.getScreenWidth();
        int width=mAttachView.getWidth();
        int height=mAttachView.getHeight();
        ViewUtils.getMaxHeight(mContainer);
        int contentWidth=mContainer.getMeasuredWidth();
        int widthC=mContainer.getMeasuredWidth();
        int heightC=mContainer.getMeasuredHeight();
        switch (animType) {
            case leftTop:
                if (loc[1]+height+heightC>screenH){
                    animType= Animate.leftBottom;
                }
                break;
            case rightTop:
                if (loc[1]+height+heightC>screenH){
                    animType= Animate.rightBottom;
                }
                break;
            case leftBottom:
                if (loc[1]<screenH){
                    animType= Animate.leftTop;
                }
                break;
            case rightBottom:
                if (loc[1]>screenH){
                    animType= Animate.rightTop;
                }
                break;

        }
        switch (animType) {
            case leftTop:
                if (loc[0]+contentWidth+offsetX>screenW){
                    animType= Animate.rightTop;
                }
                break;
            case rightTop:
                if (loc[0]+offsetX<contentWidth){
                    animType= Animate.leftTop;
                }
                break;
            case leftBottom:
                if (loc[0]+contentWidth+offsetX>screenW){
                    animType= Animate.rightBottom;
                }
                break;
            case rightBottom:
                if (loc[0]+offsetX<width){
                    animType= Animate.leftBottom;
                }
                break;

        }
        switch (animType) {
            case center:
                mContainer.setPivotX(mContainer.getMeasuredWidth() / 2f);
                mContainer.setPivotY(mContainer.getMeasuredHeight() / 2f);
                break;
            case leftTop:
                mContainer.setTranslationX(loc[0]+width+offsetX);
                mContainer.setTranslationY(loc[1]+height);
                mContainer.setPivotX(0);
                mContainer.setPivotY(0);
                break;
            case rightTop:
                mContainer.setTranslationX(loc[0]-widthC+offsetX);
                mContainer.setTranslationY(loc[1]+height);
                mContainer.setPivotX(mContainer.getMeasuredWidth());
                mContainer.setPivotY(0f);
                break;
            case leftBottom:
                mContainer.setTranslationX(loc[0]+width+offsetX);
                mContainer.setTranslationY(loc[1]-heightC);
                mContainer.setPivotX(0f);
                mContainer.setPivotY(mContainer.getMeasuredHeight());
                break;
            case rightBottom:
                mContainer.setTranslationX(loc[0]-widthC+offsetX);
                mContainer.setTranslationY(loc[1]-heightC);
                mContainer.setPivotX(mContainer.getMeasuredWidth());
                mContainer.setPivotY(mContainer.getMeasuredHeight());
                break;
            case auto:
                break;

        }

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
    public BasePopAttach setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopAttach setMaxHeight(int max) {
        maxHeight=max;
        return this;
    }

    public BasePopAttach setShowBg(boolean isShowBg) {
        this.isShowBg=isShowBg;
        return this;
    }
    public BasePopAttach setDismissTouchOutside(boolean dismissTouchOutside) {
        this.dismissTouchOutside = dismissTouchOutside;
        return this;
    }

    public BasePopAttach setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources(){
        return mBase.getResources();
    }

    public BasePopAttach setConScrollAble(boolean conScrollAble) {
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

    public BasePopAttach setAttachView(View mAttachView) {
        this.mAttachView = mAttachView;
        return this;
    }

    public BasePopAttach setAnimType(Animate animType) {
        this.animType = animType;
        return this;
    }

    public BasePopAttach setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public void show(){
        if (isShowing||isShow){
            if (isShow){
                dismiss();
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
    public void dismiss(){
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
    protected int getMaxHeight(){
        return 0;
    }
    public BasePopAttach setPopListener(MyPopLis myPopLis){
        this.myPopLis=myPopLis;
        return this;
    }

    protected int getMaxWidth(){return 0;}



    public static class MyPopLis extends BasePop.MyPopLis {
    }
}
