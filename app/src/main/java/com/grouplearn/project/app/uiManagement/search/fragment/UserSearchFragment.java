package com.grouplearn.project.app.uiManagement.search.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.ContactListAdapter;
import com.grouplearn.project.app.uiManagement.interactor.ContactListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.search.SearchAllActivity;
import com.grouplearn.project.app.uiManagement.user.UserProfileActivity;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLUser;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSearchFragment extends BaseFragment implements ContactViewInterface {
    SearchView svSearchView;
    ListView lvContacts;
    TextView tvNoItems;
    ContactListAdapter listAdapter;
    SearchAllActivity activity;

    public UserSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (SearchAllActivity) getActivity();
        initializeWidgets(view);
        registerListeners();
    }

    @Override
    protected void initializeWidgets(View v) {
        svSearchView = (SearchView) v.findViewById(R.id.sv_items);
        svSearchView.setIconifiedByDefault(true);
        svSearchView.setIconified(true);

        tvNoItems = (TextView) v.findViewById(R.id.tv_no_items);

        lvContacts = (ListView) v.findViewById(R.id.lv_contacts);
        listAdapter = new ContactListAdapter(activity, 0);
        lvContacts.setAdapter(listAdapter);

        EditText searchText = ((EditText) svSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchText.setBackground(null);
        searchText.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHint("Enter string to search");
    }

    @Override
    protected void registerListeners() {
        svSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (AppUtility.checkInternetConnection()) {
                    ContactListInteractor interactor = new ContactListInteractor(activity);
                    interactor.searchAllContacts(query, UserSearchFragment.this);
                    DisplayInfo.showLoader(activity, getString(R.string.please_wait));
                } else {
                    DisplayInfo.showLoader(activity, getString(R.string.no_network));
                }
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
                if (listAdapter != null && listAdapter.getCount() > 0)
                    tvNoItems.setVisibility(View.VISIBLE);
                return false;
            }
        });
        listAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GLContact contactModel = (GLContact) model;
                GLUser userModel = new GLUser();
                userModel.setUserDisplayName(contactModel.getContactName());
                userModel.setUserStatus(contactModel.getContactStatus());
                userModel.setUserId(contactModel.getContactUniqueId());
                userModel.setUserEmail(contactModel.getContactMailId());
                Intent intent = new Intent(activity, UserProfileActivity.class);

                intent.putExtra("user", userModel);

                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    @Override
    public void onGetAllContacts(ArrayList<GLContact> contactModels) {
        DisplayInfo.dismissLoader(activity);
        listAdapter.setContactList(contactModels);
        if (listAdapter.getCount() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No users with this keyword");
        }
    }

    @Override
    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {
        DisplayInfo.dismissLoader(activity);
        listAdapter.setContactList(contactModels);
        if (listAdapter.getCount() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No users with this keyword");
        }
    }

    @Override
    public void onGetContactsFinished(ArrayList<GLContact> contactModels) {
        listAdapter.setContactList(contactModels);
        DisplayInfo.dismissLoader(activity);
        if (listAdapter.getCount() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No users with this keyword");
        }
    }

    @Override
    public void onGetContactsFailed(AppError error) {
        DisplayInfo.dismissLoader(activity);
        DisplayInfo.showToast(activity, "No users found");
        if (listAdapter.getCount() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No users with this keyword");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (activity != null) {
                DisplayInfo.dismissLoader(activity);
            }
        }
        if (activity != null) {
            DisplayInfo.dismissLoader(activity);
        }
    }
}
