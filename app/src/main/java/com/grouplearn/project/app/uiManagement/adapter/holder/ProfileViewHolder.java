package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 03-08-2016 17:20 for Group Learn application.
 */
public class ProfileViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivIcon;
    public TextView tvName;
    public TextView tvDetails;
    public LinearLayout llItem;

    public ProfileViewHolder(View v) {
        super(v);
        tvDetails = (TextView) v.findViewById(R.id.tv_details);
        tvName = (TextView) v.findViewById(R.id.tv_item_name);
        ivIcon = (ImageView) v.findViewById(R.id.iv_item_icon);
        llItem = (LinearLayout) v.findViewById(R.id.ll_search_item);
    }
}
