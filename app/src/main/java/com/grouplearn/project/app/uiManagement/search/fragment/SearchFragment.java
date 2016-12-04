package com.grouplearn.project.app.uiManagement.search.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.SearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.GLGroup;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment implements GroupViewInterface, OnRecyclerItemClickListener {

    private static final String TAG = "SearchFragment";
    SearchRecyclerAdapter mRecyclerAdapter;
    SearchView svSearchView;
    RecyclerView rvSearchList;
    TextView tvNoItems;
    int whichWindow = 0;
    CardView cvSearch;
    ImageView ivShow;
    LinearLayout llSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeWidgets(view);
        registerListeners();
    }

    @Override
    protected void initializeWidgets(View v) {
        mRecyclerAdapter = new SearchRecyclerAdapter();
        mRecyclerAdapter.setItemClickListener(this);
        svSearchView = (SearchView) v.findViewById(R.id.sv_items);
        svSearchView.setIconifiedByDefault(true);
        cvSearch = (CardView) v.findViewById(R.id.cv_search);
        ivShow = (ImageView) v.findViewById(R.id.iv_show);
        llSearch = (LinearLayout) v.findViewById(R.id.ll_search);

        tvNoItems = (TextView) v.findViewById(R.id.tv_no_items);

        rvSearchList = (RecyclerView) v.findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSearchList.setAdapter(mRecyclerAdapter);

        EditText searchText = ((EditText) svSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchText.setBackground(null);
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHint("eg: Java, Android, iOS");

        svSearchView.performClick();
        tvNoItems.setVisibility(View.GONE);
    }

    @Override
    protected void registerListeners() {

        svSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                GroupListInteractor.getInstance(getActivity()).getGroups(query, SearchFragment.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        svSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoItems.setVisibility(View.GONE);
//                llSearch.setVisibility(View.GONE);
//                animate();
            }
        });
        svSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (mRecyclerAdapter != null && mRecyclerAdapter.getItemCount() > 0)
                    tvNoItems.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    @Override
    public void onGroupFetchSuccess(ArrayList<GLGroup> groupModelArrayList) {
        hideSoftKeyboard();
        if (groupModelArrayList != null && groupModelArrayList.size() > 0) {
            updateGroupList(groupModelArrayList);
        } else {
            DisplayInfo.showToast(getActivity(), "No groups with these keyword.");
        }
    }

    @Override
    public void onGroupFetchFailed(AppError error) {
        hideSoftKeyboard();
        DisplayInfo.showToast(getActivity(), "No groups with these keyword.");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            hideSoftKeyboard();
        }
    }

    private void updateGroupList(final ArrayList<GLGroup> groupModels) {
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

    @Override
    public void onItemClicked(int position, Object model, View v) {
        CloudGroupManagement groupManagement = new CloudGroupManagement(getActivity());
        groupManagement.addSubscribedGroup((GLGroup) model, new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                getActivity().finish();
            }

            @Override
            public void onCloudOperationFailed(AppError error) {

            }
        });
    }

    @Override
    public void onItemLongClicked(int position, Object model, View v) {

    }

    private void animate() {
        RelativeLayout root = (RelativeLayout) getView().findViewById(R.id.content_search_groups);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
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
}

