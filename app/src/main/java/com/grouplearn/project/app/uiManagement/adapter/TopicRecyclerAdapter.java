package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.TopicCardViewHolder;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.views.AppAlertDialog;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 15-05-2016 10:26 for Group Learn application.
 */
public class TopicRecyclerAdapter extends RecyclerView.Adapter<TopicCardViewHolder> {
    ArrayList<GroupModel> mTopicList;
    Context mContext;

    public TopicRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<GroupModel> getTopicList() {
        return mTopicList;
    }

    public void setTopicList(ArrayList<GroupModel> mTopicList) {
        this.mTopicList = mTopicList;
        notifyDataSetChanged();
    }

    @Override
    public TopicCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_topic_item, null);
        return new TopicCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(TopicCardViewHolder holder, int position) {
        final GroupModel mTopicModel = mTopicList.get(position);
        holder.setTopicName(mTopicModel.getGroupName());
        holder.tvTopicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertToAddGroup(mTopicModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mTopicList == null)
            return 0;
        return mTopicList.size();
    }

    private void showAlertToAddGroup(final GroupModel model) {
        AppAlertDialog dialog = AppAlertDialog.getAlertDialog(mContext);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("Are you sure want to add this group?");
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
                interactor.addSubscribedGroup(model);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }
}