package com.grouplearn.project.app.uiManagement.course;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.CourseSearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.CourseViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.settings.BrowserActivity;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.cloud.ThumbNailLoader;
import com.grouplearn.project.cloud.ThumbNailLoaderCallback;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class CourseSearchActivity extends BaseActivity {
    SearchView svSearchView;
    RecyclerView rvSearchList;
    CardView cvSearch;
    TextView tvNoItems;
    ImageView ivClose;
    Context mContext;
    CourseSearchRecyclerAdapter mRecyclerAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    TextView tvContactDetails, tvDescription, tvRequest, tvCourseName, tvLearn, tvSiteAddress;
    ImageView ivSiteIcon;

    GLCourse course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Search Course");

        mContext = this;
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        mRecyclerAdapter = new CourseSearchRecyclerAdapter(mContext, true);

        cvSearch = (CardView) findViewById(R.id.cv_search);

        svSearchView = (SearchView) findViewById(R.id.sv_items);
        svSearchView.setIconifiedByDefault(true);

        tvNoItems = (TextView) findViewById(R.id.tv_no_items);
        ivClose = (ImageView) findViewById(R.id.iv_close);

        tvContactDetails = (TextView) findViewById(R.id.tv_contact);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvRequest = (TextView) findViewById(R.id.tv_request);
        tvCourseName = (TextView) findViewById(R.id.tv_course_name);
        tvLearn = (TextView) findViewById(R.id.tv_learn);

        tvSiteAddress = (TextView) findViewById(R.id.tv_course_site);
        ivSiteIcon = (ImageView) findViewById(R.id.iv_course_site_icon);

        tvSiteAddress.setVisibility(View.GONE);
        ivSiteIcon.setVisibility(View.GONE);

        rvSearchList = (RecyclerView) findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSearchList.setAdapter(mRecyclerAdapter);

        EditText searchText = ((EditText) svSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchText.setBackground(null);
        searchText.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHint("eg: Java, Android, iOS");

        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(20);
        svSearchView.performClick();
    }

    @Override
    public void registerListeners() {
        tvLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, BrowserActivity.class);
                i.putExtra("uri", course.getUrl());
                startActivity(i);
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestToAdd(course);
            }
        });
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
                    searchCourse(query);
                } else {
                    DisplayInfo.showToast(mContext, getString(R.string.no_network));
                }
                hideSoftKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model,int action, View v) {
                course = (GLCourse) model;
                if (v instanceof LinearLayout) {
                    setup(course);
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model,int action, View v) {

            }
        });
    }

    private void setup(final GLCourse course) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        tvContactDetails.setText(course.getContactDetails());
        tvDescription.setText(course.getDefinition());
        tvCourseName.setText(course.getCourseName());
        ivSiteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = course.getUrl();
                Intent i = new Intent(mContext, BrowserActivity.class);
                i.putExtra("uri", uri);
                startActivity(i);
            }
        });
        tvSiteAddress.setVisibility(View.GONE);
        ivSiteIcon.setVisibility(View.GONE);
        if (course.getCourseSiteIconUri() == null) {
            ThumbNailLoader loader = new ThumbNailLoader(mContext, course.getUrl(), new ThumbNailLoaderCallback() {
                @Override
                public void onThumbNailLoaded(String siteName, Uri thumbNailLoaded) {
                    course.setCourseSiteIconUri(thumbNailLoaded.toString());
                    course.setCourseSiteName(siteName);
                    setCourseThumbNail(siteName, thumbNailLoaded);
                }

                @Override
                public void onThumbNailLoadingFailed() {
                    tvSiteAddress.setVisibility(View.GONE);
                    ivSiteIcon.setVisibility(View.GONE);
                }
            });
            loader.execute();
        } else {
            Uri uri = Uri.parse(course.getCourseSiteIconUri());
            setCourseThumbNail(course.getCourseSiteName(), uri);
        }

    }

    private void setCourseThumbNail(String siteName, Uri uri) {
        tvSiteAddress.setVisibility(View.VISIBLE);
        ivSiteIcon.setVisibility(View.VISIBLE);

        String tempName = siteName;

        if (siteName.length() > 9) {
            tempName = siteName.substring(0, 9) + "...";
        }
        tvSiteAddress.setText(tempName);
        Glide.with(mContext)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(ivSiteIcon);
    }

    private void searchCourse(String courseName) {
        CourseViewInterface courseViewInterface = new CourseViewInterface() {
            @Override
            public void onCourseGetSucces(ArrayList<GLCourse> courses) {
                DisplayInfo.dismissLoader(mContext);
                updateData(courses);
            }

            @Override
            public void onCourseGetFailed(AppError error) {
                DisplayInfo.dismissLoader(mContext);
            }
        };
        DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
        CloudCourseManager manager = new CloudCourseManager(mContext);
        manager.getCourse(courseName, courseViewInterface);
    }

    public void updateData(final ArrayList<GLCourse> courses) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (courses != null && courses.size() > 0) {
                    mRecyclerAdapter.clear();
                    mRecyclerAdapter.setCourses(courses);
                } else {
                    DisplayInfo.showToast(mContext, "No course with these keyword.");
                }
                if (mRecyclerAdapter.getItemCount() > 0) {
                    tvNoItems.setVisibility(View.GONE);
                } else {
                    tvNoItems.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestToAdd(GLCourse course) {
        GLGroup group = new GLGroup();
        group.setGroupUniqueId(course.getGroupId());
        group.setGroupName("" + course.getGroupName());
        group.setGroupIconId("" + course.getGroupIconId());
        group.setGroupDescription("" + course.getDefinition());
        group.setGroupAdminId(course.getCourseUserId());
        CloudGroupManagement groupManagement = new CloudGroupManagement(mContext);
        DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
        groupManagement.addSubscribedGroup(group, new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                DisplayInfo.dismissLoader(mContext);
                finish();
            }

            @Override
            public void onCloudOperationFailed(AppError error) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Something went wrong");
            }
        });
    }
}
