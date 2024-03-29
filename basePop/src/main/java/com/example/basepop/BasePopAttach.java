package com.example.basepop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
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
import com.example.basepop.utils.ViewUtils;

//依附于某个view弹窗
public abstract class BasePopAttach extends BasePop {
    protected View mAttachView;
    protected boolean isShowBg = true;
    //contentAnimate
    float startScale = .75f;
    private int offsetX;
    private Animate animType = Animate.RIGHT_TOP;
    //private int needTop,screenHeight;

    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;
    private boolean isScrollAble = false;
    private Container mContainer;

    public enum Animate {  //动画开始位置
        CENTER, RIGHT_TOP, RIGHT_BOTTOM, LEFT_TOP, LEFT_BOTTOM, AUTO
    }

    public BasePopAttach(Activity activity) {
        super(activity);
        setLayout(getImplLayoutId());
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    protected void onCreate() {  //加入弹窗
        if (!isShowBg) {
            shadowBgColor = R.color.transparent;
        }
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent = LayoutInflater.from(activity).inflate(layout, mBase, false);
        mContainer = new Container(activity, isScrollAble);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContainer.setLayoutParams(flp);
        mContainer.addView(mContent);
        mBase.addView(mContainer);  //弹窗内容
    }

    public void initAnimator() {
        mContainer.setAlpha(startScale);
        mContainer.setScaleX(startScale);
        mContainer.setScaleY(startScale);
        applyPivot();
    }

    private void applyPivot() {
        if (mAttachView == null) {
            return;
        }
        int[] loc = ViewUtils.getLocation(mAttachView);

        int screenH = PxTool.getScreenHeight();
        int screenW = PxTool.getScreenWidth();
        int width = mAttachView.getWidth();
        int height = mAttachView.getHeight();
        ViewUtils.getMaxHeight(mContainer);
        int contentWidth = mContainer.getMeasuredWidth();
        int widthC = mContainer.getMeasuredWidth();
        int heightC = mContainer.getMeasuredHeight();
        switch (animType) {
            case LEFT_TOP:
                if (loc[1] + height + heightC > screenH) {
                    animType = Animate.LEFT_BOTTOM;
                }
                break;
            case RIGHT_TOP:
                if (loc[1] + height + heightC > screenH) {
                    animType = Animate.RIGHT_BOTTOM;
                }
                break;
            case LEFT_BOTTOM:
                if (loc[1] < screenH) {
                    animType = Animate.LEFT_TOP;
                }
                break;
            case RIGHT_BOTTOM:
                if (loc[1] > screenH) {
                    animType = Animate.RIGHT_TOP;
                }
                break;

        }
        switch (animType) {
            case LEFT_TOP:
                if (loc[0] + contentWidth + offsetX > screenW) {
                    animType = Animate.RIGHT_TOP;
                }
                break;
            case RIGHT_TOP:
                if (loc[0] + offsetX < contentWidth) {
                    animType = Animate.LEFT_TOP;
                }
                break;
            case LEFT_BOTTOM:
                if (loc[0] + contentWidth + offsetX > screenW) {
                    animType = Animate.RIGHT_BOTTOM;
                }
                break;
            case RIGHT_BOTTOM:
                if (loc[0] + offsetX < width) {
                    animType = Animate.LEFT_BOTTOM;
                }
                break;

        }
        switch (animType) {
            case CENTER:
                mContainer.setPivotX(mContainer.getMeasuredWidth() / 2f);
                mContainer.setPivotY(mContainer.getMeasuredHeight() / 2f);
                break;
            case LEFT_TOP:
                mContainer.setTranslationX(loc[0] + width + offsetX);
                mContainer.setTranslationY(loc[1] + height);
                mContainer.setPivotX(0);
                mContainer.setPivotY(0);
                break;
            case RIGHT_TOP:
                mContainer.setTranslationX(loc[0] - widthC + offsetX);
                mContainer.setTranslationY(loc[1] + height);
                mContainer.setPivotX(mContainer.getMeasuredWidth());
                mContainer.setPivotY(0f);
                break;
            case LEFT_BOTTOM:
                mContainer.setTranslationX(loc[0] + width + offsetX);
                mContainer.setTranslationY(loc[1] - heightC);
                mContainer.setPivotX(0f);
                mContainer.setPivotY(mContainer.getMeasuredHeight());
                break;
            case RIGHT_BOTTOM:
                mContainer.setTranslationX(loc[0] - widthC + offsetX);
                mContainer.setTranslationY(loc[1] - heightC);
                mContainer.setPivotX(mContainer.getMeasuredWidth());
                mContainer.setPivotY(mContainer.getMeasuredHeight());
                break;
            case AUTO:
                break;
        }

    }

    public void animateShow() {
  
        mContainer.post(() -> mContainer.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(animationDuration)
                .setInterpolator(new OvershootInterpolator(1f))
//                .withLayer() 在部分6.0系统会引起crash
                .start());
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowBgColor);
        animator.addUpdateListener(animation -> {
            if (isShowBg) {
                mBase.setBackgroundColor((Integer) animation.getAnimatedValue());
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
        mContainer.animate().scaleX(startScale).scaleY(startScale).alpha(0f).setDuration(animationDuration)
                .setInterpolator(new FastOutSlowInInterpolator())
//                .withLayer() 在部分6.0系统会引起crash
                .start();
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(animation -> {
            if (isShowBg) {
                mBase.setBackgroundColor((Integer) animation.getAnimatedValue());
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


    public BasePopAttach setMaxHeight(int max) {
        maxHeight = max;
        return this;
    }

    public BasePopAttach setShowBg(boolean isShowBg) {
        this.isShowBg = isShowBg;
        return this;
    }


    public BasePopAttach setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources() {
        return mBase.getResources();
    }

    public BasePopAttach setScrollAble(boolean scrollAble) {
        isScrollAble = scrollAble;
        return this;
    }

    public void beforeShow() {   //弹窗显示之前执行
     
        initAnimator();
    }

    public void beforeDismiss() {
      
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

    protected void onDismiss() {

    }


    protected int getMaxWidth() {
        return 0;
    }


}
