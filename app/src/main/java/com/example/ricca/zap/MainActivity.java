package com.example.ricca.zap;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ricca.zap.pager.HomePager;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


public class MainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;  //disabilita status bar
        decorView.setSystemUiVisibility(uiOptions);


        DotsIndicator indicator=findViewById(R.id.dots_indicator);

        ViewPager vpPager = findViewById(R.id.pager);
        adapterViewPager = new HomePager(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);


        indicator.setViewPager( vpPager);
        vpPager.setCurrentItem(1);
    }
}