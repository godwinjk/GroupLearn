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
import com.grouplearn.project.app.uiManagement.adapter.RequestRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupRequestCallback;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.RequestModel;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

public class RequestAcceptingActivity extends BaseActivity implements GroupRequestCallback {
    RecyclerView rvRequests;
    Context mContext;
    RequestRecyclerAdapter mRecyclerAdapter;
    RecyclerView rvRequestView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_acceptiong);
        Toolbar toolbar = setupToolbar("Requests", true);
        mContext = this;

        initializeWidgets();
        registerListeners();


    }

    @Override
    public void initializeWidgets() {
        rvRequestView = (RecyclerView) findViewById(R.id.rv_request_list);
        rvRequestView.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        mRecyclerAdapter = new RequestRecyclerAdapter();
        rvRequestView.setAdapter(mRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequests();
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

    private void getRequests() {
        GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
        interactor.getGroupRequests(this);
    }

    private void updateData(final RequestModel requestModel) {
        CloudOperationCallback callback = new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerAdapter.getRequestModels().remove(requestModel);
                        mRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCloudOperationFailed(AppError error) {

            }
        };
        new CloudGroupManagement(mContext).updateSubscribeGroups(requestModel, callback);
    }

    @Override
    public void onGroupRequestFetchSuccess(ArrayList<RequestModel> requestModels) {
        if (requestModels != null) {
            mRecyclerAdapter.setRequestModels(requestModels);
        }
    }

    @Override
    public void onGroupRequestFetchFailed(AppError error) {

    }
}
