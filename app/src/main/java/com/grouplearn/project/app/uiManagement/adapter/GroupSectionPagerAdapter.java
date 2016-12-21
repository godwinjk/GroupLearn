package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.grouplearn.project.app.uiManagement.contact.ContactListFragment;
import com.grouplearn.project.app.uiManagement.course.MyCourseFragment;
import com.grouplearn.project.app.uiManagement.group.GroupListFragment;

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
            return new MyCourseFragment();
        } else if (position == 1) {
            return new GroupListFragment();
        } else {
            return new ContactListFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if (position == 0) {
            title = "Courses";
        } else if (position == 1) {
            title = "Groups";
        } else {
            title = "People";
        }
        return title;
    }
}
