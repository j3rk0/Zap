package com.example.ricca.zap.GUI.tutorial;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ricca.zap.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class HelpDialogManager {
    private DialogPlus dialog;

    public HelpDialogManager(AppCompatActivity context)
    {
        ViewHolder holder=new ViewHolder(R.layout.dialog_help);
        dialog=DialogPlus.newDialog(context)
                .setCancelable(true)
                .setContentHolder(holder)
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setOverlayBackgroundResource(Color.TRANSPARENT)
                .create();

        View content=holder.getInflatedView();
        final ViewPager pager=content.findViewById(R.id.viewPagerHelp);
        FragmentPagerAdapter adapter=new HelpPager(context.getSupportFragmentManager());
        pager.setAdapter(adapter);
        DotsIndicator indicator=content.findViewById(R.id.dots_indicator_help_dialog);
        indicator.setViewPager(pager);
        pager.setCurrentItem(0);
        content.findViewById(R.id.button_next_help_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem()<=6)
                pager.setCurrentItem(pager.getCurrentItem()+1);
            }
        });
        content.findViewById(R.id.button_prev_help_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem()>0)
                pager.setCurrentItem(pager.getCurrentItem()-1);
            }
        });

    }
    public void showHelpDialog()
    {
        dialog.show();
    }
}

class HelpPager extends FragmentPagerAdapter {

    HelpPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return Pag0.newIstance();

            case 1: return Pag1.newIstance();

            case 2: return Pag2.newIstance();

            case 3: return Pag3.newIstance();

            case 4: return Pag4.newIstance();

            case 5: return Pag5.newIstance();

            default:return Pag6.newIstance();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}

