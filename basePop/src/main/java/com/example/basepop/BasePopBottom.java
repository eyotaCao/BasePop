package com.example.basepop;

import static com.example.basepop.base.BasePopConstants.SHOW_STATE_DISMISS;
import static com.example.basepop.base.BasePopConstants.SHOW_STATE_DISMISSING;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopConstants;
import com.example.basepop.base.container.ContainerBottom;
import com.example.basepop.utils.PxTool;
import com.example.basepop.utils.SoftUtils;
import com.example.basepop.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

//底部弹框 有输入框自动弹起
public abstract class BasePopBottom extends BasePop<ContainerBottom> {
    protected boolean isShow=false;
    //是否自动调整弹窗位置
    protected boolean isAutoEdit=false;
    private List<EditText> mEdits;
    private ScrollView mScroll;
    private ViewGroup mScrollChild;
    //是否显示导航栏
    private boolean isShowNavi=false;
    private int l,t,r,b;
    private NestedScrollView mScroll2;

    private float scrollP = 1;

    //contentAnimate
    private int  oldHeight,maxHeight=0;  //初始高度
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final boolean isZeroDuration = false;
    private boolean isConScrollAble=true;

    public BasePopBottom(Activity activity){
        super(activity);
    }


    public void setLayout(int layout){
        this.layout=layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(){  //加入弹窗
        super.onCreate();
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent= LayoutInflater.from(activity).inflate(layout,mBase,false);
        mContainer=new ContainerBottom(activity,isConScrollAble);
        mContainer.setOnback(this::dismiss2);
        FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity= Gravity.BOTTOM;
        try {
            int resourceId=getResources().getIdentifier("navigation_bar_height","dimen","android");
            int height = getResources().getDimensionPixelSize(resourceId);
            if (isShowNavi){
                FrameLayout.LayoutParams flpBa=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                flpBa.bottomMargin=height;
                mBase.setLayoutParams(flpBa);
            }else {
                mContent.setPadding(0,0,0,height);
            }
        }catch (Exception ignored){}
        mContainer.setLayoutParams(flp);
        mContainer.setMaxHeight(maxHeight);
        mContainer.addView(mContent);
        mBase.addView(mContainer);  //弹窗内容
        mContainer.setOnScrollLis(percent -> {
            scrollP = percent;
            mBaseView.setBackgroundColor((Integer) argbEvaluator.evaluate(percent , startColor, shadowBgColor));
        });

        if (isAutoEdit){
            try {

                initAutoEdit();
            }catch (Exception ignored){}

        }
    }

    public void initAnimator() {
        oldHeight= ViewUtils.getMaxHeight(mContainer);

        mContent.setTranslationY(oldHeight);
    }

    public void animateShow() {

        if (myPopListener !=null){
            myPopListener.onShow();
        }
        mContainer.setTranslationY(0);
        ViewPropertyAnimator animator2 ;
        animator2 = mContent.animate().translationY(0);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
              //  .withLayer()
                .start();

        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor,shadowBgColor );
        animator.addUpdateListener(animation -> mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showState = BasePopConstants.SHOW_STATE_SHOW;
                mContainer.setTranslationY(0);
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration?0:animationDuration).start();
    }

