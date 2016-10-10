package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 05-08-2016 10:35 for Group Learn application.
 */
public class RequestViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvMessage, tvAccept, tvIgnore;
    public ImageView ivProfile;

    public RequestViewHolder(View v, int viewType) {
        super(v);
        if (viewType == 1) {
            tvName = (TextView) v.findViewById(R.id.tv_group_name);
        } else {
            tvName = (TextView) v.findViewById(R.id.tv_user_name);
            tvMessage = (TextView) v.findViewById(R.id.tv_user_message);
            tvAccept = (TextView) v.findViewById(R.id.tv_accept);
            tvIgnore = (TextView) v.findViewById(R.id.tv_ignore);
            ivProfile = (ImageView) v.findViewById(R.id.iv_profile);
        }
    }
}
