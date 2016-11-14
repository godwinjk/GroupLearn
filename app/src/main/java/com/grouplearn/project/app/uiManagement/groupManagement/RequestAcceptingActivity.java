package com.grouplearn.project.app.uiManagement.groupManagement;

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
    Context mContext;
    RequestRecyclerAdapter mRecyclerAdapter;
    RecyclerView rvRequestView;
    SwipeRefreshLayout srlRecycler;
    TextView tvNoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_acceptiong);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Requests");
        mContext = this;

        initializeWidgets();
        registerListeners();


    }

    @Override
    public void initializeWidgets() {
        rvRequestView = (RecyclerView) findViewById(R.id.rv_request_list);
        rvRequestView.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        srlRecycler = (SwipeRefreshLayout) findViewById(R.id.srl_recycler);
        tvNoItems = (TextView) findViewById(R.id.tv_no_items);

        mRecyclerAdapter = new RequestRecyclerAdapter();
        rvRequestView.setAdapter(mRecyclerAdapter);

        int[] colorSchema = new int[]{R.color.pale_rose, R.color.blue, R.color.green, R.color.purple, R.color.majenta, R.color.light_green, R.color.yellow, R.color.pale_red};
        srlRecycler.setColorSchemeColors(colorSchema);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequests();
        srlRecycler.setRefreshing(true);
    }

    @Override
    public void registerListeners() {
        srlRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequests();

            }
        });
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
        srlRecycler.setRefreshing(false);
        if (requestModels != null) {
            mRecyclerAdapter.setRequestModels(requestModels);
        }
        if (mRecyclerAdapter.getRequestModels().size() > 0) {
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
