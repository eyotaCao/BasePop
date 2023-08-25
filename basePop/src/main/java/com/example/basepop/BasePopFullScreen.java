package com.example.basepop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.base.container.Container;
import com.example.basepop.utils.FastOutSlowInInterpolator;


//全屏弹窗
public abstract class BasePopFullScreen extends BasePop {
    protected boolean isShow = false;
    //contentAnimate
    float startScale = 0;

    private boolean isConScrollAble = false;

    private Container mContainer;

    public BasePopFullScreen(Activity activity) {
        super(activity);
        setLayout(getImplLayoutId());
    }


    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    protected void onCreate() {  //加入弹窗
        //初始高度
        int maxWidth = getMaxWidth();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent = LayoutInflater.from(activity).inflate(layout, mBase, false);
        mContainer = new Container(activity, isConScrollAble);

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flp.gravity = Gravity.CENTER;
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.setMaxWidth(maxWidth);
        mContainer.addView(mContent);
        mBase.addView(mContainer);
    }


    public void initAnimator() {
        mContainer.setAlpha(startScale);
    }

    public void animateShow() {

  

        mContainer.animate().alpha(1f)
                .setDuration(animationDuration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        showState = BasePopConstants.SHOW_STATE_SHOW;
                    }
                })
                .setInterpolator(new OvershootInterpolator(1f))
//                .withLayer() 在部分6.0系统会引起crash
                .start();


    }

    public void animateDismiss() {

        
        mContainer.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        showState = BasePopConstants.SHOW_STATE_DISMISS;
                        try {
                            mParent.removeView(mBase);
                        } catch (Exception ignored) {
                        }

                    }
                }).setDuration(animationDuration)
                .setInterpolator(new FastOutSlowInInterpolator())
//                .withLayer() 在部分6.0系统会引起crash
                .start();

    }

    public <T extends View> T findViewById(int id) {
        return mContent.findViewById(id);
    }


    //设置没有阴影的背景点击可穿透
    public BasePopFullScreen setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopFullScreen setMaxHeight(int max) {
        maxHeight = max;
        return this;
    }


    public BasePopFullScreen setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    protected Resources getResources() {
        return mBase.getResources();
    }

    public BasePopFullScreen setConScrollAble(boolean conScrollAble) {
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

    protected void onDismiss() {

    }


    protected int getMaxWidth() {
        return 0;
    }


}
