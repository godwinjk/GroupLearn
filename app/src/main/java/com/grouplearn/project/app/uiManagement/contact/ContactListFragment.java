package com.grouplearn.project.app.uiManagement.contact;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.ContactListAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudContactManager;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.group.GroupListNewActivity;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.user.UserProfileActivity;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends BaseFragment implements ContactViewInterface {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;

    private ListView lvContacts;
    private TextView tvNoContacts;

    private ContactListAdapter mAdapter;
    private String TAG = "ContactListActivity";
    private ContactDbHelper mDbHelper;

    private GroupListNewActivity mContext;

    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = (GroupListNewActivity) getActivity();
        initializeWidgets(view);
        registerListeners();

        getContactFromDb();
    }

    private void getContactFromDb() {
        ArrayList<GLContact> contactModels = mDbHelper.getContacts();
        if (contactModels != null && contactModels.size() > 0) {
            updateDataInList(sortUserList(contactModels));
        }
        getContactsFromCloud();
    }

    private void getContactsFromCloud() {
        CloudContactManager manager = new CloudContactManager(mContext);
        manager.getAllContact(this);
    }

    @Override
    protected void initializeWidgets(View v) {
        mDbHelper = new ContactDbHelper(mContext);

        lvContacts = (ListView) v.findViewById(R.id.lv_contacts);
        int type = 0;

        mAdapter = new ContactListAdapter(mContext, type);
        lvContacts.setAdapter(mAdapter);
        tvNoContacts = (TextView) v.findViewById(R.id.tv_no_contacts);
    }

    @Override
    protected void registerListeners() {

        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
                                                    @Override
                                                    public void onItemClicked(int position, Object model, int action, View v) {
                                                        GLContact contactModel = (GLContact) model;
                                                        if (v instanceof LinearLayout) {
                                                            openUserDetailsWindow(v, contactModel);
                                                        }
                                                    }

                                                    @Override
                                                    public void onItemLongClicked(int position, Object model, int action, View v) {

                                                    }
                                                }
        );
    }

    private void openUserDetailsWindow(View v, GLContact contactModel) {
        Intent intent = new Intent(mContext, UserProfileActivity.class);

        intent.putExtra("user", contactModel);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "transition_group_icon");
        startActivity(intent, options.toBundle());
    }


    private ArrayList<GLContact> sortUserList(final ArrayList<GLContact> models) {
        Collections.sort(models, new Comparator<GLContact>() {
            @Override
            public int compare(GLContact lhs, GLContact rhs) {
//                return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
                if (lhs.getStatus() == rhs.getStatus())
                    return 0;
                else if (rhs.getStatus() < lhs.getStatus()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return models;
    }

    private void updateDataInList(final ArrayList<GLContact> contactModels) {
        if (mContext != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (contactModels != null && contactModels.size() > 0) {
                        mAdapter.setContactList(contactModels);
                    }
                    if (mAdapter.getCount() > 0)
                        tvNoContacts.setVisibility(View.GONE);
                    else
                        tvNoContacts.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onGetAllContactsFromCloud(ArrayList<GLContact> contactModels) {
        updateDataInList(sortUserList(new ContactDbHelper(mContext).getContacts()));
    }

    @Override
    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {
        updateDataInList(sortUserList(contactModels));
    }

    @Override
    public void onGetContactsFailed(AppError error) {
        Log.e(TAG, error.getErrorMessage());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DisplayInfo.showToast(mContext, "Try again");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null && isVisibleToUser)
            getContactFromDb();
    }
}
