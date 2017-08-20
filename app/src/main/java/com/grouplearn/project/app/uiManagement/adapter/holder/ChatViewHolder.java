package com.grouplearn.project.app.uiManagement.adapter.holder;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grouplearn.project.R;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Godwin Joseph on 25-09-2016 13:29 for Group Learn application.
 */
public class ChatViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout llChatItem, llMessageItem, llThumbNail, llDoc;
    public RelativeLayout rlThumbNail, rlMain, rlDownload;
    public TextView tvTitle, tvSize, tvDocName, tvTimeStamp;
    public EmojiconTextView tvMessage;
    public ImageView ivThumbNail, ivPlayButton, ivDownload, ivDocImage;
    public ContentLoadingProgressBar pbLoading;

    public ChatViewHolder(View v, int viewType) {
        super(v);
        initializeWidgets(v, viewType);
    }

    private void initializeWidgets(View v, int viewType) {
        llMessageItem = (LinearLayout) v.findViewById(R.id.ll_message_item);
        llChatItem = (LinearLayout) v.findViewById(R.id.ll_message_chat_item);
        llThumbNail = (LinearLayout) v.findViewById(R.id.ll_image_thumbnail);
        llDoc = (LinearLayout) v.findViewById(R.id.ll_doc);

        rlThumbNail = (RelativeLayout) v.findViewById(R.id.rl_thumbnail);
        rlDownload = (RelativeLayout) v.findViewById(R.id.rl_download);
        rlMain = (RelativeLayout) v.findViewById(R.id.rl_main);

        tvTitle = (TextView) v.findViewById(R.id.tv_chat_who);
        tvSize = (TextView) v.findViewById(R.id.tv_size);
        tvDocName = (TextView) v.findViewById(R.id.tv_doc_name);
        tvMessage = (EmojiconTextView) v.findViewById(R.id.tv_chat_body);
        tvTimeStamp = (TextView) v.findViewById(R.id.tv_time_stamp);

        ivPlayButton = (ImageView) v.findViewById(R.id.iv_play_button);
        ivThumbNail = (ImageView) v.findViewById(R.id.iv_thumbnail);
        ivDownload = (ImageView) v.findViewById(R.id.iv_download);
        ivDocImage = (ImageView) v.findViewById(R.id.iv_doc);

        pbLoading = (ContentLoadingProgressBar) v.findViewById(R.id.pb_loading);
        pbLoading.setVisibility(View.GONE);
        int tempViewType = Math.abs(viewType);
        if (viewType > 0) {
            llChatItem.setBackgroundResource(R.drawable.chat_bubble_other);
            llMessageItem.setGravity(Gravity.RIGHT);
            llThumbNail.setGravity(Gravity.RIGHT);
            tvMessage.setGravity(Gravity.RIGHT);
            tvTitle.setVisibility(View.GONE);
            ivDownload.setVisibility(View.GONE);
        } else {
            llChatItem.setBackgroundResource(R.drawable.chat_bubble_other);
            llMessageItem.setGravity(Gravity.LEFT);
            llThumbNail.setGravity(Gravity.LEFT);
            tvMessage.setGravity(Gravity.LEFT);
            tvTitle.setGravity(Gravity.LEFT);
            tvTitle.setVisibility(View.VISIBLE);
            ivDownload.setVisibility(View.VISIBLE);
        }
        if (tempViewType == 1) {
            llChatItem.setVisibility(View.VISIBLE);
            llThumbNail.setVisibility(View.GONE);
        } else if (tempViewType == 2) {
            tvMessage.setVisibility(View.GONE);
            llThumbNail.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.GONE);
            llDoc.setVisibility(View.GONE);
            ivThumbNail.setImageResource(R.drawable.image_128);
        } else if (tempViewType == 3) {
            tvMessage.setVisibility(View.GONE);
            llThumbNail.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.VISIBLE);
            ivThumbNail.setVisibility(View.VISIBLE);
            llDoc.setVisibility(View.GONE);
            ivThumbNail.setImageResource(R.drawable.video_128);
        } else if (tempViewType == 4) {
            tvMessage.setVisibility(View.GONE);
            llThumbNail.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.GONE);
            ivThumbNail.setVisibility(View.GONE);
            llDoc.setVisibility(View.VISIBLE);
        }
    }
}
