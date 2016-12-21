package com.grouplearn.project.app.uiManagement.contact;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.ContactListAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.group.GroupListNewActivity;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
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
    ListView lvContacts;
    TextView tvNoContacts;

    ContactListAdapter mAdapter;
    private String TAG = "ContactListActivity";
    ContactDbHelper mDbHelper;

    GroupListNewActivity mContext;

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
        if (mAdapter != null && mAdapter.getCount() > 0) {
            ContactReadTask readTask = new ContactReadTask(mContext);
            readTask.setContactViewInterface(this);
            readTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
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
        lvContacts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mAdapter != null && mAdapter.getCount() > 0) {
                    String firstLetter = ((GLContact) mAdapter.getItem(firstVisibleItem)).getContactName().substring(0, 1);
//                    setFastScrollThumbImage(view, writeOnDrawable(R.drawable.fast_scroll_thumb_512, firstLetter));
                }
            }
        });
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GLContact contactModel = (GLContact) model;
                if (v instanceof TextView) {
                    sendSms(contactModel.getContactNumber());
                } else if (v instanceof ImageView) {
                    openContact(contactModel.getContactId());
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    private void openContact(String contactId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
        intent.setData(uri);
        startActivity(intent);
    }

    private void sendSms(String phoneNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Install GroupLearn App to learn new things in new way https://goo.gl/nXaY6l", null, null);
            DisplayInfo.showToast(mContext, "Invitation sent successfully.");
        } catch (Exception ex) {
            Log.e(TAG, "FAILED TO SEND MESSAGE, FAILED TO SEND MESSAGE");
            ex.printStackTrace();
        }
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
    public void onGetAllContacts(ArrayList<GLContact> contactModels) {
        updateDataInList(sortUserList(new ContactDbHelper(mContext).getContacts()));
    }

    @Override
    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {
        updateDataInList(sortUserList(contactModels));
    }

    @Override
    public void onGetContactsFinished(ArrayList<GLContact> contactModels) {
        updateDataInList(sortUserList(contactModels));
    }

    @Override
    public void onGetContactsFailed(AppError error) {
        Log.e(TAG, error.getErrorMessage());
    }

}
