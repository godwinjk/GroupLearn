package com.grouplearn.project.app.uiManagement.groupManagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.InvitationRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupRequestCallback;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.RequestModel;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

public class InvitationActivity extends BaseActivity implements GroupRequestCallback {
    InvitationRecyclerAdapter mRecyclerAdapter;
    RecyclerView rvInvitationList;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Toolbar toolbar = setupToolbar("Invitations", true);
        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        rvInvitationList = (RecyclerView) findViewById(R.id.rv_invitation_list);
        rvInvitationList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        mRecyclerAdapter = new InvitationRecyclerAdapter(mContext);
        rvInvitationList.setAdapter(mRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupListInteractor.getInstance(mContext).getGroupInvitations(this);
    }

    @Override
    public void registerListeners() {
        mRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                RequestModel requestModel = (RequestModel) model;
                if (((TextView) v).getText().toString().equalsIgnoreCase("Accept")) {
                    requestModel.setAction(1);
                    updateData(requestModel);
                } else if (((TextView) v).getText().toString().equalsIgnoreCase("Ignore")) {
                    requestModel.setAction(2);
                    updateData(requestModel);
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    private void updateData(final RequestModel requestModel) {
        CloudOperationCallback callback = new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerAdapter.getInvitationList().remove(requestModel);
                        mRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCloudOperationFailed(AppError error) {

            }
        };
        new CloudGroupManagement(mContext).updateInvitations(requestModel, callback);
    }

    @Override
    public void onGroupRequestFetchSuccess(ArrayList<RequestModel> requestModels) {
        if (requestModels != null && requestModels.size() > 0) {
            mRecyclerAdapter.setInvitationList(requestModels);
        }
    }

    @Override
    public void onGroupRequestFetchFailed(AppError error) {

    }
}
