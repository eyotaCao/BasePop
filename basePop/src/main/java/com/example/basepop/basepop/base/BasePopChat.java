package com.example.basepop.basepop.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.example.basepop.basepop.base.utils.PxTool;
import com.example.basepop.basepop.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

//底部弹框 直接弹出
public abstract class BasePopChat extends BasePop{
    protected int layout;
    protected BackgroudView mBaseView; //阴影背景
    protected ViewGroup mParent;
    protected View mContent;
    protected ContainerBottom mContainer;
    protected Activity activity;
    protected boolean isShow=false,isCreate=false;
    protected boolean isShowing=false,isDismissing=false,isAutoEdit=false;
    //是否显示导航栏
    private boolean isShowNavi=false;
    private int l,t,r,b;
    private NestedScrollView mScroll2;
    private EditText mEditText;

    //contentAnimate
    private int  oldHeight,maxHeight=0;  //初始高度
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final int startColor = Color.TRANSPARENT;
    private final boolean isZeroDuration = false;
    private boolean isClickThrough=true;
    private boolean isConScrollAble=true;
    private final int shadowBgColor = Color.parseColor("#7F000000");
    private MyPopLis myPopLis;

    public BasePopChat(Activity activity){
        super(activity);
        this.activity =activity;
        setLayout(getImplLayoutId());
        getMaxHeight();
    }


    protected abstract int getImplLayoutId();

    public void setLayout(int layout){
        this.layout=layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(){  //加入弹窗

        isCreate=true;
        mBase=new Backgroud(activity);
        mBase.setClickThrough(isClickThrough);
        mBase.setOnback(this::dismiss);
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent= LayoutInflater.from(activity).inflate(layout,mBase,false);
        mContainer=new ContainerBottom(activity,isConScrollAble);
        mContainer.setOnback(this::dismiss2);
        FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity= Gravity.BOTTOM;
        try {
            int resourceId=getResources().getIdentifier("navigation_bar_height","dimen","android");
            int height = getResources().getDimensionPixelSize(resourceId);
            if (isShowNavi){
                FrameLayout.LayoutParams flpBa=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                flpBa.bottomMargin=height;
                mBase.setLayoutParams(flpBa);
            }else {
                mContent.setPadding(0,0,0,height);
            }
        }catch (Exception ignored){}
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.addView(mContent);
        mBaseView=new BackgroudView(activity);

        mBaseView.setOnback(this::dismiss);
        FrameLayout.LayoutParams flp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseView.setLayoutParams(flp2);
        mBase.addView(mBaseView);  //背景
        mBase.addView(mContainer);  //弹窗
        mParent =(FrameLayout) activity.getWindow().getDecorView();
        mParent.setOnTouchListener((v, event) -> false);
        mContainer.setOnScrollLis(percent -> {
            mBaseView.setBackgroundColor((Integer) argbEvaluator.evaluate(Math.abs(percent),startColor,shadowBgColor));
        });
        try {
            mParent.addView(mBase);


        }catch (Exception ignored){

        }

    }

    public void initAnimator() {
        oldHeight= ViewUtils.getMaxHeight(mContainer);

        mContent.setTranslationY(oldHeight);
    }

    public void animateShow() {

        if (myPopLis!=null){
            myPopLis.onShow();
        }
        initAutoEdit();
        initEdit();
        mContainer.setTranslationY(0);
        mContent.setTranslationY(0);
      /*  ViewPropertyAnimator animator2 ;
        animator2 = mContent.animate().translationY(0);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
              //  .withLayer()
                .start();*/

        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor,shadowBgColor );
        animator.addUpdateListener(animation -> mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShow=true;
                isShowing=false;
                mContainer.setTranslationY(0);
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration?0:animationDuration).start();
    }

    public void animateDismiss() {

        if (myPopLis!=null){
            myPopLis.onDismiss();
        }
        ViewPropertyAnimator animator2 ;
        animator2 = mContent.animate().translationY(oldHeight);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
                .withLayer()
                .start();

        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(animation -> mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShow=false;
                isDismissing=false;
                mParent.removeView(mBase);
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration?0:animationDuration).start();
    }

    public <T extends View> T findViewById(int id){
        return mContent.findViewById(id);
    }

    //设置没有阴影的背景点击可穿透
    public BasePopChat setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopChat setMaxHeight(int max) {
        maxHeight=max;
        return this;
    }

    protected Resources getResources(){
        return mBase.getResources();
    }

    public BasePopChat setConScrollAble(boolean conScrollAble) {
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

    private void initEdit(){
        if (mEditText!=null){
            mEditText.requestFocus();
            setAutoEdit(true);
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mEditText.postDelayed(()->{
                imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
            },200);

        }

    }

    //自动弹起
    public BasePopChat setAutoEdit(boolean autoEdit) {
        isAutoEdit = autoEdit;
        if (isAutoEdit){
            try {

            }catch (Exception ignored){}

        }
        return this;
    }
    //是否显示导航栏
    public void setShowNavi(boolean showNavi) {
        isShowNavi = showNavi;
    }

    public void beforeDismiss(){
        if (myPopLis!=null){
            myPopLis.beforeDismiss();
        }
    }

    public void show(){
        activity.getWindow().getDecorView().post(()->{
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
        });

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

    public BasePopChat setEdit(EditText mEdits) {
        this.mEditText = mEdits;
        return this;
    }

    private void initAutoEdit(){

       // traversalView(mContainer);

        final boolean[] isChange = {false};

        SoftUtils.addSoftListener(activity, (change, isShow) -> {
            if (isShow){
                if (isChange[0])return;
                isChange[0]=true;
                TransitionManager.beginDelayedTransition((ViewGroup) mContainer.getParent(), new TransitionSet()
                        .setDuration(200)
                        .addTransition(new ChangeBounds()).addTransition(new ChangeTransform())
                        .setInterpolator(new FastOutSlowInInterpolator()));
                mContainer.setTranslationY(-change);
            }else {
                if (!isChange[0])return;
                isChange[0]=false;
                mContainer.setTranslationY(0);
                mBase.init();
            }
        });

    }



    public void dismiss2(){
        if (isDismissing){
            return;
        }
        isDismissing=true;
        beforeDismiss();
        isShow=false;
        isDismissing=false;
        mParent.removeView(mBase);
        onDismiss();
    }
    protected void onDismiss() {

    }
    protected int getMaxHeight(){
        return 0;
    }
    public BasePopChat setPopListener(MyPopLis myPopLis){
        this.myPopLis=myPopLis;
        return this;
    }

    public ViewGroup getBase() {
        return mBase;
    }

    public ContainerBottom getmContainer() {
        return mContainer;
    }

    public static class MyPopLis extends BasePop.MyPopLis {
    }
}
