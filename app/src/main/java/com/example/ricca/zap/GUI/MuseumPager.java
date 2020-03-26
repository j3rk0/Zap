package com.example.ricca.zap.GUI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ricca.zap.GUI.fragment.museum.Camera;
import com.example.ricca.zap.GUI.fragment.museum.MuseumInfo;

public class MuseumPager extends FragmentPagerAdapter {
    public MuseumPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return position == 0 ? Camera.newInstance() : MuseumInfo.newIstance();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Camera" : "Info";
    }
}
