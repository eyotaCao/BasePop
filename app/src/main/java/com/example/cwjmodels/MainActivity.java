package com.example.cwjmodels;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.basepop.base.BasePop;
import com.example.basepop.base.BasePopListener;
import com.example.basepop.photoViewerDialog.ImageDialog;
import com.example.cwjmodels.databinding.ActivityMainBinding;
import com.example.cwjmodels.dialogs.Attach;
import com.example.cwjmodels.dialogs.Bottom;
import com.example.cwjmodels.dialogs.BottomAnDialog;
import com.example.cwjmodels.dialogs.BottomChat;
import com.example.cwjmodels.dialogs.Center;
import com.example.cwjmodels.dialogs.CenterBottom;
import com.example.cwjmodels.dialogs.CenterEdit;
import com.example.cwjmodels.dialogs.Top;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.popCenter.setOnClickListener(view->{
            Center center=new Center(this);
            bindListener(center, "Center");
            center.show();

        });

        binding.popCenter2.setOnClickListener(view -> {
            CenterBottom centerBottom=new CenterBottom(this);
            bindListener(centerBottom, "centerBottom");
            centerBottom.show();

        });
        binding.popCenter3.setOnClickListener(view -> {
            Bottom bottom=new Bottom(this);
            bindListener(bottom, "bottom");
            bottom.show();

        });
        binding.popCenter4.setOnClickListener(view -> {
            Top attachTop=new Top(this);
            bindListener(attachTop, "attachTop");
            attachTop.atView(binding.toolbar).show();


        });
        binding.popCenter5.setOnClickListener(view -> {
            Attach attach=new Attach(this);
            bindListener(attach, "attach");
            attach.setAttachView(binding.popCenter5).show();


        });

        binding.popCenter6.setOnClickListener(view -> {
            CenterEdit centerEdit=new CenterEdit(this);
            bindListener(centerEdit, "CenterEdit");
            centerEdit.setAutoEdit(true).show();


        });

        binding.popCenter7.setOnClickListener(view -> {
            ImageDialog imageDialog=new ImageDialog(this);
            bindListener(imageDialog, "imageDialog");
            imageDialog.setSrcView(binding.popCenter7).setLoadImage(view1 -> {
                view1.setImageResource(R.mipmap.ic_launcher);
            }).show();


        });

        binding.popBottomChat.setOnClickListener(view->{
            BottomChat bottom2=new BottomChat(this);
            bindListener(bottom2, "bottom2");
            bottom2.show();


        });
        binding.popTestDialog.setOnClickListener(view->{
            BottomAnDialog dialog=new BottomAnDialog();
            dialog.show(getSupportFragmentManager(),null);
        });
    }


    private void bindListener(BasePop basePop, String tag) {
        basePop.setPopListener(new BasePopListener(){
            @Override
            public void onShow() {
                Log.i(TAG, "onShow:" + tag);
            }

            @Override
            public void onDismiss() {
                Log.i(TAG, "onDismiss:" + tag);
            }

            @Override
            public void onBack() {
                Log.i(TAG, "onBack:" + tag);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}