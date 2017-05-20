package com.grouplearn.project.app.uiManagement.course;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.CourseSearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager;
import com.grouplearn.project.app.uiManagement.databaseHelper.CourseDbHelper;
import com.grouplearn.project.app.uiManagement.interfaces.CourseViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.settings.BrowserActivity;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.cloud.ThumbNailLoader;
import com.grouplearn.project.cloud.ThumbNailLoaderCallback;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class MyCourseActivity extends BaseActivity {
    Context mContext;

    private RecyclerView rvSearchList;
    private TextView tvNoItems;
    private CourseSearchRecyclerAdapter mRecyclerAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    LinearLayout llLoading;
    TextView tvContactDetails, tvDescription, tvCourseName,
            tvCourseDelete, tvSiteAddress, tvLearn;
    ImageView ivSiteIcon, ivClose;
    GLCourse course;
    SwipeRefreshLayout srlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("My Courses");

        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llLoading.setVisibility(View.GONE);
        srlMain = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        mRecyclerAdapter = new CourseSearchRecyclerAdapter(mContext, true);

        tvNoItems = (TextView) findViewById(R.id.tv_no_items);

        tvContactDetails = (TextView) findViewById(R.id.tv_contact);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvCourseName = (TextView) findViewById(R.id.tv_course_name);
        tvSiteAddress = (TextView) findViewById(R.id.tv_course_site);
        ivSiteIcon = (ImageView) findViewById(R.id.iv_course_site_icon);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        tvLearn = (TextView) findViewById(R.id.tv_course_learn);

        tvSiteAddress.setVisibility(View.GONE);
        ivSiteIcon.setVisibility(View.GONE);
        rvSearchList = (RecyclerView) findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSearchList.setAdapter(mRecyclerAdapter);

        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(20);
    }

    @Override
    public void registerListeners() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, int action, View v) {
                course = (GLCourse) model;
                setup(course);
            }

            @Override
            public void onItemLongClicked(int position, Object model, int action, View v) {

            }
        });
        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCourses();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourses();
    }

    private void setup(final GLCourse course) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        tvContactDetails.setText(course.getContactDetails());
        tvDescription.setText(course.getDefinition());
        tvCourseName.setText(course.getCourseName());
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = course.getUrl();
                Intent i = new Intent(mContext, BrowserActivity.class);
                i.putExtra("uri", uri);
                startActivity(i);
            }
        };
        ivSiteIcon.setOnClickListener(clickListener);
        tvLearn.setOnClickListener(clickListener);

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


    private void getCourses() {
        ArrayList<GLCourse> courses = new CourseDbHelper(mContext).getMyCourses();
        updateData(courses);
        CourseViewInterface courseViewInterface = new CourseViewInterface() {
            @Override
            public void onCourseGetSucces(ArrayList<GLCourse> courses) {
                updateData(courses);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srlMain.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onCourseGetFailed(AppError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srlMain.setRefreshing(false);
                    }
                });
            }
        };
        if (AppUtility.checkInternetConnection()) {
            CloudCourseManager manager = new CloudCourseManager(mContext);
            manager.getCourse(courseViewInterface);
            srlMain.setRefreshing(true);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    public void updateData(final ArrayList<GLCourse> courses) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DisplayInfo.dismissLoader(mContext);
                if (courses != null && courses.size() > 0) {
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
}
