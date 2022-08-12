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
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.R;
import com.example.basepop.basepop.base.utils.PxTool;
import com.example.basepop.basepop.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

//中心弹框  中心弹出动画 有编辑框自动弹起
public abstract class BasePopCenterEdit extends BasePop{
    protected int layout;
    protected BackgroudView mBaseView; //阴影背景
    protected ViewGroup mParent;
    protected View mContent;
    protected Container mContainer;
    protected Activity activity;
    protected boolean isShow=false,isCreate=false,isShowBg=true,isAutoEdit=false;
    protected boolean isShowing=false,isDismissing=false;
    protected boolean dismissTouchOutside=true,dismissOnBack=true;
    //contentAnimate
    float startScale = .75f;
    private List<EditText> mEdits;
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

    public BasePopCenterEdit(Activity activity){
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
        flp.gravity= Gravity.CENTER;
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
        if (isAutoEdit){
            try {
                initAutoEdit();
            }catch (Exception ignored){}

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
    private void initAutoEdit(){
        mEdits=new ArrayList<>();
        traversalView(mContainer);
        int screenHei= PxTool.getWindowHeight();
        SoftUtils.addSoftListener(activity, (change, isShow) -> {
            if (isShow){
                if (mEdits.size()>0){
                    int []location= ViewUtils.getLocation(mContainer);

                    if (screenHei-location[1]<Math.abs(change)+mContent.getMeasuredHeight()){

                        ViewPropertyAnimator animator2 ;
                        animator2 = mContainer.animate().translationY(-(Math.abs(change)-screenHei+location[1]+mContainer.getMeasuredHeight()));
                        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                                .setDuration(200)
                                //  .withLayer()
                                .start();
                    }
                }
            }else {
                ViewPropertyAnimator animator2 ;
                animator2 = mContainer.animate().translationY(0);
                if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(200)
                        //  .withLayer()
                        .start();
                mBase.init();
            }
        });

    }
    private void traversalView(ViewGroup viewGroup){
        for (int i=0;i<viewGroup.getChildCount();i++){
            if (viewGroup.getChildAt(i) instanceof ViewGroup){
                traversalView((ViewGroup) viewGroup.getChildAt(i));
            }else if (viewGroup.getChildAt(i) instanceof EditText){
                mEdits.add((EditText) viewGroup.getChildAt(i));
            }
        }
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
    //自动弹起
    public BasePopCenterEdit setAutoEdit(boolean autoEdit) {
        isAutoEdit = autoEdit;
        return this;
    }


    //设置没有阴影的背景点击可穿透
    public BasePopCenterEdit setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopCenterEdit setMaxHeight(int max) {
        maxHeight=max;
        return this;
    }

    public BasePopCenterEdit setShowBg(boolean isShowBg) {
        this.isShowBg=isShowBg;
        return this;
    }
    public BasePopCenterEdit setDismissTouchOutside(boolean dismissTouchOutside) {
        this.dismissTouchOutside = dismissTouchOutside;
        return this;
    }

    public BasePopCenterEdit setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources(){
        return mBase.getResources();
    }

    public BasePopCenterEdit setConScrollAble(boolean conScrollAble) {
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
    public BasePopCenterEdit setPopListener(MyPopLis myPopLis){
        this.myPopLis=myPopLis;
        return this;
    }
    protected int getMaxWidth(){return (int) (PxTool.getScreenWidth()*0.85);}




    public static class MyPopLis extends BasePop.MyPopLis {
    }
}
