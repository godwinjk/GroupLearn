package com.grouplearn.project.app.uiManagement.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ContactListAdapter;
import com.grouplearn.project.app.uiManagement.interactor.ContactListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.user.UserProfileActivity;
import com.grouplearn.project.models.GLContact;
import com.grouplearn.project.models.GLUser;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class SearchUserActivity extends BaseActivity implements ContactViewInterface {
    SearchView svSearchView;
    ListView lvContacts;
    TextView tvNoItems;
    ContactListAdapter listAdapter;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        setupToolbar("GroupLearn", true);
        mToolbar.setSubtitle("Search Users");
        mContext = this;
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        svSearchView = (SearchView) findViewById(R.id.sv_items);
        svSearchView.setIconifiedByDefault(true);
        svSearchView.setIconified(true);

        tvNoItems = (TextView) findViewById(R.id.tv_no_items);

        lvContacts = (ListView) findViewById(R.id.lv_contacts);
        listAdapter = new ContactListAdapter(mContext, 0);
        lvContacts.setAdapter(listAdapter);

        EditText searchText = ((EditText) svSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchText.setBackground(null);
        searchText.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHint("eg: John, George");

        svSearchView.performClick();
    }

    @Override
    public void registerListeners() {
        svSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (AppUtility.checkInternetConnection()) {
                    ContactListInteractor interactor = new ContactListInteractor(mContext);
                    interactor.searchAllContacts(query, SearchUserActivity.this);
                    DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
                } else {
                    DisplayInfo.showLoader(mContext, getString(R.string.no_network));
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
                userModel.setUserId(Long.parseLong(contactModel.getContactUniqueId()));
                userModel.setUserEmail(contactModel.getContactMailId());
                Intent intent = new Intent(mContext, UserProfileActivity.class);

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
        DisplayInfo.dismissLoader(mContext);
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
        DisplayInfo.dismissLoader(mContext);
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
        DisplayInfo.dismissLoader(mContext);
        if (listAdapter.getCount() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No users with this keyword");
        }
    }

    @Override
    public void onGetContactsFailed(AppError error) {
        DisplayInfo.dismissLoader(mContext);
        DisplayInfo.showToast(mContext, "No users found");
        if (listAdapter.getCount() > 0) {
            tvNoItems.setVisibility(View.GONE);
        } else {
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No users with this keyword");
        }
    }
}
