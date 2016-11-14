package com.grouplearn.project.app.uiManagement.serachManagement;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.SearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

public class SearchGroupsActivity extends BaseActivity implements View.OnClickListener, OnRecyclerItemClickListener, GroupViewInterface {
    private static final String TAG = "";
    Context mContext;
    FloatingActionButton fab;
    SearchRecyclerAdapter mRecyclerAdapter;
    SearchView svSearchView;
    RecyclerView rvSearchList;
    CardView cvSearch;
    TextView tvNoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_groups);
        mToolbar = setupToolbar("GroupLearn", true);
        mToolbar.setSubtitle("Search Groups");

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerAdapter = new SearchRecyclerAdapter();

        cvSearch = (CardView) findViewById(R.id.cv_search);

        svSearchView = (SearchView) findViewById(R.id.sv_items);
        svSearchView.setIconifiedByDefault(true);

        tvNoItems = (TextView) findViewById(R.id.tv_no_items);

        rvSearchList = (RecyclerView) findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSearchList.setAdapter(mRecyclerAdapter);

        EditText searchText = ((EditText) svSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchText.setBackground(null);
        searchText.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHint("eg: Java, Android, iOS");

        svSearchView.performClick();
        fab.setVisibility(View.GONE);
    }

    @Override
    public void registerListeners() {
        fab.setOnClickListener(this);
        mRecyclerAdapter.setItemClickListener(this);
        svSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                animate();
            }
        });
        svSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (AppUtility.checkInternetConnection()) {
                    GroupListInteractor.getInstance(mContext).getGroups(query, SearchGroupsActivity.this);
                    hideSoftKeyboard();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mRecyclerAdapter.setItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                CloudGroupManagement groupManagement = new CloudGroupManagement(mContext);
                groupManagement.addSubscribedGroup((GroupModel) model, new CloudOperationCallback() {
                    @Override
                    public void onCloudOperationSuccess() {
                        finish();
                    }

                    @Override
                    public void onCloudOperationFailed(AppError error) {

                    }
                });
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                break;
        }
    }

    private void animate() {

        RelativeLayout root = (RelativeLayout) findViewById(R.id.content_search_groups);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        cvSearch.getLocationOnScreen(originalPos);

        int xDest = dm.widthPixels / 2;
        xDest -= (cvSearch.getMeasuredWidth() / 2);
        int yDest = dm.heightPixels / 2 - (cvSearch.getMeasuredHeight() / 2) - statusBarOffset;

//        TranslateAnimation anim = new TranslateAnimation(0, xDest - originalPos[0], 0, );
        TranslateAnimation anim = new TranslateAnimation(xDest - originalPos[0], 0, yDest - originalPos[1], 0);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setInterpolator(new OvershootInterpolator());
        cvSearch.startAnimation(anim);
    }

    @Override
    public void onItemClicked(int position, Object model, View v) {

    }

    @Override
    public void onItemLongClicked(int position, Object model, View v) {

    }

    @Override
    public void onGroupFetchSuccess(ArrayList<GroupModel> groupModelArrayList) {
        updateGroupList(groupModelArrayList);
    }

    @Override
    public void onGroupFetchFailed(AppError error) {

    }

    private void updateGroupList(final ArrayList<GroupModel> groupModels) {
        hideSoftKeyboard();
        Log.d(TAG, "MODEL RECIEVED FROM DB :  How Much ? :" + groupModels.size());
        mRecyclerAdapter.setGroupList(groupModels);
        if (groupModels == null || groupModels.size() <= 0) {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No groups found with this keyword");
        } else {
            tvNoItems.setVisibility(View.GONE);
        }
    }
}
