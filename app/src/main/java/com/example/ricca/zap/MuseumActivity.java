package com.example.ricca.zap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.ricca.zap.Data.MuseumMetaData;
import com.example.ricca.zap.Data.MuseumMetaDataWaiter;
import com.example.ricca.zap.GUI.MuseumPager;
import com.example.ricca.zap.GUI.tutorial.HelpDialogManager;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MuseumActivity extends AppCompatActivity implements MuseumMetaDataWaiter {

    FragmentPagerAdapter adapterViewPager;
    HelpDialogManager help;
    Activity self =this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        //build metadata of income referenced museum and ask for them back
        new MuseumMetaData(getIntent().getStringExtra(EXTRA_MESSAGE),this);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;  //disabilita status bar
        decorView.setSystemUiVisibility(uiOptions);

        DotsIndicator indicator=findViewById(R.id.dots_indicator_museum);

        ViewPager pager= findViewById(R.id.museum_pager);
        adapterViewPager= new MuseumPager(getSupportFragmentManager());
        pager.setAdapter(adapterViewPager);

        indicator.setViewPager(pager);
        pager.setCurrentItem(0);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.finish();
            }
        });
    }


    //acquire metadata
    @Override
    public void init(MuseumMetaData metaData)
    {
        //propagate metadata to fragments
        for(Fragment i: getSupportFragmentManager().getFragments())
        {
            ((MuseumMetaDataWaiter)i).init(metaData);
        }
    }

}
