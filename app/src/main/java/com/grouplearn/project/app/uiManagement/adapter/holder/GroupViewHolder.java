package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;


/**
 * Created by Godwin Joseph on 10-05-2016 15:23 for Group Learn application.
 */
public class GroupViewHolder {
    public TextView tvGroupName;
    public ImageView ivGroupIcon;
    public LinearLayout llMain;

    public void initialize(View v) {
        if (v != null) {
            tvGroupName = (TextView) v.findViewById(R.id.tv_group_name);
            ivGroupIcon = (ImageView) v.findViewById(R.id.iv_group_icon);
            llMain = (LinearLayout) v.findViewById(R.id.ll_group_item);
        }
    }
}
