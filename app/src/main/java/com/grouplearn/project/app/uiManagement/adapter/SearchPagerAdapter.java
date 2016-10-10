package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.grouplearn.project.app.uiManagement.serachManagement.fragment.GoogleSearchFragment;
import com.grouplearn.project.app.uiManagement.serachManagement.fragment.SearchFragment;

/**
 * Created by Godwin Joseph on 03-08-2016 12:02 for Group Learn application.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    public SearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        SearchFragment fragment = new SearchFragment();
        GoogleSearchFragment googleSearchFragment = new GoogleSearchFragment();
        if (position == 0) {
            fragment.setWhichWindow(0);
            return fragment;
        } else
            return googleSearchFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position != 0)
            return "Web";
        else
            return "Groups";
    }
}
