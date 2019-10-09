package com.example.ricca.zap.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ricca.zap.fragment.Bookmarks;
import com.example.ricca.zap.fragment.History;
import com.example.ricca.zap.fragment.Home;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
    // Returns the fragment to display for a particular page.

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return History.newInstance();
            case 1:
                return Home.newInstance();
            case 2:
                return Bookmarks.newInstance();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "History";
            case 1:
                return "Zap!";
            case 2:
                return "Bookmarks";
            default:
                return null;
        }

    }
}

