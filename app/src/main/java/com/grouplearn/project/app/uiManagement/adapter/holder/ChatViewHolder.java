package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 25-09-2016 13:29 for Group Learn application.
 */
public class ChatViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout llChatItem, llMessageItem;
    public TextView tvTitle, tvMessage;

    public ChatViewHolder(View v, int viewType) {
        super(v);
        initializeWidgets(v, viewType);
    }

    private void initializeWidgets(View v, int viewType) {
        llMessageItem = (LinearLayout) v.findViewById(R.id.ll_message_item);
        llChatItem = (LinearLayout) v.findViewById(R.id.ll_message_chat_item);
        tvTitle = (TextView) v.findViewById(R.id.tv_chat_who);
        tvMessage = (TextView) v.findViewById(R.id.tv_chat_body);

        if (viewType == 0) {
            llChatItem.setBackgroundResource(R.drawable.chat_bubble_me);
            llMessageItem.setGravity(Gravity.RIGHT);
            tvMessage.setGravity(Gravity.RIGHT);
            tvTitle.setVisibility(View.GONE);
            tvMessage.setTextColor(Color.WHITE);
        } else if (viewType > 0) {
            llChatItem.setBackgroundResource(R.drawable.chat_bubble_other);
            llMessageItem.setGravity(Gravity.LEFT);
            tvMessage.setGravity(Gravity.LEFT);
            tvTitle.setGravity(Gravity.LEFT);
            tvTitle.setVisibility(View.VISIBLE);
//            tvMessage.setTextColor(Color.DKGRAY);
//            tvTitle.setTextColor(Color.DKGRAY);
        }
    }

    private int getTintColor(int id) {
        return 0;
    }
}
