package com.grouplearn.project.app.uiManagement.contactManagement;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.models.ContactModel;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ContactListActivity extends BaseActivity implements ContactViewInterface {
    ListView lvContacts;
    TextView tvNoContacts;
    Context mContext;
    ContactListAdapter mAdapter;
    private String TAG = "ContactListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = setupToolbar("Contacts", true);
        mContext = this;

        initializeWidgets();
        registerListeners();
//        getCountryData();
    }

    @Override
    public void initializeWidgets() {
        lvContacts = (ListView) findViewById(R.id.lv_contacts);

        mAdapter = new ContactListAdapter(mContext);
        lvContacts.setAdapter(mAdapter);
        tvNoContacts = (TextView) findViewById(R.id.tv_no_contacts);

        ContactReadTask readTask = new ContactReadTask(this);
        readTask.setContactViewInterface(this);
        readTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    String firstLetter = ((ContactModel) mAdapter.getItem(firstVisibleItem)).getContactName().substring(0, 1);
//                    setFastScrollThumbImage(view, writeOnDrawable(R.drawable.fast_scroll_thumb_512, firstLetter));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateDataInList(final ArrayList<ContactModel> contactModels) {
//        new ContactListInteractor(mContext).addAllContacts(contactModels, this);
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
    public void onGetAllContacts(ArrayList<ContactModel> contactModels) {
//        updateDataInList(contactModels);
    }

    @Override
    public void onGetAllContactsFromDb(ArrayList<ContactModel> contactModels) {
        updateDataInList(contactModels);
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
                String shareBody = "Install GroupLearn App.";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
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
}
