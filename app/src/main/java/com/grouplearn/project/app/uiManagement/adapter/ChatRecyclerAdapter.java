package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.ChatViewHolder;
import com.grouplearn.project.bean.GLMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Godwin on 25-09-2016 13:29 for Group Learn application 17:29 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private ArrayList<GLMessage> messageModels = new ArrayList<>();
    private Context mContext;

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
        setEventListeners(holder, message);
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

    private void setImageAccordingToType(ChatViewHolder holder, GLMessage message) {

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
                    .placeholder(R.drawable.image_128)
                    .into(holder.ivThumbNail);

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

    private void setEventListeners(ChatViewHolder holder, final GLMessage message) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localPath = message.getLocalFilePath();
                if (localPath != null) {
                    try {
                        openFile(localPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        holder.ivPlayButton.setOnClickListener(onClickListener);
        holder.ivThumbNail.setOnClickListener(onClickListener);
    }

    public void openFile(String url) throws IOException {
        // Create URI

        Uri uri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // ZIP Files
            intent.setDataAndType(uri, "application/zip");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
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

    private void setDynamicColor(LinearLayout ll, long id) {
        Drawable mDrawable = mContext.getResources().getDrawable(R.drawable.chat_bubble_other);

        int[] colorList = {R.color.a1, R.color.a2, R.color.a3, R.color.a4, R.color.a5, R.color.a6, R.color.a7, R.color.a8, R.color.a9, R.color.a10, R.color.a11, R.color.a12, R.color.a13, R.color.a14, R.color.a15, R.color.a16, R.color.a17, R.color.a18, R.color.a19, R.color.a20};
        int randomColor = (int) Math.abs((id % colorList.length));
        int color = ContextCompat.getColor(mContext, colorList[randomColor]);
        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.LIGHTEN));
        ll.setBackground(mDrawable);
    }
}
