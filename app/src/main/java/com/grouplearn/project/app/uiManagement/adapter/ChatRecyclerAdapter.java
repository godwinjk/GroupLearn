package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.adapter.holder.ChatViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.AppUtility;

import java.util.ArrayList;

import static com.grouplearn.project.app.file.FileManager.getFileNameFromUri;

/**
 * Created by Godwin on 25-09-2016 13:29 for Group Learn application 17:29 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private ArrayList<GLMessage> messageModels = new ArrayList<>();
    private Context mContext;
    private OnRecyclerItemClickListener listener;
    private long userId = 0;

    public ChatRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
        userId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);
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
        if (getItemViewType(position) < 0) {
            senderName = message.getSenderName();
        }
        holder.tvTitle.setText(senderName);
        holder.tvMessage.setText(message.getMessageBody());

        setImageAccordingToType(holder, message);

        getDocName(holder, message);
        holder.rlMain.setSelected(message.isSelected());
        setDynamicColor(holder, message.getSenderId());
        setProgress(holder, message);
        setEventListeners(position, holder, message);
        try {
            holder.tvTimeStamp.setVisibility(View.VISIBLE);
            double timestamp = Double.parseDouble(message.getTimeStamp());
            setTime(holder.tvTimeStamp, (long) timestamp);
        } catch (NullPointerException | NumberFormatException e) {
            holder.tvTimeStamp.setVisibility(View.GONE);
        }

    }

    private void setTime(TextView textView, long timeInMillies) {
        String s = String.valueOf(AppUtility.getHour(timeInMillies) + ":" + AppUtility.getMin(timeInMillies));
        textView.setText(s);
    }

    private void getDocName(ChatViewHolder holder, GLMessage message) {
        if (message.getMessageType() == GLMessage.DOCUMENT) {
            String uri = "";
            if (message.getLocalFilePath() != null) {
                uri = message.getLocalFilePath();
            } else {
                uri = message.getCloudFilePath();
            }
            holder.tvDocName.setText(getFileNameFromUri(uri));
            setDocImage(holder, message);
        }
    }

    private void setDocImage(ChatViewHolder holder, GLMessage message) {

    }

    private void setProgress(ChatViewHolder holder, GLMessage message) {
        if (message.isOperationOnProgress()) {
            holder.ivDownload.setImageResource(R.drawable.download_cancel);
            holder.pbLoading.setVisibility(View.VISIBLE);
            if (message.getProgress() == -1) {
                holder.pbLoading.setIndeterminate(true);
            } else {
                if (message.getProgress() == 1) {
                    ResizeWidthAnimation animation = new ResizeWidthAnimation(holder.rlDownload,
                            (int) AppUtility.dp2px(mContext.getResources(), 150));
                    animation.setDuration(1200);
                    holder.rlDownload.startAnimation(animation);
                }
                holder.pbLoading.setIndeterminate(false);
                holder.pbLoading.setProgress(message.getProgress());
            }
        } else {
            if (message.getProgress() == 100) {
                ResizeWidthAnimation animation = new ResizeWidthAnimation(holder.rlDownload, 0);
                animation.setDuration(600);
                holder.rlDownload.startAnimation(animation);
            }
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
                    .fitCenter()
                    .override(200, 200)
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
        } else if (message.getMessageType() == GLMessage.DOCUMENT) {
            if (message.getLocalFilePath() != null) {
                holder.ivDownload.setVisibility(View.GONE);
            } else {
                holder.ivDownload.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setEventListeners(final int position, ChatViewHolder holder, final GLMessage message) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = 0;
                if (v.getId() == R.id.iv_play_button ||
                        v.getId() == R.id.iv_thumbnail ||
                        v.getId() == R.id.ll_doc) {
                    action = 1;
                } else if (v.getId() == R.id.iv_download) {
                    action = 2;
                } else if (v.getId() == R.id.tv_title) {
                    action = 3;
                }
                if (listener != null) {
                    listener.onItemClicked(position, message, action, v);
                }
            }
        };
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onItemLongClicked(position, message, -1, v);
                }
                return true;
            }
        };
        holder.ivPlayButton.setOnClickListener(onClickListener);
        holder.ivThumbNail.setOnClickListener(onClickListener);
        holder.ivDownload.setOnClickListener(onClickListener);
        holder.rlMain.setOnClickListener(onClickListener);
        holder.llDoc.setOnClickListener(onClickListener);

        holder.tvTitle.setOnClickListener(onClickListener);

        holder.rlMain.setOnLongClickListener(longClickListener);
        holder.ivThumbNail.setOnLongClickListener(longClickListener);
        holder.ivPlayButton.setOnLongClickListener(longClickListener);
        holder.llDoc.setOnLongClickListener(longClickListener);
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        GLMessage model = messageModels.get(position);
        if (model.getSenderId() == userId)
            return model.getMessageType();
        else return model.getMessageType() * -1;
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public ArrayList<GLMessage> getSelectedItems() {
        ArrayList<GLMessage> messages = new ArrayList<>();
        for (GLMessage message : messageModels) {
            if (message.isSelected())
                messages.add(message);
        }
        return messages;
    }

    public GLMessage toggleSelection(int position, GLMessage message) {
        message.setSelected(!message.isSelected());
        notifyItemChanged(position, message);
        return message;
    }

    private void setDynamicColor(ChatViewHolder holder, long id) {
//        Drawable mDrawable = mContext.getResources().getDrawable(R.drawable.chat_bubble_other);

        int[] colorList = {/*R.color.white
                ,*/ R.color.a1, R.color.a2, R.color.a3, R.color.a4, R.color.a5, R.color.a6, R.color.a7, R.color.a8, R.color.a9, R.color.a10, R.color.a11, R.color.a12, R.color.a13, R.color.a14, R.color.a15, R.color.a16, R.color.a17, R.color.a18, R.color.a19, R.color.a20};
        int randomColor = (int) Math.abs((id % colorList.length));
        int color = ContextCompat.getColor(mContext, colorList[randomColor]);
//        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.LIGHTEN));
        holder.tvTitle.setTextColor(color);
    }

    public void deSelectAll() {
        for (GLMessage message : messageModels) {
            message.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public class ResizeWidthAnimation extends Animation {
        private int mWidth;
        private int mStartWidth;
        private View mView;

        public ResizeWidthAnimation(View view, int width) {
            mView = view;
            mWidth = width;
            mStartWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

            mView.getLayoutParams().width = newWidth;
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
