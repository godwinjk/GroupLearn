package com.grouplearn.project.app.uiManagement.group;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.grouplearn.project.models.GLRequest;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

public class InvitationActivity extends BaseActivity implements GroupRequestCallback {
    InvitationRecyclerAdapter mRecyclerAdapter;
    RecyclerView rvInvitationList;
    SwipeRefreshLayout srlRecycler;
    TextView tvNoItems;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Invitations");
        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        rvInvitationList = (RecyclerView) findViewById(R.id.rv_invitation_list);
        srlRecycler = (SwipeRefreshLayout) findViewById(R.id.srl_recycler);
        tvNoItems = (TextView) findViewById(R.id.tv_no_items);

        rvInvitationList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        mRecyclerAdapter = new InvitationRecyclerAdapter(mContext);
        rvInvitationList.setAdapter(mRecyclerAdapter);

        int[] colorSchema = new int[]{R.color.pale_rose, R.color.blue, R.color.green, R.color.purple, R.color.majenta, R.color.light_green, R.color.yellow, R.color.pale_red};
        srlRecycler.setColorSchemeColors(colorSchema);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupListInteractor.getInstance(mContext).getGroupInvitations(this);
        srlRecycler.setRefreshing(true);

    }

    @Override
    public void registerListeners() {
        srlRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GroupListInteractor.getInstance(mContext).getGroupInvitations(InvitationActivity.this);

            }
        });
        mRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GLRequest requestModel = (GLRequest) model;
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

    private void updateData(final GLRequest requestModel) {
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
    public void onGroupRequestFetchSuccess(ArrayList<GLRequest> requestModels) {
        srlRecycler.setRefreshing(false);
        if (requestModels != null && requestModels.size() > 0) {
            mRecyclerAdapter.setInvitationList(requestModels);
        }
        if (mRecyclerAdapter.getInvitationList().size() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGroupRequestFetchFailed(AppError error) {
        srlRecycler.setRefreshing(false);
    }
}
