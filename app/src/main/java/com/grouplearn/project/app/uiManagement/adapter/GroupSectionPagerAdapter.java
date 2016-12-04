package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.grouplearn.project.app.uiManagement.group.GroupListFragment;
import com.grouplearn.project.app.uiManagement.search.fragment.SearchFragment;

/**
 * Created by Godwin Joseph on 13-11-2016 15:51 for GroupLearn application.
 */

public class GroupSectionPagerAdapter extends FragmentStatePagerAdapter {
    public GroupSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SearchFragment();
        } else {
            return new GroupListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if (position == 0) {
            title = "Search Groups";
        } else {
            title = "Subscribed Groups";
        }
        return title;
    }
}
