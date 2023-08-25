package com.example.basepop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import com.example.basepop.utils.PxTool;
import com.example.basepop.utils.ViewUtils;

//中心弹框 从底部弹出
public abstract class BasePopCenterBottom extends BasePop {

    private int maxHeight = 0;  //初始高度
    private int needTop, screenHeight;
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;
    private boolean isConScrollAble = false;
    private Container mContainer;

    public BasePopCenterBottom(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {  //加入弹窗
        screenHeight = PxTool.getScreenHeight();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent = LayoutInflater.from(activity).inflate(layout, mBase, false);
        mContainer = new Container(activity, isConScrollAble);

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_HORIZONTAL;
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.addView(mContent);

        mBase.addView(mContainer);  //弹窗内容

    }

    public void initAnimator() {
        //contentAnimate
        int oldHeight = ViewUtils.getMaxHeight(mContainer);

        mContainer.setTranslationY(screenHeight);
        needTop = (int) ((float) (screenHeight - oldHeight) / 2f);
    }

    public void animateShow() {

  
        ViewPropertyAnimator animator2;
        animator2 = mContainer.animate().translationY(needTop);
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

        
        ViewPropertyAnimator animator2;
        animator2 = mContainer.animate().translationY(screenHeight);
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


    //设置没有阴影的背景点击可穿透
    public BasePopCenterBottom setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopCenterBottom setMaxHeight(int max) {
        maxHeight = max;
        return this;
    }


    public BasePopCenterBottom setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources() {
        return mBase.getResources();
    }

    public BasePopCenterBottom setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
        return this;
    }


    public void beforeShow() {   //弹窗显示之前执行
     
        initAnimator();
    }

    public void beforeDismiss() {
      
    }

}
