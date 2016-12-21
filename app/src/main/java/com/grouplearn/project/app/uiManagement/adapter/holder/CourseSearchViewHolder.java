package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by WiSilica on 04-12-2016 10:43 for GroupLearn application.
 */

public class CourseSearchViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivCourseIcon;
    public TextView tvSiteAddress, tvCourseName, tvDescription, tvView;
    public LinearLayout llSearchItem;

    public CourseSearchViewHolder(View v) {
        super(v);
        ivCourseIcon = (ImageView) v.findViewById(R.id.iv_item_icon);

        llSearchItem = (LinearLayout) v.findViewById(R.id.ll_search_item);

        tvCourseName = (TextView) v.findViewById(R.id.tv_item_name);
        tvSiteAddress = (TextView) v.findViewById(R.id.tv_site);
        tvDescription = (TextView) v.findViewById(R.id.tv_details);
        tvView = (TextView) v.findViewById(R.id.tv_view);
    }
}