package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by WiSilica on 04-12-2016 20:35 for GroupLearn application.
 */

public class InterestViewHolder extends RecyclerView.ViewHolder {
    public TextView tvInterest;
    public ImageView ivClose;

    public InterestViewHolder(View v) {
        super(v);
        tvInterest = (TextView) v.findViewById(R.id.tv_interest_name);
        ivClose = (ImageView) v.findViewById(R.id.iv_close);
    }
}
