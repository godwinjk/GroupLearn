package com.grouplearn.project.app.uiManagement.contact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ContactListAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLRequest;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.inviteGroup.CloudGroupInvitationResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ContactListActivity extends BaseActivity implements ContactViewInterface {
    private ListView lvContacts;
    private TextView tvNoContacts;
    private Context mContext;
    private ContactListAdapter mAdapter;
    private final String TAG = "ContactListActivity";
    private ContactDbHelper mDbHelper;
    private GLGroup groupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = setupToolbar("Contacts", true);
        mContext = this;
        long groupUniqueId = getIntent().getLongExtra("groupCloudId", -1);
        groupModel = new GroupDbHelper(mContext).getGroupInfo(groupUniqueId);

        initializeWidgets();
        registerListeners();
        getContactsFromDb();
    }

    @Override
    public void initializeWidgets() {
        mDbHelper = new ContactDbHelper(mContext);

        lvContacts = (ListView) findViewById(R.id.lv_contacts);
        int type = 0;
        if (groupModel != null)
            type = 1;
        mAdapter = new ContactListAdapter(mContext, type);
        lvContacts.setAdapter(mAdapter);
        tvNoContacts = (TextView) findViewById(R.id.tv_no_contacts);
    }

    @Override
    public void registerListeners() {
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
            public void onItemClicked(int position, Object model, int action,View v) {
                GLContact contactModel = (GLContact) model;
                if (v instanceof TextView) {
                    if (groupModel != null) {
                        GLRequest requestModel = new GLRequest();

                        requestModel.setUserId(contactModel.getContactUserId());
                        requestModel.setGroupIconId(groupModel.getGroupIconId());
                        requestModel.setGroupId(groupModel.getGroupUniqueId());
                        requestModel.setGroupName(groupModel.getGroupName());

                        callGroupInvite(requestModel);
                    }
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model,int action, View v) {

            }
        });
    }

    private void callGroupInvite(GLRequest model) {
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                CloudGroupInvitationResponse response = (CloudGroupInvitationResponse) cloudResponse;
                if (response.getRequestModels().size() > 0) {
                    for (GLRequest requestModel : response.getRequestModels()) {
                        if (requestModel.getStatus() == 1) {
                            DisplayInfo.showToast(mContext, "Invitation sent successfully");
                            finish();
                        } else {
                            DisplayInfo.showToast(mContext, requestModel.getMessage());
                        }
                    }
                } else {
                    DisplayInfo.showToast(mContext, "Invitation sent failed");
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Invitation sent failed");
            }
        };
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, "Please wait");
            GroupListInteractor.getInstance(mContext).inviteToGroup(model, callback);
        } else {
            DisplayInfo.showToast(mContext, "Please check your internet connection");
        }
    }

    private void getContactsFromDb() {
        if (groupModel != null) {
            ArrayList<GLContact> contactModels = new ContactDbHelper(mContext).getContacts();
            updateDataInList(contactModels);
        } else {
            ArrayList<GLContact> contactModels = mDbHelper.getContacts();
            if (contactModels != null && contactModels.size() > 0) {
                updateDataInList(sortUserList(contactModels));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateDataInList(final ArrayList<GLContact> contactModels) {
        runOnUiThread(new Runnable() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView mSearchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Filter filter = mAdapter.getFilter();
                filter.filter(newText);
                if (mAdapter.getCount() > 0) {
                    tvNoContacts.setVisibility(View.GONE);
                } else {
                    tvNoContacts.setVisibility(View.VISIBLE);
                    tvNoContacts.setText("No contacts with " + newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private ArrayList<String> mCountries = new ArrayList<String>();

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

    public void getCountryData() {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !mCountries.contains(country)) {
                Log.d(TAG, country);
                mCountries.add(country);
            }
        }
        Collections.sort(mCountries);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                String shareBody = "Install GroupLearn App to learn new things in new way https://goo.gl/nXaY6l";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GroupLearn App");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Set a ListView or GridView fast scroll thumb image.
     *
     * @param listView The {@link android.widget.ListView} or {@link android.widget.GridView}
     * @param thumb    The fast-scroll drawable
     * @return {@code true} if successfully set.
     */
    public static boolean setFastScrollThumbImage(AbsListView listView, Drawable thumb) {
        try {
            Field f;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                f = AbsListView.class.getDeclaredField("mFastScroll");
            } else {
                f = AbsListView.class.getDeclaredField("mFastScroller");
            }
            f.setAccessible(true);
            Object o = f.get(listView);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                f = f.getType().getDeclaredField("mThumbImage");
                f.setAccessible(true);
                ImageView iv = (ImageView) f.get(o);
                iv.setImageDrawable(thumb);
            } else {
                f = f.getType().getDeclaredField("mThumbDrawable");
                f.setAccessible(true);
                Drawable drawable = (Drawable) f.get(o);
                drawable = thumb;
                f.set(o, drawable);
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public Drawable writeOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(300);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, (bm.getWidth() / 2) - 30, (bm.getHeight() / 2) + 30, paint);

        return new BitmapDrawable(bm);
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
}
