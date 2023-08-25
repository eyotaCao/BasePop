package com.example.basepop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.base.container.Container;
import com.example.basepop.utils.PxTool;
import com.example.basepop.utils.SoftUtils;
import com.example.basepop.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

//中心弹框  中心弹出动画 有编辑框自动弹起
public abstract class BasePopCenterEdit extends BasePop {
    protected boolean isShow = false, isShowBg = true, isAutoEdit = false;
    //contentAnimate
    float startScale = .75f;
    private List<EditText> mEdits;
    //private int needTop,screenHeight;
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;
    private boolean isConScrollAble = false;

    private Container mContainer;

    public BasePopCenterEdit(Activity activity) {
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
        //初始高度
        int maxWidth = getMaxWidth();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent = LayoutInflater.from(activity).inflate(layout, mBase, false);
        mContainer = new Container(activity, isConScrollAble);

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER;
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.setMaxWidth(maxWidth);
        mContainer.addView(mContent);
        mBase.addView(mContainer);
        if (isAutoEdit) {
            try {
                initAutoEdit();
            } catch (Exception ignored) {
            }

        }
    }

    public void initAnimator() {
        mContainer.setAlpha(startScale);
        mContainer.setScaleX(startScale);
        mContainer.setScaleY(startScale);
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

    private void initAutoEdit() {
        mEdits = new ArrayList<>();
        traversalView(mContainer);
        int screenHei = PxTool.getScreenHeight();
        SoftUtils.addSoftListener(activity, (change, isShow) -> {
            if (isShow) {
                if (mEdits.size() > 0) {
                    int[] location = ViewUtils.getLocation(mContainer);

                    if (screenHei - location[1] < Math.abs(change) + mContent.getMeasuredHeight()) {

                        ViewPropertyAnimator animator2;
                        animator2 = mContainer.animate().translationY(-(Math.abs(change) - screenHei + location[1] + mContainer.getMeasuredHeight()));
                        if (animator2 != null)
                            animator2.setInterpolator(new FastOutSlowInInterpolator())
                                    .setDuration(200)
                                    //  .withLayer()
                                    .start();
                    }
                }
            } else {
                ViewPropertyAnimator animator2;
                animator2 = mContainer.animate().translationY(0);
                if (animator2 != null) animator2.setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(200)
                        //  .withLayer()
                        .start();
                mBase.init();
            }
        });

    }

    private void traversalView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                traversalView((ViewGroup) viewGroup.getChildAt(i));
            } else if (viewGroup.getChildAt(i) instanceof EditText) {
                mEdits.add((EditText) viewGroup.getChildAt(i));
            }
        }
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
                Log.i("dasda", "dsds");
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
        maxHeight = max;
        return this;
    }

    public BasePopCenterEdit setShowBg(boolean isShowBg) {
        this.isShowBg = isShowBg;
        return this;
    }

    public BasePopCenterEdit setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources() {
        return mBase.getResources();
    }

    public BasePopCenterEdit setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
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

    protected int getMaxWidth() {
        return (int) (PxTool.getScreenWidth() * 0.85);
    }


}
