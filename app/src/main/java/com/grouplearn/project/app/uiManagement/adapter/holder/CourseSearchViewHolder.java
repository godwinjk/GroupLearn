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
    public TextView  tvCourseName, tvDescription, tvView,tvBadge;
    public LinearLayout llSearchItem;
    public View view;

    public CourseSearchViewHolder(View v) {
        super(v);
        ivCourseIcon = (ImageView) v.findViewById(R.id.iv_item_icon);
        tvBadge = (TextView) v.findViewById(R.id.tv_badge);

        llSearchItem = (LinearLayout) v.findViewById(R.id.ll_search_item);
        view = v.findViewById(R.id.view);

        tvCourseName = (TextView) v.findViewById(R.id.tv_item_name);
        tvDescription = (TextView) v.findViewById(R.id.tv_details);
        tvView = (TextView) v.findViewById(R.id.tv_view);
    }
}
