package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.SearchViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.GLGroup;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-08-2016 11:55 for Group Learn application.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    ArrayList<GLGroup> groupList = new ArrayList<>();

    OnRecyclerItemClickListener mItemClickListener;

    public SearchRecyclerAdapter() {

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
            GLGroup groupModel = groupList.get(position);
            holder.tvName.setText(groupModel.getGroupName());
            holder.tvDetails.setText(groupModel.getGroupDescription());
            holder.tvAdd.setText("Request");
        }

        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClicked(position, groupList.get(position), v);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupList.size();
    }


    public ArrayList<GLGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<GLGroup> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    public OnRecyclerItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    public void setItemClickListener(OnRecyclerItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
