package com.example.basepop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.base.container.ContainerBottom;
import com.example.basepop.utils.PxTool;
import com.example.basepop.utils.SoftUtils;
import com.example.basepop.utils.ViewUtils;


//底部弹框 直接弹出
public abstract class BasePopChat extends BasePop<ContainerBottom> {
    protected boolean isShow=false;
    protected boolean isAutoEdit=false;
    private InputMethodManager imm;
    //是否显示导航栏
    private boolean isShowNavi=false;
    private int l,t,r,b,mNavigationHeight;
    private int offsetInput;  //偏移量
    private NestedScrollView mScroll2;
    private EditText mEditText;


    //contentAnimate
    private int  oldHeight,maxHeight=0;  //初始高度
    //shadowAnimate
    private boolean isConScrollAble=true;


    public BasePopChat(Activity activity){
        super(activity);
        this.activity =activity;
        setLayout(getImplLayoutId());
    }


    public void setLayout(int layout){
        this.layout=layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(){  //加入弹窗
        super.onCreate();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent= LayoutInflater.from(activity).inflate(layout,mBase,false);
        mContainer=new ContainerBottom(activity,isConScrollAble);
        mContainer.setOnback(this::dismiss);
        FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity= Gravity.BOTTOM;
        boolean isShowNav= PxTool.isShowNavBar(activity);
        if (isShowNav){
            int resourceId=getResources().getIdentifier("navigation_bar_height","dimen","android");
            mNavigationHeight = getResources().getDimensionPixelSize(resourceId);
        }else {
            mNavigationHeight = 0;
        }

        try {

            if (isShowNavi){
                FrameLayout.LayoutParams flpBa=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                flpBa.bottomMargin=mNavigationHeight;
                mBase.setLayoutParams(flpBa);
            }else {
                mContent.setPadding(0,0,0,mNavigationHeight);
            }
        }catch (Exception ignored){}
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.addView(mContent);
        mBase.addView(mContainer);
    }

    public void initAnimator() {
        oldHeight= ViewUtils.getMaxHeight(mContainer);

        mContent.setTranslationY(oldHeight);
    }

    private boolean isPostShowSoft;
    public void animateShow() {

        if (myPopListener !=null){
            myPopListener.onShow();
        }
        initAutoEdit();
        isPostShowSoft=true;
        initEdit();
        TransitionManager.beginDelayedTransition((ViewGroup) mContent.getParent(), new TransitionSet()
                .setDuration(100)
                .addTransition(new ChangeTransform())
                .setInterpolator(new FastOutSlowInInterpolator()));
        mContent.setTranslationY(0);
        mContent.postDelayed(()->{
            showState = BasePopConstants.SHOW_STATE_SHOW;
            mContainer.setTranslationY(0);
        },animationDuration);

    }

    public void animateDismiss() {

        if (myPopListener !=null){
            myPopListener.onDismiss();
        }
        mContent.postDelayed(()->{
            showState = BasePopConstants.SHOW_STATE_DISMISS;
            mParent.removeView(mBase);
        },animationDuration);
        if (isChange){
            if (imm!=null){
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(),0);
            }
        }else {
            dismissContent();
        }

    }
    private void dismissContent(){
        ViewPropertyAnimator animator2 ;
        animator2 = mContent.animate().translationY(oldHeight);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
                .withLayer()
                .start();
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
        if (myPopListener !=null){
            myPopListener.beforeShow();
        }
        initAnimator();
    }

    public void setOffsetInput(int offsetInput) {
        this.offsetInput = offsetInput;
    }

    private void initEdit(){
        if (mEditText!=null){
            mEditText.requestFocus();
            setAutoEdit(true);
            imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!isPostShowSoft){
                        return;
                    }
                    isPostShowSoft=false;
                    imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
                }
            });
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
        if (myPopListener !=null){
            myPopListener.beforeDismiss();
        }

    }

    public BasePopChat setEdit(EditText mEdits) {
        this.mEditText = mEdits;
        return this;
    }

    private boolean isChange;
    private void initAutoEdit(){

        // traversalView(mContainer);

        SoftUtils.addSoftListener(activity, (change, isShow) -> {
            if (isShow){
                if (isChange)return;
                isChange=true;
                TransitionManager.beginDelayedTransition((ViewGroup) mContainer.getParent(), new TransitionSet()
                        .setDuration(animationDuration)
                        .addTransition(new ChangeTransform())
                        .setInterpolator(new FastOutSlowInInterpolator()));

                mContainer.setTranslationY(-change);
            }else {
                if (!isChange)return;
                if (showState == BasePopConstants.SHOW_STATE_DISMISS){
                    TransitionManager.beginDelayedTransition((ViewGroup) mContainer.getParent(), new TransitionSet()
                            .setDuration(200)
                            .addTransition(new ChangeTransform())
                            .setInterpolator(new FastOutSlowInInterpolator()));
                }

                isChange=false;
                mContainer.setTranslationY(showState == BasePopConstants.SHOW_STATE_DISMISSING?mContainer.getMeasuredHeight():0);
                mBase.init();
            }
        });

    }




    protected void onDismiss() {

    }


    public ViewGroup getBase() {
        return mBase;
    }


}
