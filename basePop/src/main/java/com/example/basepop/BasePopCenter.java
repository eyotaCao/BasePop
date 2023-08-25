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
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.base.container.Container;
import com.example.basepop.utils.PxTool;

//中心弹框  中心弹出动画
public abstract class BasePopCenter extends BasePop<Container> {
    protected boolean isShow = false, isShowBg = true;
    //contentAnimate
    float startScale = .75f;
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;
    private boolean isConScrollAble = false;

    public BasePopCenter(Activity activity) {
        super(activity);
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    protected void onCreate() {
        if (!isShowBg) {
            shadowBgColor = R.color.transparent;
        }
        //初始高度
        int maxWidth = getMaxWidth();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            , Gravity.CENTER));
        mContainer = new Container(activity, isConScrollAble);
        mContent = LayoutInflater.from(activity).inflate(layout, mContainer, false);
        try {
            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) mContent.getLayoutParams();
            flp.gravity = Gravity.CENTER;
            mContainer.setLayoutParams(flp);
        } catch (Exception ignored) {
        }
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER;
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.setMaxWidth(maxWidth);
        mContainer.addView(mContent);
        mBase.addView(mContainer);  //弹窗内容

    }

    public void initAnimator() {
        mContainer.setAlpha(startScale);
        mContainer.setScaleX(startScale);
        mContainer.setScaleY(startScale);
    }

    public void animateShow() {
        if (myPopListener != null) {
            myPopListener.onShow();
        }
        mContainer.post(() -> mContainer.animate().scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(animationDuration)
            .setInterpolator(new OvershootInterpolator(1f))
//                .withLayer() 在部分6.0系统会引起crash
            .start());
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowBgColor);
        animator.addUpdateListener(animation -> {
            if (isShowBg) {
                mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
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

        if (myPopListener != null) {
            myPopListener.onDismiss();
        }
        mContainer.animate().scaleX(startScale).scaleY(startScale).alpha(0f).setDuration(animationDuration)
            .setInterpolator(new FastOutSlowInInterpolator())
//                .withLayer() 在部分6.0系统会引起crash
            .start();
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(animation -> {
            if (isShowBg) {
                mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showState = BasePopConstants.SHOW_STATE_DISMISS;
                try {
                    mParent.removeView(mBase);
                } catch (Exception ignored) {
                }
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : animationDuration).start();
    }

    public <T extends View> T findViewById(int id) {
        return mContent.findViewById(id);
    }


    //设置没有阴影的背景点击可穿透
    public BasePopCenter setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopCenter setMaxHeight(int max) {
        maxHeight = max;
        return this;
    }

    public BasePopCenter setShowBg(boolean isShowBg) {
        this.isShowBg = isShowBg;
        return this;
    }

    public BasePopCenter setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources() {
        return mBase.getResources();
    }

    public BasePopCenter setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
        return this;
    }

    public boolean isShow() {
        return isShow;
    }

    public void beforeShow() {   //弹窗显示之前执行
        if (myPopListener != null) {
            myPopListener.beforeShow();
        }
        initAnimator();
    }

    public void beforeDismiss() {
        if (myPopListener != null) {
            myPopListener.beforeDismiss();
        }
    }

    //获取弹窗最大高度
    protected int getMaxHeight() {
        return 0;
    }

    //获取弹窗最大宽度
    protected int getMaxWidth() {
        return (int) (PxTool.getScreenWidth() * 0.85);
    }


}
