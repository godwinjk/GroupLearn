package com.grouplearn.project.app.uiManagement.contact;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudContactManager;
import com.grouplearn.project.app.uiManagement.interactor.ContactInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class ContactRequestActivity extends BaseActivity implements ContactViewInterface {
    Context mContext;
    RecyclerView rvRequests;
    TextView tvNoItems;
    ContactInteractor interactor;
    SwipeRefreshLayout srlMain;
    ContactRequestAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_request);
        Toolbar toolbar = setupToolbar("Contact Requests", true);
        toolbar.setSubtitle("Contact Requests");
        mContext = this;

                initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        rvRequests = (RecyclerView) findViewById(R.id.rv_request_list);
        rvRequests.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        tvNoItems = (TextView) findViewById(R.id.tv_no_items);
        srlMain = (SwipeRefreshLayout) findViewById(R.id.srl_recycler);

        mAdapter = new ContactRequestAdapter();
        rvRequests.setAdapter(mAdapter);
    }

    public void fetchList() {
        interactor.getRequests(this);
    }

    @Override
    public void registerListeners() {
        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchList();
            }
        });
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GLContact requestModel = (GLContact) model;
                if (((TextView) v).getText().toString().equalsIgnoreCase("Accept")) {
                    requestModel.setAction(1);
                    acceptRequest(requestModel);
                } else if (((TextView) v).getText().toString().equalsIgnoreCase("Ignore")) {
                    requestModel.setAction(2);
                    acceptRequest(requestModel);
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    @Override
    public void onGetAllContactsFromCloud(ArrayList<GLContact> contactModels) {
        srlMain.setRefreshing(false);
        mAdapter.setRequestModels(contactModels);
    }

    @Override
    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {
        srlMain.setRefreshing(false);
    }

    @Override
    public void onGetContactsFailed(AppError error) {
        srlMain.setRefreshing(false);
    }

    private void acceptRequest(final GLContact contact) {
        DisplayInfo.showLoader(mContext, "Please wait");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                mAdapter.getRequestModels().remove(contact);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
            }
        };
        CloudContactManager manager = new CloudContactManager(mContext);
        manager.acceptContactRequest(contact, callback);
    }
}
