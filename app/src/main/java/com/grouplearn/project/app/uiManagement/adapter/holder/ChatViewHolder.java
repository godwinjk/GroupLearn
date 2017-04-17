package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Godwin Joseph on 25-09-2016 13:29 for Group Learn application.
 */
public class ChatViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout llChatItem, llMessageItem, llThumbNail;
    public TextView tvTitle;
    public EmojiconTextView tvMessage;
    public ImageView ivThumbNail, ivPlayButton;
    public ContentLoadingProgressBar pbLoading;

    public ChatViewHolder(View v, int viewType) {
        super(v);
        initializeWidgets(v, viewType);
    }

    private void initializeWidgets(View v, int viewType) {
        llMessageItem = (LinearLayout) v.findViewById(R.id.ll_message_item);
        llChatItem = (LinearLayout) v.findViewById(R.id.ll_message_chat_item);
        llThumbNail = (LinearLayout) v.findViewById(R.id.ll_image_thumbnail);

        tvTitle = (TextView) v.findViewById(R.id.tv_chat_who);
        tvMessage = (EmojiconTextView) v.findViewById(R.id.tv_chat_body);

        ivPlayButton = (ImageView) v.findViewById(R.id.iv_play_button);
        ivThumbNail = (ImageView) v.findViewById(R.id.iv_thumbnail);

        pbLoading = (ContentLoadingProgressBar) v.findViewById(R.id.pb_loading);
        pbLoading.setVisibility(View.GONE);
        int tempViewType = Math.abs(viewType);
        if (viewType > 0) {
            llChatItem.setBackgroundResource(R.drawable.chat_bubble_me);
            llMessageItem.setGravity(Gravity.RIGHT);
            llThumbNail.setGravity(Gravity.RIGHT);
            tvMessage.setGravity(Gravity.RIGHT);
            tvTitle.setVisibility(View.GONE);
        } else {
            llChatItem.setBackgroundResource(R.drawable.chat_bubble_other);
            llMessageItem.setGravity(Gravity.LEFT);
            llThumbNail.setGravity(Gravity.LEFT);
            tvMessage.setGravity(Gravity.LEFT);
            tvTitle.setGravity(Gravity.LEFT);
            tvTitle.setVisibility(View.VISIBLE);
        }
        if (tempViewType == 1) {
            llChatItem.setVisibility(View.VISIBLE);
            llThumbNail.setVisibility(View.GONE);
        } else if (tempViewType == 2) {
            llChatItem.setVisibility(View.GONE);
            llThumbNail.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.GONE);
            ivThumbNail.setImageResource(R.drawable.image_128);
        } else if (tempViewType == 3) {
            llChatItem.setVisibility(View.GONE);
            llThumbNail.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.VISIBLE);
            ivThumbNail.setVisibility(View.VISIBLE);
            ivThumbNail.setImageResource(R.drawable.video_128);
        } else if (tempViewType == 4) {
            llChatItem.setVisibility(View.GONE);
            llThumbNail.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.GONE);
            ivThumbNail.setVisibility(View.VISIBLE);
            ivThumbNail.setImageResource(R.drawable.docs_128);
        }
    }
}
