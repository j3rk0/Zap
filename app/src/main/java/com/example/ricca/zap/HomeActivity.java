package com.example.ricca.zap;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ricca.zap.GUI.HomePager;
import com.example.ricca.zap.GUI.fragment.home.Home;
import com.example.ricca.zap.GUI.tutorial.HelpDialogManager;
import com.example.ricca.zap.Services.ConnectionListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


public class HomeActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    HelpDialogManager help;
    private ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new ConnectionListener(this)).start();  //crea un monitor per la connessione

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;  //disabilita status bar
        decorView.setSystemUiVisibility(uiOptions);


        DotsIndicator indicator=findViewById(R.id.dots_indicator_home);

        vpPager = findViewById(R.id.pager);
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

    @Override
    public void onBackPressed()
    {
        if(vpPager.getCurrentItem()==1) {
            Home pageHome=((Home)getSupportFragmentManager().getFragments().get(0));

            if(pageHome.isListOpen())
                pageHome.setVisibilyListOff();

        }else super.onBackPressed();

    }

}

