package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 03-08-2016 11:55 for Group Learn application.
 */
public class SearchViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivIcon;
    public TextView tvName;
    public TextView tvDetails;
    public TextView tvAdd;

    public SearchViewHolder(View v) {
        super(v);
        tvDetails = (TextView) v.findViewById(R.id.tv_details);
        tvName = (TextView) v.findViewById(R.id.tv_item_name);
        ivIcon = (ImageView) v.findViewById(R.id.iv_item_icon);
        tvAdd = (TextView) v.findViewById(R.id.tv_add);
    }
}
