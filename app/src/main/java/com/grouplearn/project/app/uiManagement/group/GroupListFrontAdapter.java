package com.grouplearn.project.app.uiManagement.group;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by WiSilica on 21-01-2017 14:04.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class GroupListFrontAdapter extends RecyclerView.Adapter<GroupFrontViewHolder> {
    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private ArrayList<GLGroup> mGroupList = new ArrayList<>();
    private long myUserId;

    public GroupListFrontAdapter(Context mContext) {
        myUserId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);
    }

    @Override
    public GroupFrontViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_main_item, null);
        GroupFrontViewHolder holder = new GroupFrontViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupFrontViewHolder holder, final int position) {
        final GLGroup mGroupModel = mGroupList.get(position);
        String lastMessage = mGroupModel.getLastMessage();
        if (myUserId == mGroupModel.getGroupAdminId()) {
            holder.tvBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }
        if (mGroupModel.isMine()) {
            holder.tvRequest.setVisibility(View.GONE);
        } else {
            holder.tvBadge.setVisibility(View.GONE);
            holder.tvRequest.setVisibility(View.VISIBLE);
            lastMessage = mGroupModel.getGroupDescription();
        }
        holder.tvGroupName.setText(mGroupModel.getGroupName());
        int messageCount = mGroupModel.getNewMessage();
        if (messageCount == 0) {
            holder.tvMessageCount.setVisibility(View.GONE);
        } else {
            holder.tvMessageCount.setVisibility(View.VISIBLE);
        }
        holder.tvMessageCount.setText("" + messageCount);

        final Context mContext = holder.itemView.getContext();
        String imageUri = mGroupModel.getIconUrl();
        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.group_person)
                    .thumbnail(.2f)
                    .into(new BitmapImageViewTarget(holder.ivGroupIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.ivGroupIcon.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            holder.ivGroupIcon.setImageResource(R.drawable.group_person);
        }

        if (TextUtils.isEmpty(lastMessage)) {
            holder.tvLastMessage.setVisibility(View.GONE);
        } else {
            String[] splitArray = lastMessage.split(" : ");
            if (splitArray.length > 1 && splitArray[1].length() > 20) {
                lastMessage = splitArray[0] + " : " + splitArray[1].substring(0, 20) + "...";
            }
            holder.tvLastMessage.setVisibility(View.VISIBLE);
            holder.tvLastMessage.setText(lastMessage);
        }
        holder.tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, mGroupModel, holder.tvRequest);
                }
            }
        });
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, mGroupModel, holder.ivGroupIcon);
                }
            }
        });
        holder.llMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemLongClicked(position, mGroupModel, holder.ivGroupIcon);
                }
                return false;
            }

        });
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public ArrayList<GLGroup> getGroupList() {
        return mGroupList;
    }

    public void setGroupList(ArrayList<GLGroup> listData) {
        if (this.mGroupList.size() <= 0) {
            this.mGroupList = (ArrayList<GLGroup>) listData.clone();
            notifyDataSetChanged();
            return;
        }

        for (Iterator<GLGroup> iterator = listData.iterator(); iterator.hasNext(); ) {
            GLGroup tempGroup = iterator.next();
            for (GLGroup group : mGroupList) {
                if (tempGroup.getGroupUniqueId() == group.getGroupUniqueId()) {
                    group.setIconUrl(tempGroup.getIconUrl());
                    group.setLastMessage(tempGroup.getLastMessage());
                    group.setMessageModel(tempGroup.geMessageModel());
                    group.setNewMessage(tempGroup.getNewMessage());
                    group.setGroupName(tempGroup.getGroupName());
                    group.setGroupDescription(tempGroup.getGroupDescription());

                    iterator.remove();
                }
            }
        }
        if (listData != null && listData.size() > 0) {
            this.mGroupList.addAll(listData);
        }
        mGroupList = sort(mGroupList);
        notifyDataSetChanged();
    }

    private ArrayList<GLGroup> sort(ArrayList<GLGroup> listData) {
        Collections.sort(listData);
        return mGroupList;
    }

    public void removeItem(GLGroup group) {
        mGroupList.remove(group);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    public void clearAndInsert() {
        ArrayList<GLGroup> notMineList = new ArrayList<>();
        ListIterator<GLGroup> groupListIterator = mGroupList.listIterator();
        for (; groupListIterator.hasNext(); ) {
            GLGroup group = groupListIterator.next();
            if (!group.isMine()) {
                notMineList.add(group);
                groupListIterator.remove();
            }
        }
        notifyDataSetChanged();
        setGroupList(notMineList);
    }
}
