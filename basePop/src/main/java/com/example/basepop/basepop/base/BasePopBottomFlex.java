package com.example.basepop.basepop.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
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

import com.example.basepop.basepop.base.utils.PxTool;
import com.example.basepop.basepop.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

//底部全屏伸缩弹框 有输入框自动弹起
public abstract class BasePopBottomFlex extends BasePop{
    protected int layout;
    protected BackgroudView mBaseView; //阴影背景
    protected ViewGroup mParent;
    protected View mContent;
    protected ContainerBottomFlex mContainer;
    protected Activity activity;
    protected boolean isShow=false,isCreate=false;
    protected boolean isShowing=false,isDismissing=false,isAutoEdit=false;
    private List<EditText> mEdits;
    private ScrollView mScroll;
    private ViewGroup mScrollChild;
    //是否显示导航栏
    private boolean isShowNavi=false;
    private int l,t,r,b;
    private NestedScrollView mScroll2;

    //contentAnimate
    private int  oldHeight,maxHeight=0;  //初始高度
    private int screenHei;
    private static final int animationDuration = 350;
    //shadowAnimate
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final int startColor = Color.TRANSPARENT;
    private final boolean isZeroDuration = false;
    private boolean isClickThrough=true;
    private boolean isConScrollAble=true,hasShadow=false;
    private MyPopLis myPopLis;
    //offset
    public static float BOTTOM_OFFSET=0.4f;

    public BasePopBottomFlex(Activity activity){
        super(activity);
        this.activity =activity;
        setLayout(getImplLayoutId());
        getMaxHeight();
    }


    protected abstract int getImplLayoutId();

    public void setLayout(int layout){
        this.layout=layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(){  //加入弹窗

        isCreate=true;
        mBase=new Backgroud(activity);
        mBase.setClickThrough(isClickThrough);
        mBase.setOnback(this::dismiss);
        mBase.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent= LayoutInflater.from(activity).inflate(layout,mBase,false);
        mContainer=new ContainerBottomFlex(activity,isConScrollAble);
        mContainer.setOnback(this::dismiss2);
        FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity= Gravity.BOTTOM;
        try {
            screenHei=PxTool.getWindowWidthAndHeight(activity)[1];
            ViewGroup.LayoutParams flp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    screenHei);
            mContent.setLayoutParams(flp2);
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
        mBaseView=new BackgroudView(activity);

        mBaseView.setOnback(this::dismiss);
        FrameLayout.LayoutParams flp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseView.setLayoutParams(flp2);
        mBase.addView(mBaseView);  //背景
        mBase.addView(mContainer);  //弹窗
        mParent =(FrameLayout) activity.getWindow().getDecorView();
        mParent.setOnTouchListener((v, event) -> false);
        mContainer.setOnScrollLis(percent -> {
            if (!hasShadow){
                return;
            }
        });
        try {
            mParent.addView(mBase);
        }catch (Exception ignored){

        }
        if (isAutoEdit){
            try {
                initAutoEdit();
            }catch (Exception ignored){}

        }
    }

    public void initAnimator() {
        oldHeight= ViewUtils.getMaxHeight(mContainer);
        mContainer.setTranslationY(oldHeight);

    }

    public void animateShow() {

        if (myPopLis!=null){
            myPopLis.onShow();
        }
       // mContainer.setTranslationY(0);
        ViewPropertyAnimator animator2 ;
        int offset=(int)((float)oldHeight*BOTTOM_OFFSET);
     //   System.out.println("offset:"+offset);
        animator2 = mContainer.animate().translationY(offset);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
              //  .withLayer()
                .start();
        mContainer.postDelayed(()->{
            isShow=true;
            isShowing=false;
        },animationDuration);

    }

    public void animateDismiss() {

        if (myPopLis!=null){
            myPopLis.onDismiss();
        }
        ViewPropertyAnimator animator2 ;
        animator2 = mContainer.animate().translationY(oldHeight);
        if(animator2!=null)animator2.setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animationDuration)
                .withLayer()
                .start();

        mContainer.postDelayed(()->{
            isShow=false;
            isDismissing=false;
            mParent.removeView(mBase);
        },animationDuration);


    }

    public <T extends View> T findViewById(int id){
        return mContent.findViewById(id);
    }

    //设置没有阴影的背景点击可穿透
    public BasePopBottomFlex setClickThrough(boolean clickThrough) {
        isClickThrough = clickThrough;
        return this;
    }

    public BasePopBottomFlex setMaxHeight(int max) {
        maxHeight=max;
        return this;
    }

    protected Resources getResources(){
        return mBase.getResources();
    }

    public BasePopBottomFlex setConScrollAble(boolean conScrollAble) {
        isConScrollAble = conScrollAble;
        return this;
    }

    public boolean isShow(){
        return isShow;
    }

    public void beforeShow(){   //弹窗显示之前执行
        if (myPopLis!=null){
            myPopLis.beforeShow();
        }
        initAnimator();
    }

    //自动弹起
    public BasePopBottomFlex setAutoEdit(boolean autoEdit) {
        isAutoEdit = autoEdit;
        return this;
    }
    //是否显示导航栏
    public void setShowNavi(boolean showNavi) {
        isShowNavi = showNavi;
    }

    public void beforeDismiss(){
        if (myPopLis!=null){
            myPopLis.beforeDismiss();
        }
    }

    public void show(){
        activity.getWindow().getDecorView().post(()->{
            if (isShowing||isShow){
                if (isShow){
                    dismiss();
                }
                return;
            }
            isShowing=true;
            if (!isCreate){
                onCreate();
            }else {
                try {
                    mParent.addView(mBase);
                    mBase.init();
                }catch (Exception ignored){}
            }

            beforeShow();
            animateShow();
        });

    }
    public void dismiss(){
        if (isDismissing){
            return;
        }
        isDismissing=true;
        beforeDismiss();
        animateDismiss();
        onDismiss();
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


    public void dismiss2(){
        if (isDismissing){
            return;
        }
        isDismissing=true;
        beforeDismiss();
        isShow=false;
        isDismissing=false;
        mParent.removeView(mBase);
        onDismiss();
    }
    protected void onDismiss() {

    }
    protected int getMaxHeight(){
        return 0;
    }
    public BasePopBottomFlex setPopListener(MyPopLis myPopLis){
        this.myPopLis=myPopLis;
        return this;
    }

    public ViewGroup getBase() {
        return mBase;
    }

    public ContainerBottomFlex getContainer() {
        return mContainer;
    }

    public static class MyPopLis extends BasePop.MyPopLis {
    }
}
