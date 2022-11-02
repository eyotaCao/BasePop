package com.example.basepop.basepop.base;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.example.basepop.basepop.base.views.MyNestScrollView;

//弹窗之底部弹出 的容器
public class ContainerBottomFlex extends FrameLayout {
    private int lastY;
    private int mHeight;
    private int maxHeight=0;
    private int maxWidth;
    private boolean isScroll=false;
    private onBack mOnBack;
    private onScrollLis onScrollLis;
    private MyNestScrollView mScroll;


    public ContainerBottomFlex(Context context, boolean isScroll) {
        super(context);
        this.isScroll=isScroll;
    }

    public ContainerBottomFlex(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerBottomFlex(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    boolean re=false,re2=false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isScroll){
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastY = (int) event.getRawY();
                    getParent().requestDisallowInterceptTouchEvent(false);
                    mScroll.onTouchEvent(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dy =  lastY-(int) event.getRawY();
                    boolean cansScroll=false;
                    if (dy>0){
                        if (getTranslationY()==0){

                            if (!re){
                                re=true;
                                event.setAction(MotionEvent.ACTION_DOWN);
                                event.setLocation(event.getRawX(),event.getRawY()-1);
                                mScroll.onTouchEvent(event);
                            }
                            event.setAction(MotionEvent.ACTION_MOVE);
                            mScroll.onTouchEvent(event);
                        }else {
                            re=false;
                            cansScroll=true;
                        }
                    }else {
                        if (!mScroll.canScrollVertically(-1)){
                            if (!re2){
                                re2=true;
                                dy=0;
                            }


                            cansScroll=true;
                        }else {
                            re2=false;
                        }
                        mScroll.onTouchEvent(event);
                    }
                    if (cansScroll){

                        setTranslationY(getTranslationY()-dy<=0?0:getTranslationY()-dy);
                        if (onScrollLis!=null){
                            onScrollLis.onScroll(((float) mHeight-getTranslationY())/mHeight);
                        }
                        lastY = (int) event.getRawY();
                    }else {

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    checkUp();
                    mScroll.onTouchEvent(event);
                    break;
                default:
                    mScroll.onTouchEvent(event);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    //0  hei*offset  hei
    private void checkUp(){
        float tranY=getTranslationY();

        if (tranY<(float) mHeight*BasePopBottomFlex.BOTTOM_OFFSET/2f){
            scroll(getTranslationY(),0);
        }else if (
                tranY<(float) mHeight*(1-(BasePopBottomFlex.BOTTOM_OFFSET/2f+0.1))){
            scroll(getTranslationY(),mHeight*BasePopBottomFlex.BOTTOM_OFFSET);
        }else {
            scroll(getTranslationY(),mHeight);
        }
    }

    public void setScroll(MyNestScrollView mScroll) {
        this.mScroll = mScroll;
    }

    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
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

    public void setOnScrollLis(ContainerBottomFlex.onScrollLis onScrollLis) {
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
