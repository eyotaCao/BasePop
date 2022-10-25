package com.example.cwjmodels;

import android.os.Bundle;

import com.example.basepop.basepop.base.photoViewer.BasePopImage;
import com.example.basepop.basepop.base.photoViewer.ImageDialog;
import com.example.cwjmodels.dialogs.Attach;
import com.example.cwjmodels.dialogs.Bottom;
import com.example.cwjmodels.dialogs.Bottom2;
import com.example.cwjmodels.dialogs.Center;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.cwjmodels.databinding.ActivityMainBinding;
import com.example.cwjmodels.dialogs.CenterBottom;
import com.example.cwjmodels.dialogs.CenterEdit;
import com.example.cwjmodels.dialogs.Top;

import android.view.Menu;
import android.view.MenuItem;

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

        binding.popBottom.setOnClickListener(view->{
            Bottom2 bottom2=new Bottom2(this);
            bottom2.show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}