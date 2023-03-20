package com.example.basepop.basepop.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.example.basepop.basepop.base.base.ContainerBottomFlex;

public class MyNestScrollView extends NestedScrollView {
    private ContainerBottomFlex container;
    public MyNestScrollView(@NonNull Context context) {
        super(context);
    }

    public MyNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        //Log.i("overscorll",scrollY+"");
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void setContainer(ContainerBottomFlex container) {
        this.container = container;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

     //   Log.i("MotionEvent2",event.getAction()+" y:"+event.getRawY());
        if (container.getTranslationY()!=0){
            return false;
        }else {
            return super.onTouchEvent(event);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
