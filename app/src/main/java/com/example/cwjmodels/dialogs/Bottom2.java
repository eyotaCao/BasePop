package com.example.cwjmodels.dialogs;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import com.example.basepop.basepop.base.BasePopBottomFlex;
import com.example.cwjmodels.R;
import com.example.basepop.basepop.base.views.MyNestScrollView;

public class Bottom2 extends BasePopBottomFlex {
    public Bottom2(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        MyNestScrollView scrollView=findViewById(R.id.scroll_view);

        getContainer().setScroll(scrollView);
        scrollView.setContainer(getContainer());
        // scrollView.seto

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_bottom2;
    }
}
