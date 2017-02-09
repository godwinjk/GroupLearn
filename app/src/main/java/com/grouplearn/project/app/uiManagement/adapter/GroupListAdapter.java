package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.GroupViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLGroup;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Godwin Joseph on 07-05-2016 14:24 for Group Learn application.
 */
public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<GLGroup> mGroupList = new ArrayList<>();
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public GroupListAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = inflater.inflate(R.layout.layout_group_list_item, null);
            holder.initialize(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        final GLGroup mGroupModel = mGroupList.get(position);

        holder.tvGroupName.setText(mGroupModel.getGroupName());



        String imageUri = mGroupModel.getIconUrl();
        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
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
        return convertView;
    }

    public void setGroupListData(ArrayList<GLGroup> listData) {
        if (this.mGroupList.size() <= 0) {
            this.mGroupList = (ArrayList<GLGroup>) listData.clone();
            return;
        }
        ArrayList<GLGroup> tempGroupArrayList = new ArrayList<>();

        for (Iterator<GLGroup> iterator = listData.iterator(); iterator.hasNext(); ) {
            GLGroup tempGroup = iterator.next();
            for (GLGroup group : mGroupList) {
                if (tempGroup.getGroupUniqueId() == group.getGroupUniqueId()) {
                    iterator.remove();
                } /*else {
                    tempGroupArrayList.add(tempGroup);
                }*/
            }
        }
        this.mGroupList.addAll(listData);
        notifyDataSetChanged();
    }

    public ArrayList<GLGroup> getGroupListData() {
        return this.mGroupList;
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }
}
