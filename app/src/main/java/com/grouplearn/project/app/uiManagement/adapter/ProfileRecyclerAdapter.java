package com.grouplearn.project.app.uiManagement.adapter;

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
import com.grouplearn.project.app.uiManagement.adapter.holder.ProfileViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLUser;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-08-2016 17:20 for Group Learn application.
 */
public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileViewHolder> {
    public final static int GROUP_LIST = 0;
    public final static int USER_LIST = 1;
    ArrayList<GLGroup> groupList = new ArrayList<>();
    ArrayList<GLUser> userList = new ArrayList<>();
    int whichAdapter = 0;

    OnRecyclerItemClickListener mItemClickListener;

    public ProfileRecyclerAdapter(int whichAdapter) {
        this.whichAdapter = whichAdapter;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_profile_recycler_item, null);
        ProfileViewHolder holder = new ProfileViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, final int position) {
        if (getItemViewType(position) == 0) {
            GLGroup groupModel = groupList.get(position);
            holder.tvName.setText(groupModel.getGroupName());
            holder.tvDetails.setText(groupModel.getGroupDescription());
        } else {
            GLUser userModel = userList.get(position);
            holder.tvName.setText(userModel.getUserDisplayName());
            holder.tvDetails.setText(userModel.getUserStatus());

            final Context mContext = holder.itemView.getContext();
            String imageUri = userModel.getIconUrl();
            if (!TextUtils.isEmpty(imageUri)) {
                Glide.with(mContext)
                        .load(imageUri)
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(holder.ivIcon) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.ivIcon.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } else {
                holder.ivIcon.setImageResource(R.drawable.group_person);
            }
        }
        if (mItemClickListener != null) {
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whichAdapter == 0) {
                        mItemClickListener.onItemClicked(position, getGroupList().get(position), v);
                    } else {
                        mItemClickListener.onItemClicked(position, getUserList().get(position), v);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (whichAdapter == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public int getItemCount() {
        if (whichAdapter == 0)
            return groupList.size();
        else
            return userList.size();
    }

    public int getWhichAdapter() {
        return whichAdapter;
    }

    public void setWhichAdapter(int whichAdapter) {
        this.whichAdapter = whichAdapter;
    }

    public ArrayList<GLGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<GLGroup> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    public ArrayList<GLUser> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<GLUser> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public OnRecyclerItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    public void setItemClickListener(OnRecyclerItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
