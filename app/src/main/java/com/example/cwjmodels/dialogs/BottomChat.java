package com.example.cwjmodels.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.basepop.basepop.base.BasePopBottomFlex;
import com.example.basepop.basepop.base.BasePopChat;
import com.example.basepop.basepop.base.views.MyNestScrollView;
import com.example.cwjmodels.R;

public class BottomChat extends BasePopChat {
    public BottomChat(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setEdit(findViewById(R.id.chat_input));

    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_bottom_chat;
    }
}
