package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.GroupViewHolder;
import com.grouplearn.project.models.GroupModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-05-2016 14:24 for Group Learn application.
 */
public class GroupListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<GroupModel> mGroupList = new ArrayList<>();

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
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = inflater.inflate(R.layout.layout_group_list_item, null);
            holder.initialize(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        GroupModel mGroupModel = mGroupList.get(position);

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
        return convertView;
    }

    public void setGroupListData(ArrayList<GroupModel> listData) {
        this.mGroupList = listData;
    }

    public ArrayList<GroupModel> getGroupListData() {
        return this.mGroupList;
    }
}
