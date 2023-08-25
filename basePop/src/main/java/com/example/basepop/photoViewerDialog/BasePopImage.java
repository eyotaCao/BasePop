package com.example.basepop.photoViewerDialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.bumptech.glide.Glide;
import com.example.basepop.R;
import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.utils.PxTool;
import com.example.basepop.utils.ViewUtils;

import java.util.Locale;

/**
 * 大图预览弹窗
 */
public abstract class BasePopImage extends BasePop<PhotoViewContainer> {
    protected int layout;
    protected ImageView srcView;
    protected PhotoView mPhoto;
    private String url;
    private Rect rect;
    protected boolean isShow = false, isShowBg = true, isAboveNavi = false;
    //contentAnimate
    protected static final int animationDuration = 480; //弹窗打开/关闭动画时长
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;

    private LoadImage loadImage;


    public BasePopImage(Activity activity) {
        super(activity);
        setLayout(getImplLayoutId());
    }


    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    @SuppressLint("ResourceType")
    protected void onCreate() {  //加入弹窗
        if (!isShowBg) {
            shadowBgColor = R.color.transparent;
        }
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent = LayoutInflater.from(activity).inflate(layout, mBase, false);
        mContainer = new PhotoViewContainer(activity);
        setPhoto(mContent.findViewById(R.id.dialog_image_photo));
        if (isAboveNavi) {
            try {
                @SuppressLint("InternalInsetResource") int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                int height = getResources().getDimensionPixelSize(resourceId);
                FrameLayout.LayoutParams flpBa = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
                flpBa.bottomMargin = height;
                mBase.setLayoutParams(flpBa);
            } catch (Exception ignored) {
            }
        }
        if (mPhoto != null) {
            if (srcView != null) {
                mPhoto.setImageDrawable(srcView.getDrawable());
            }
            if (loadImage != null) {
                loadImage.onLoad(mPhoto);
            } else {
                Glide.with(activity).load(url).into(mPhoto);
            }
            mPhoto.setOnViewTapListener((view, x, y) -> dismiss());
            mContainer.setContent((LinearLayout) mContent);
            mContainer.setOnDragChangeListener(new PhotoViewContainer.OnDragChangeListener() {
                @Override
                public void onRelease() {
                    dismiss();
                }

                @Override
                public void onDragChange(int dy, float scale, float fraction) {


                    mBaseView.setBackgroundColor((Integer) argbEvaluator.evaluate(fraction * .8f, shadowBgColor, Color.TRANSPARENT));
                }
            });
        }

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flp.gravity = Gravity.CENTER;
        mContainer.setLayoutParams(flp);
        mContainer.setClipChildren(false);
        mContainer.addView(mContent);
        mBase.addView(mContainer);
    }

    public void initAnimator() {
        if (srcView != null && activity != null) {
            int[] locations = ViewUtils.getLocation(srcView);
            if (isLayoutRtl(activity)) {
                int left = -(PxTool.getWindowWidthAndHeight(activity)[0] - locations[0] - srcView.getWidth());
                rect = new Rect(left, locations[1], left + srcView.getWidth(), locations[1] + srcView.getHeight());
            } else {
                rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
            }
        }
        initParam();
    }

    public void animateShow() {

        if (myPopListener != null) {
            myPopListener.onShow();
        }
        mPhoto.post(() -> {
            TransitionManager.beginDelayedTransition((ViewGroup) mPhoto.getParent(), new TransitionSet()
                .setDuration(animationDuration)
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform())
                .setInterpolator(new FastOutSlowInInterpolator()));
            mPhoto.setScaleY(1);
            mPhoto.setScaleX(1);
            mPhoto.setTranslationY(0);
            mPhoto.setTranslationX(0);

        });
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
        mPhoto.post(() -> {
            TransitionManager.beginDelayedTransition((ViewGroup) mContent.getParent(), new TransitionSet()
                .setDuration(animationDuration)
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform())
                .setInterpolator(new FastOutSlowInInterpolator()));
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            flp.gravity = Gravity.CENTER;
            mContainer.setLayoutParams(flp);
            mContent.setScaleX(1);
            mContent.setScaleY(1);
            initAnimator();
        });

        final int start = ((ColorDrawable) mBaseView.getBackground()).getColor();
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, start, startColor);
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

    public static boolean isLayoutRtl(Context context) {
        Locale primaryLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            primaryLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            primaryLocale = context.getResources().getConfiguration().locale;
        }
        return TextUtils.getLayoutDirectionFromLocale(primaryLocale) == View.LAYOUT_DIRECTION_RTL;
    }

    //设置没有阴影的背景点击可穿透
    public BasePopImage setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }


    public BasePopImage setShowBg(boolean isShowBg) {
        this.isShowBg = isShowBg;
        return this;
    }


    public BasePopImage setDismissOnBack(boolean dismissOnBack) {
        this.dismissOnBack = dismissOnBack;
        return this;
    }

    public void setPhoto(PhotoView mPhoto) {
        this.mPhoto = mPhoto;
    }

    //设置图片路径 (网络图片)
    public BasePopImage setUrl(String url) {
        this.url = url;
        return this;
    }

    private void initParam() {
        mPhoto.attacher.reset();
        float screenWidth = (float) PxTool.screenWidth;
        mPhoto.setScaleX((float) (rect.width()) / screenWidth);
        mPhoto.setScaleY((float) (rect.width()) / screenWidth);
        float tranX = -(float) (screenWidth - rect.width()) / 2f - rect.left;
        float tranY = -(float) ((mParent.getMeasuredHeight() - rect.height()) / 2f - rect.top);
        mPhoto.setTranslationX(tranX);
        mPhoto.setTranslationY(tranY);
    }


    protected Resources getResources() {
        return mBase.getResources();
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


    public BasePopImage setSrcView(ImageView srcView) {
        this.srcView = srcView;
        return this;
    }

    public interface LoadImage {
        void onLoad(ImageView view);
    }

    //可自定义加载大图方式
    public BasePopImage setLoadImage(LoadImage loadImage) {
        this.loadImage = loadImage;
        return this;
    }
}
