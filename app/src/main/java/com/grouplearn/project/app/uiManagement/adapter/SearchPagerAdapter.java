package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.grouplearn.project.app.uiManagement.serachManagement.fragment.GoogleSearchFragment;
import com.grouplearn.project.app.uiManagement.serachManagement.fragment.SearchFragment;
import com.grouplearn.project.app.uiManagement.serachManagement.fragment.UserSearchFragment;

/**
 * Created by Godwin Joseph on 03-08-2016 12:02 for Group Learn application.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    public SearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        GoogleSearchFragment googleSearchFragment = new GoogleSearchFragment();
        if (position == 0) {
            fragment = new UserSearchFragment();
            return fragment;
        } else if (position == 1) {
            fragment = new SearchFragment();
            return fragment;
        } else
            return googleSearchFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Users";
        else if (position == 1)
            return "Groups";
        else if (position == 2) {
            return "Web";
        }
        return "Search";
    }
}
