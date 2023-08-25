package com.example.basepop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.base.container.Container;
import com.example.basepop.utils.ViewUtils;


//头部弹框
public abstract class BasePopTop extends BasePop {
    protected View attachView;
    protected boolean isShow = false;
    protected boolean isMove = false;
    //contentAnimate
    private int oldHeight, maxHeight = 0;  //初始高度
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;
    private boolean isConScrollAble = false;
    private boolean isContentCenter = false;
    private Container mContainer;

    public BasePopTop(Activity activity) {
        super(activity);
    }


    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate() {  //加入弹窗
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent = LayoutInflater.from(activity).inflate(layout, mBase, false);
        mContainer = new Container(activity, isConScrollAble);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int hei = 0;
        if (attachView != null) {
            hei = ViewUtils.getLocation(attachView)[1] + attachView.getMeasuredHeight();
        }
        flp.topMargin = hei;
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        if (isContentCenter) {
            mContent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL));
        }
        mContainer.addView(mContent);
        mBase.addView(mContainer);
    }

    public void initAnimator() {
        oldHeight = ViewUtils.getMaxHeight(mContainer);
        mContent.setTranslationY(-oldHeight);
        if (isMove) {
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int hei = 0;
            if (attachView != null) {
                hei = ViewUtils.getLocation(attachView)[1] + attachView.getMeasuredHeight();
            }
            flp.topMargin = hei;
            mContainer.setLayoutParams(flp);
        }
    }

    public void animateShow() {
  
        ViewPropertyAnimator animator2;
        animator2 = mContent.animate().translationY(0);
        if (animator2 != null) animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
                //  .withLayer()
                .start();

        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowBgColor);
        animator.addUpdateListener(animation -> mBase.setBackgroundColor((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showState = BasePopConstants.SHOW_STATE_SHOW;
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : animationDuration).start();
    }

    public void animateDismiss() {

        
        if (mContent == null) {
            return;
        }
        ViewPropertyAnimator animator2;
        animator2 = mContent.animate().translationY(-oldHeight);
        if (animator2 != null) animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
                .withLayer()
                .start();

        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(animation -> mBase.setBackgroundColor((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showState = BasePopConstants.SHOW_STATE_DISMISS;
                mParent.removeView(mBase);
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : animationDuration).start();
    }

    public <T extends View> T findViewById(int id) {
        return mContent.findViewById(id);
    }

    public BasePopTop atView(View view) {
        attachView = view;
        return this;
    }

    //设置没有阴影的背景点击可穿透
    public BasePopTop setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopTop setMove(boolean move) {
        isMove = move;
        return this;
    }

    public BasePopTop setMaxHeight(int max) {
        maxHeight = max;
        return this;
    }

    protected Resources getResources() {
        return mBase.getResources();
    }

    public BasePopTop setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
        return this;
    }

    public BasePopTop setContentCenter(boolean contentCenter) {
        isContentCenter = contentCenter;
        return this;
    }

    public boolean isShow() {
        return isShow;
    }

    public void beforeShow() {   //弹窗显示之前执行
     
        initAnimator();
    }

    public void beforeDismiss() {
      
    }


    public void setFocus() {  //打开子窗口失去焦点需要重新获取
        try {
            mBase.init();
        } catch (Exception ignored) {
        }
    }


}
