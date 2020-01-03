package com.example.ricca.zap;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ricca.zap.GUI.tutorial.HelpDialogManager;
import com.example.ricca.zap.GUI.HomePager;
import com.example.ricca.zap.Services.ConnectionListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


public class MainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    HelpDialogManager help;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new ConnectionListener(this)).start();  //crea un monitor per la connessione

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;  //disabilita status bar
        decorView.setSystemUiVisibility(uiOptions);


        DotsIndicator indicator=findViewById(R.id.dots_indicator);

        ViewPager vpPager = findViewById(R.id.pager);
        adapterViewPager = new HomePager(getSupportFragmentManager()); //setta il pager
        vpPager.setAdapter(adapterViewPager);


        indicator.setViewPager( vpPager);
        vpPager.setCurrentItem(1);

        help=new HelpDialogManager(this);
        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help.showHelpDialog();
            }
        });
    }

}

