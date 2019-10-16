package com.example.ricca.zap.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ricca.zap.pager.fragment.Bookmarks;
import com.example.ricca.zap.pager.fragment.History;
import com.example.ricca.zap.pager.fragment.Home;

public class HomePager extends FragmentPagerAdapter {

    public HomePager(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 3;
    }
    // Returns the fragment to display for a particular page.

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return History.newInstance();
            case 1:
                return Home.newInstance();
            default:
                return Bookmarks.newInstance();

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
            default:
                return "Bookmarks";
        }

    }
}

