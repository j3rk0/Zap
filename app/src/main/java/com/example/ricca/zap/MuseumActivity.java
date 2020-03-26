package com.example.ricca.zap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.ricca.zap.GUI.MuseumPager;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class MuseumActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum3);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;  //disabilita status bar
        decorView.setSystemUiVisibility(uiOptions);

        DotsIndicator indicator=findViewById(R.id.dots_indicator_museum);

        ViewPager pager= findViewById(R.id.museum_pager);
        adapterViewPager= new MuseumPager(getSupportFragmentManager());
        pager.setAdapter(adapterViewPager);

        indicator.setViewPager(pager);
        pager.setCurrentItem(0);
    }
}
