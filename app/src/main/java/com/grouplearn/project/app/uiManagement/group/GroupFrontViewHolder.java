package com.grouplearn.project.app.uiManagement.group;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by WiSilica on 21-01-2017 14:06.
 *
 * @Author : Godwin Joseph Kurinjikattu
 */

public class GroupFrontViewHolder extends RecyclerView.ViewHolder {

    public TextView tvGroupName, tvMessageCount, tvRequest, tvBadge;
    public EmojiconTextView tvLastMessage;
    public ImageView ivGroupIcon;
    public LinearLayout llMain;

    public GroupFrontViewHolder(View v) {
        super(v);
        tvGroupName = (TextView) v.findViewById(R.id.tv_group_name);
        tvMessageCount = (TextView) v.findViewById(R.id.tv_msg_count);
        ivGroupIcon = (ImageView) v.findViewById(R.id.iv_group_icon);
        tvLastMessage = (EmojiconTextView) v.findViewById(R.id.tv_last_msg);
        tvRequest = (TextView) v.findViewById(R.id.tv_group_request);
        llMain = (LinearLayout) v.findViewById(R.id.ll_group_item);
        tvBadge = (TextView) v.findViewById(R.id.tv_badge);
    }
}