    public void animateDismiss() {

        if (myPopListener !=null){
            myPopListener.onDismiss();
        }
        ViewPropertyAnimator animator2 ;
        animator2 = mContent.animate().translationY(oldHeight);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
                .withLayer()
                .start();

        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(animation -> mBaseView.setBackgroundColor((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                mParent.removeView(mBase);
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration?0:animationDuration).start();
    }

    public void animateDismiss2() {

        if (myPopListener !=null){
            myPopListener.onDismiss();
        }
        mContainer.post(()->{
            try {
                mParent.removeView(mBase);
            }catch (Exception ignore){}
        });
    }

    /**
     * 关闭弹窗
     */
    public void dismiss2(){
        if (showState == SHOW_STATE_DISMISSING || showState == SHOW_STATE_DISMISS){
            return;
        }
        showState = SHOW_STATE_DISMISSING;
        beforeDismiss();
        animateDismiss2();
    }

    public <T extends View> T findViewById(int id){
        return mContent.findViewById(id);
    }

    //设置没有阴影的背景点击可穿透
    public BasePopBottom setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopBottom setMaxHeight(int max) {
        maxHeight=max;
        return this;
    }

    protected Resources getResources(){
        return mBase.getResources();
    }

    public BasePopBottom setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
        return this;
    }

    public boolean isShow(){
        return isShow;
    }

    public void beforeShow(){   //弹窗显示之前执行
        if (myPopListener !=null){
            myPopListener.beforeShow();
        }
        initAnimator();
    }

    //自动弹起
    public BasePopBottom setAutoEdit(boolean autoEdit) {
        isAutoEdit = autoEdit;
        return this;
    }
    //是否显示导航栏
    public void setShowNavi(boolean showNavi) {
        isShowNavi = showNavi;
    }

    public void beforeDismiss(){
        if (myPopListener !=null){
            myPopListener.beforeDismiss();
        }
    }


    private void initAutoEdit(){
        mEdits=new ArrayList<>();
        final boolean[] isChange = {false};
        int screenHei= PxTool.getWindowHeight();
        SoftUtils.addSoftListener(activity, (change, isShow) -> {
            if (isShow){
                if (mContent.getMeasuredHeight()<change||mScrollChild==null){
                    mContainer.setTranslationY(-change-(isShowNavi?0:mContent.getPaddingBottom()));
                }else {
                    if (b<change){
                        mScrollChild.setPadding(l,t,r,change);
                        isChange[0] =true;
                        b=change;
                    }
                    for (EditText editText:mEdits){
                        if (editText.isFocused()){

                            int []location=ViewUtils.getLocation(editText);
                            if (screenHei-location[1]<Math.abs(change)+editText.getMeasuredHeight()){
                                if (mScroll!=null){
                                    if (isChange[0]){
                                        mScroll.postDelayed(()-> mScroll.smoothScrollBy(0,(Math.abs(change)-screenHei+location[1]+editText.getMeasuredHeight())),200);
                                        isChange[0]=false;
                                    }else {
                                        mScroll.smoothScrollBy(0,(Math.abs(change)-screenHei+location[1]+editText.getMeasuredHeight()));
                                    }
                                }else if (mScroll2!= null){
                                    if (isChange[0]){
                                        mScroll2.postDelayed(()->{
                                            mScroll2.smoothScrollBy(0,(Math.abs(change)-screenHei+location[1]+editText.getMeasuredHeight()));
                                            isChange[0]=false;
                                        },200);
                                    }else {
                                        mScroll2.smoothScrollBy(0,(Math.abs(change)-screenHei+location[1]+editText.getMeasuredHeight()));

                                    }
                                }
                            }
                            break;
                        }
                    }
                }

            }else {
                mContainer.setTranslationY(0);
                mBase.init();
            }
        });
        traversalView(mContainer);
    }
    private void traversalView(ViewGroup viewGroup){
        for (int i=0;i<viewGroup.getChildCount();i++){
            if (viewGroup.getChildAt(i) instanceof ViewGroup){
                traversalView((ViewGroup) viewGroup.getChildAt(i));
                if (viewGroup.getChildAt(i) instanceof ScrollView){
                    mScroll=(ScrollView) viewGroup.getChildAt(i);
                    mScrollChild=(ViewGroup) mScroll.getChildAt(0);
                    l=mScrollChild.getPaddingLeft();t=mScrollChild.getPaddingTop();
                    r=mScrollChild.getPaddingRight();b=mScrollChild.getPaddingBottom();
                }else if (viewGroup.getChildAt(i) instanceof NestedScrollView){
                    mScroll2=(NestedScrollView) viewGroup.getChildAt(i);
                    mScrollChild=(ViewGroup) mScroll2.getChildAt(0);
                    l=mScrollChild.getPaddingLeft();t=mScrollChild.getPaddingTop();
                    r=mScrollChild.getPaddingRight();b=mScrollChild.getPaddingBottom();
                }
            }else if (viewGroup.getChildAt(i) instanceof EditText){
                mEdits.add((EditText) viewGroup.getChildAt(i));
            }
        }
    }



    public ViewGroup getBase() {
        return mBase;
    }

    public ContainerBottom getmContainer() {
        return mContainer;
    }

}
