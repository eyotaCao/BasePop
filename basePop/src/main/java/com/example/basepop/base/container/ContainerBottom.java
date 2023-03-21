package com.example.basepop.base.container;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

//弹窗之底部弹出 的容器
public class ContainerBottom extends FrameLayout {
    private int lastY;
    private int mHeight;
    private int maxHeight=0;
    private int maxWidth;
    private boolean isScroll=false;
    private onBack mOnBack;
    private onScrollLis onScrollLis;


    public ContainerBottom(Context context, boolean isScroll) {
        super(context);
        this.isScroll=isScroll;
    }

    public ContainerBottom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerBottom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isScroll){
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dy =  lastY-(int) event.getRawY();
                    setTranslationY(getTranslationY()-dy>=0?getTranslationY()-dy:0);
                    if (onScrollLis!=null){
                        onScrollLis.onScroll(((float) mHeight-getTranslationY())/mHeight);
                    }
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    checkUp();
                    break;
            }
        }
        return true;
    }

    private void checkUp(){
        float tranY=getTranslationY();
        if (tranY>(float) mHeight/2f){
            scroll(getTranslationY(),mHeight);
        }else {
            scroll(getTranslationY(),0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight=bottom-top;
    }
    public void scroll(float start,float end){
        ValueAnimator animator=ValueAnimator.ofFloat(start,end);
        animator.addUpdateListener(animation -> {
            setTranslationY((Float) animator.getAnimatedValue());
            if (onScrollLis!=null){
                onScrollLis.onScroll(((float) mHeight-getTranslationY())/mHeight);
                if ((Float) animator.getAnimatedValue()==mHeight&&mOnBack!=null){
                    mOnBack.onback();
                }
            }
        });
        animator.setDuration(200).start();
    }


    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (maxHeight > 0&&getMeasuredHeight()>maxHeight) {
            try {
                LayoutParams llp= (LayoutParams) getLayoutParams();
                llp.height=maxHeight;
                this.post(()->{
                    setLayoutParams(llp);
                });

            }catch (Exception ignored){}
        }
        if (maxWidth > 0&&getMeasuredWidth()>maxWidth) {
            try {
                LayoutParams llp= (LayoutParams) getLayoutParams();
                llp.width=maxWidth;
                this.post(()->{
                    setLayoutParams(llp);
                });

            }catch (Exception ignored){}
        }

    }

    public void setOnScrollLis(ContainerBottom.onScrollLis onScrollLis) {
        this.onScrollLis = onScrollLis;
    }

    public interface onScrollLis{
        void onScroll(float percent);
    }
    public interface onBack{
        void onback();
    }
    public void setOnback(onBack onBack){
        mOnBack=onBack;
    }

}
