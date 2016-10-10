package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.ChatViewHolder;
import com.grouplearn.project.models.MessageModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 25-09-2016 13:29 for Group Learn application.
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    Context mContext;
    long myUserId;

    public ChatRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
        notifyDataSetChanged();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_item, null);
        ChatViewHolder holder = new ChatViewHolder(v, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        MessageModel model = messageModels.get(position);
        String senderName = "Me";
        if (getItemViewType(position) > 0) {
            senderName = model.getSenderName();
        }
        holder.tvTitle.setText(senderName);
        holder.tvMessage.setText(model.getMessageBody());
        if (getItemViewType(position) == 1) {
            setDynamicColor(holder.llChatItem, model.getSenderId());
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel model = messageModels.get(position);
        int senderId = (int) model.getSenderId();
        int viewType = 0;
        if (senderId != myUserId) {
            viewType = 1;
        }
        return viewType;
    }

    private void setDynamicColor(LinearLayout ll, long id) {
        Drawable mDrawable = mContext.getResources().getDrawable(R.drawable.chat_bubble_other);

        int[] colorList = {R.color.a1, R.color.a2, R.color.a3, R.color.a4, R.color.a5, R.color.a6, R.color.a7, R.color.a8, R.color.a9, R.color.a10, R.color.a11, R.color.a12, R.color.a13, R.color.a14, R.color.a15, R.color.a16, R.color.a17, R.color.a18, R.color.a19, R.color.a20};
        int randomColor = (int) Math.abs((id % colorList.length));
        int color = ContextCompat.getColor(mContext, colorList[randomColor]);
        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.LIGHTEN));
        ll.setBackground(mDrawable);
    }

    public long getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(long myUserId) {
        this.myUserId = myUserId;
    }
}
