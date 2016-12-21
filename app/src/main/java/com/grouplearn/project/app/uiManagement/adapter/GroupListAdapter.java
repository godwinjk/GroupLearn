package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.GroupViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLGroup;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-05-2016 14:24 for Group Learn application.
 */
public class GroupListAdapter extends BaseAdapter {
    private int mode = 0;
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<GLGroup> mGroupList = new ArrayList<>();
    OnRecyclerItemClickListener onRecyclerItemClickListener;

    public GroupListAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public GroupListAdapter(Context mContext, int mode) {
        this.mode = 1;
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
        int messageCount = mGroupModel.getNewMessage();
        if (messageCount == 0) {
            holder.tvMessageCount.setVisibility(View.GONE);
        } else {
            holder.tvMessageCount.setVisibility(View.VISIBLE);
        }
        holder.tvMessageCount.setText("" + messageCount);
        String lastMesage = mGroupModel.getLastMessage();
        if (TextUtils.isEmpty(lastMesage)) {
            holder.tvLastMessage.setVisibility(View.GONE);
        } else {
            String[] splitArray = lastMesage.split(" : ");
            if (splitArray.length > 1 && splitArray[1].length() > 20) {
                lastMesage = splitArray[0] + " : " + splitArray[1].substring(0, 20) + "...";
            }
            holder.tvLastMessage.setVisibility(View.VISIBLE);
            holder.tvLastMessage.setText(lastMesage);
        }
        if (mode == 1) {
            holder.tvLastMessage.setVisibility(View.GONE);
            holder.tvMessageCount.setVisibility(View.GONE);
        }
//        holder.llMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onRecyclerItemClickListener != null) {
//                    onRecyclerItemClickListener.onItemClicked(position, mGroupModel, holder.ivGroupIcon);
//                }
//            }
//        });
//        holder.llMain.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (onRecyclerItemClickListener != null) {
//                    onRecyclerItemClickListener.onItemLongClicked(position, mGroupModel, holder.ivGroupIcon);
//                }
//                return false;
//            }
//
//        });
        return convertView;
    }

    public void setGroupListData(ArrayList<GLGroup> listData) {
        this.mGroupList = listData;
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
