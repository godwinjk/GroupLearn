package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 13-10-2016 21:26 for GroupLearn application.
 */

public class InvitationViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvMessage, tvAccept, tvIgnore;
    public ImageView ivGroupIcon;

    public InvitationViewHolder(View v) {
        super(v);
        tvName = (TextView) v.findViewById(R.id.tv_group_name);
        tvMessage = (TextView) v.findViewById(R.id.tv_group_message);
        tvAccept = (TextView) v.findViewById(R.id.tv_accept);
        tvIgnore = (TextView) v.findViewById(R.id.tv_ignore);
        ivGroupIcon = (ImageView) v.findViewById(R.id.iv_group_icon);
    }
}
