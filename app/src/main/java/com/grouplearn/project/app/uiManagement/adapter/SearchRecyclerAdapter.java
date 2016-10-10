package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.SearchViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.ContactModel;
import com.grouplearn.project.models.GroupModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-08-2016 11:55 for Group Learn application.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    public final static int GROUP_LIST = 0;
    public final static int USER_LIST = 1;
    ArrayList<GroupModel> groupList = new ArrayList<>();
    ArrayList<ContactModel> userList = new ArrayList<>();
    int whichAdapter = 0;

    OnRecyclerItemClickListener mItemClickListener;

    public SearchRecyclerAdapter(int whichAdapter) {
        this.whichAdapter = whichAdapter;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_item, null);
        SearchViewHolder holder = new SearchViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        if (getItemViewType(position) == 0) {
            GroupModel groupModel = groupList.get(position);
            holder.tvName.setText(groupModel.getGroupName());
            holder.tvDetails.setText(groupModel.getGroupDescription());
            holder.tvAdd.setText("Request");
        } else {
            ContactModel contactModel = userList.get(position);
            holder.tvName.setText(contactModel.getContactName());
            holder.tvDetails.setText(contactModel.getContactStatus());
            holder.tvAdd.setText("Add to contact");
        }

        if (mItemClickListener != null) {
            holder.tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemViewType(position) == 0) {
                        mItemClickListener.onItemClicked(position, groupList.get(position), v);
                    } else {
                        mItemClickListener.onItemClicked(position, userList.get(position), v);
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

    public ArrayList<GroupModel> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<GroupModel> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    public ArrayList<ContactModel> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<ContactModel> userList) {
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
