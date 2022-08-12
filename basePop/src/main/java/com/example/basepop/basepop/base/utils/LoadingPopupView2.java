package com.example.basepop.basepop.base.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.basepop.R;

public class LoadingPopupView2 extends FrameLayout {
    private TextView tv_title;
    private LoadCircleView mCircle;
    private String mTitle;
    private Context mContent;
    private AnimatorSet animatorSet;
    //private ValueAnimator animationS;

    public LoadingPopupView2(@NonNull Context context, String title) {
        super(context);
        loadingPopupView2(context,null,title);
    }



    public void loadingPopupView2(@NonNull Context context,AttributeSet attributeSet,String title) {
        this.mContent = context;
        this.mTitle = title;
        View load=View.inflate(context, R.layout.xpopup_center_loading2,null);
        addView(load);
        mCircle = findViewById(R.id.tv_load);
        tv_title = findViewById(R.id.tv_title);

        LinearLayout.LayoutParams params_circle;
        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setText(mTitle);
            tv_title.setVisibility(VISIBLE);
            params_circle = new LinearLayout.LayoutParams(PxTool.dpToPx(mContent, 27), PxTool.dpToPx(mContent, 27));
            params_circle.setMargins(PxTool.dpToPx(mContent, 20), PxTool.dpToPx(mContent, 12),
                    0, PxTool.dpToPx(mContent, 12));
        } else {
            tv_title.setText("");
            tv_title.setVisibility(GONE);
            params_circle = new LinearLayout.LayoutParams(PxTool.dpToPx(mContent, 43), PxTool.dpToPx(mContent, 43));
            params_circle.setMargins(PxTool.dpToPx(mContent, 15), PxTool.dpToPx(mContent, 15),
                    PxTool.dpToPx(mContent, 15), PxTool.dpToPx(mContent, 15));
        }
        mCircle.setLayoutParams(params_circle);
        mCircle.setProgress(0);
    }




    public void setShow() {
        if (animatorSet == null) {
            animatorSet = new AnimatorSet();
            ValueAnimator animator_progress = ObjectAnimator.ofInt(10, 360);
            animator_progress.addUpdateListener(valueAnimator -> {
                mCircle.post(()->{
                    mCircle.setProgress((Integer) valueAnimator.getAnimatedValue());
                });
            });
            animator_progress.setRepeatMode(ValueAnimator.REVERSE);
            animator_progress.setRepeatCount(Animation.INFINITE);
            ObjectAnimator animator_rotation = ObjectAnimator.ofFloat(
                    mCircle, "rotation", 0, 720);
            animator_rotation.setRepeatCount(Animation.INFINITE);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.setDuration(1500);
            animatorSet.play(animator_progress).with(animator_rotation);
        }
  //      animationS.start();
        animatorSet.start();
    //    setScaleX(0);
      //  setScaleY(0);
        setVisibility(VISIBLE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void dismiss() {
        setVisibility(GONE);
        if (animatorSet != null) {
            animatorSet.pause();
            animatorSet.cancel();
            animatorSet = null;
        }

    }
}
