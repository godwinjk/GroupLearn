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
    public TextView tvGroupName, tvLastMessage;
    public TextView tvMessageCount;
    public ImageView ivGroupIcon;
    public LinearLayout llMain;

    public void initialize(View v) {
        if (v != null) {
            tvGroupName = (TextView) v.findViewById(R.id.tv_group_name);
            tvMessageCount = (TextView) v.findViewById(R.id.tv_msg_count);
            ivGroupIcon = (ImageView) v.findViewById(R.id.iv_group_icon);
            tvLastMessage = (TextView) v.findViewById(R.id.tv_last_msg);
            llMain = (LinearLayout) v.findViewById(R.id.ll_group_item);
        }
    }
}
