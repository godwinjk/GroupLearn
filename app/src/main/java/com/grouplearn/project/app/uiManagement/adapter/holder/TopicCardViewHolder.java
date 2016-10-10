package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;


/**
 * Created by Godwin Joseph on 15-05-2016 15:51 for Group Learn application.
 */
public class TopicCardViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTopicName;

    public TopicCardViewHolder(View itemView) {
        super(itemView);
        tvTopicName = (TextView) itemView.findViewById(R.id.tv_topic_name);
    }

    public void setTopicName(String topicName) {
        tvTopicName.setText("#" + topicName);
    }
}
