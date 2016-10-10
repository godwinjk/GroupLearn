package com.grouplearn.project.app.uiManagement.serachManagement.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.SearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.cloud.networkManagement.CloudGenericHttpMethod;
import com.grouplearn.project.models.GoogleSearchResult;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment implements GroupViewInterface, OnRecyclerItemClickListener {

    private static final String TAG = "SearchFragment";
    SearchRecyclerAdapter mRecyclerAdapter;
    SearchView svSearchView;
    RecyclerView rvSearchList;
    TextView tvNoItems, tvSearchFromWeb;
    int whichWindow = 0;

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
        mRecyclerAdapter = new SearchRecyclerAdapter(whichWindow);
        mRecyclerAdapter.setItemClickListener(this);
        svSearchView = (SearchView) v.findViewById(R.id.sv_items);
        svSearchView.setIconifiedByDefault(true);

        tvNoItems = (TextView) v.findViewById(R.id.tv_no_items);
        tvSearchFromWeb = (TextView) v.findViewById(R.id.tv_search_web);

        rvSearchList = (RecyclerView) v.findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSearchList.setAdapter(mRecyclerAdapter);

        EditText searchText = ((EditText) svSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchText.setBackground(null);
        searchText.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHint("Enter string to search");
    }

    @Override
    protected void registerListeners() {
        tvSearchFromWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        svSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                GroupListInteractor.getInstance(getActivity()).getGroups(query, SearchFragment.this);
//                doGoogleSearch(query);
//                String siteUrl = "https://www.google.co.in/search?q=" + query;
//                (new ParseURL()).execute(new String[]{siteUrl});

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

    private void updateWithGoogleSearchResult() {

    }

    protected void doGoogleSearch(String s) {
        CloudGenericHttpMethod genericHttpMethod = new CloudGenericHttpMethod(getActivity());
        genericHttpMethod.setUrl("https://www.google.co.in/search?q=" + s);
        genericHttpMethod.execute();
    }

    public void setWhichWindow(int whichWindow) {
        this.whichWindow = whichWindow;
    }

    @Override
    public void onGroupFetchSuccess(ArrayList<GroupModel> groupModelArrayList) {
        updateGroupList(groupModelArrayList);
        hideSoftKeyboard();
    }

    @Override
    public void onGroupFetchFailed(AppError error) {
        hideSoftKeyboard();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            hideSoftKeyboard();
        }
    }

    private void updateGroupList(final ArrayList<GroupModel> groupModels) {
        hideSoftKeyboard();
        Log.d(TAG, "MODEL RECIEVED FROM DB :  How Much ? :" + groupModels.size());
        mRecyclerAdapter.setGroupList(groupModels);
        if (groupModels == null || groupModels.size() <= 0)
            tvNoItems.setVisibility(View.VISIBLE);
        else
            tvNoItems.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(int position, Object model, View v) {
        CloudGroupManagement groupManagement = new CloudGroupManagement(getActivity());
        groupManagement.addSubscribedGroup((GroupModel) model, new CloudOperationCallback() {
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


}

