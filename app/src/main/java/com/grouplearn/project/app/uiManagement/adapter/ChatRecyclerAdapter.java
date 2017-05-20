package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.file.FileManager;
import com.grouplearn.project.app.uiManagement.adapter.holder.ChatViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLMessage;

import java.util.ArrayList;

/**
 * Created by Godwin on 25-09-2016 13:29 for Group Learn application 17:29 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private ArrayList<GLMessage> messageModels = new ArrayList<>();
    private Context mContext;
    private OnRecyclerItemClickListener listener;

    public ChatRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<GLMessage> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<GLMessage> messageModels) {
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
        GLMessage message = messageModels.get(position);
        String senderName = "Me";
        if (getItemViewType(position) > 0) {
            senderName = message.getSenderName();
        }
        holder.tvTitle.setText(senderName);
        holder.tvMessage.setText(message.getMessageBody());

        setImageAccordingToType(holder, message);

        if (getItemViewType(position) == 1) {
            setDynamicColor(holder.llChatItem, message.getSenderId());
        }
        setProgress(holder, message);
        setEventListeners(position, holder, message);
    }

    private void setProgress(ChatViewHolder holder, GLMessage message) {
        if (message.isOperationOnProgress()) {
            holder.ivDownload.setImageResource(R.drawable.download_cancel);
            holder.pbLoading.setVisibility(View.VISIBLE);
            holder.pbLoading.setProgress(message.getProgress());
        } else {
            holder.ivDownload.setImageResource(R.drawable.download_48);
            holder.pbLoading.setVisibility(View.GONE);
            holder.pbLoading.setProgress(0);
        }
    }

    private void setImageAccordingToType(final ChatViewHolder holder, final GLMessage message) {

        if (message.getMessageType() == GLMessage.IMAGE) {
            String uri = null;
            if (message.getLocalFilePath() != null) {
                uri = message.getLocalFilePath();
                holder.ivDownload.setVisibility(View.GONE);
            } else {
                uri = message.getCloudFilePath();
                holder.ivDownload.setVisibility(View.VISIBLE);
            }

            Glide.with(mContext).load(uri)
                    .asBitmap()
                    .centerCrop()
                    .thumbnail(.2f)
                    .atMost()
                    .fitCenter()
                    .override(200, 200)
                    .placeholder(R.drawable.image_128)
                    .into(new BitmapImageViewTarget(holder.ivThumbNail) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            holder.ivThumbNail.setImageBitmap(resource);
                            saveImage(message.getCloudFilePath(), resource);
//                            notifyDataSetChanged();
                        }
                    });

        } else if (message.getMessageType() == GLMessage.VIDEO) {
            String uri = null;
            if (message.getLocalFilePath() != null) {
                uri = message.getLocalFilePath();
                holder.ivDownload.setVisibility(View.GONE);
            } else {
                uri = message.getCloudFilePath();
                holder.ivDownload.setVisibility(View.VISIBLE);
            }
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Video.Thumbnails.MINI_KIND);
            if (bMap != null)
                holder.ivThumbNail.setImageBitmap(bMap);
        }
    }

    private void saveImage(String cloudPath, Bitmap resource) {
        FileManager manager = new FileManager();
        manager.saveBitmap(getName(cloudPath), resource);
    }

    private String getName(String cloudPath) {
        String[] arr = cloudPath.split("/");
        return arr[arr.length - 1];
    }

    private void setEventListeners(final int position, ChatViewHolder holder, final GLMessage message) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = 0;
                if (v.getId() == R.id.iv_play_button ||
                        v.getId() == R.id.iv_thumbnail)
                    action = 1;
                else if (v.getId() == R.id.iv_download)
                    action = 2;
                if (listener != null) {
                    listener.onItemClicked(position, message, action, v);
                }
            }
        };
        holder.ivPlayButton.setOnClickListener(onClickListener);
        holder.ivThumbNail.setOnClickListener(onClickListener);
        holder.ivDownload.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        GLMessage model = messageModels.get(position);
        return model.getMessageType();
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    private void setDynamicColor(LinearLayout ll, long id) {
        Drawable mDrawable = mContext.getResources().getDrawable(R.drawable.chat_bubble_other);

        int[] colorList = {R.color.a1, R.color.a2, R.color.a3, R.color.a4, R.color.a5, R.color.a6, R.color.a7, R.color.a8, R.color.a9, R.color.a10, R.color.a11, R.color.a12, R.color.a13, R.color.a14, R.color.a15, R.color.a16, R.color.a17, R.color.a18, R.color.a19, R.color.a20};
        int randomColor = (int) Math.abs((id % colorList.length));
        int color = ContextCompat.getColor(mContext, colorList[randomColor]);
        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.LIGHTEN));
        ll.setBackground(mDrawable);
    }
}
