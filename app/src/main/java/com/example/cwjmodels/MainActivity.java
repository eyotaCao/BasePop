package com.example.cwjmodels;

import android.os.Bundle;

import com.example.basepop.basepop.base.photoViewerDialog.ImageDialog;
import com.example.cwjmodels.dialogs.Attach;
import com.example.cwjmodels.dialogs.Bottom;
import com.example.cwjmodels.dialogs.BottomAnDialog;
import com.example.cwjmodels.dialogs.BottomChat;
import com.example.cwjmodels.dialogs.Center;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.cwjmodels.databinding.ActivityMainBinding;
import com.example.cwjmodels.dialogs.CenterBottom;
import com.example.cwjmodels.dialogs.CenterEdit;
import com.example.cwjmodels.dialogs.Top;

import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.popCenter.setOnClickListener(view->{
            Center center=new Center(this);
            center.show();
        });

        binding.popCenter2.setOnClickListener(view -> {
            CenterBottom centerBottom=new CenterBottom(this);
            centerBottom.show();
        });
        binding.popCenter3.setOnClickListener(view -> {
            Bottom bottom=new Bottom(this);
            bottom.show();
        });
        binding.popCenter4.setOnClickListener(view -> {
            Top attachTop=new Top(this);
            attachTop.atView(binding.toolbar).show();
        });
        binding.popCenter5.setOnClickListener(view -> {
            Attach attach=new Attach(this);
            attach.setAttachView(binding.popCenter5).show();
        });

        binding.popCenter6.setOnClickListener(view -> {
            CenterEdit centerEdit=new CenterEdit(this);
            centerEdit.setAutoEdit(true).show();
        });

        binding.popCenter7.setOnClickListener(view -> {
            ImageDialog imageDialog=new ImageDialog(this);
            imageDialog.setSrcView(binding.popCenter7).setLoadImage(view1 -> {
                view1.setImageResource(R.mipmap.ic_launcher);
            }).show();
        });

        binding.popBottomChat.setOnClickListener(view->{
            BottomChat bottom2=new BottomChat(this);
            bottom2.show();
        });
        binding.popTestDialog.setOnClickListener(view->{
            BottomAnDialog dialog=new BottomAnDialog();
            dialog.show(getSupportFragmentManager(),null);
        });
        /*
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                wv.loadUrl("javascript:document.body.style.padding=\"3%\"; void 0");
            }
        });


        if (getLanguage().equals("zh")){//changeLan
            wv.loadUrl("file:android_asset/LoginVerify.html");
        }else {
            wv.loadUrl("file:android_asset/LoginVerify2.html");
        }

        JavaScriptInterface ji= new JavaScriptInterface(this,imm,mEtPhone);
        wv.addJavascriptInterface(ji,"Android");*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}