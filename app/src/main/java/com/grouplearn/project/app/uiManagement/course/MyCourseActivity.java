package com.grouplearn.project.app.uiManagement.course;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.CourseSearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager;
import com.grouplearn.project.app.uiManagement.interfaces.CourseViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class MyCourseActivity extends BaseActivity {
    Context mContext;

    RecyclerView rvSearchList;
    TextView tvNoItems;
    CourseSearchRecyclerAdapter mRecyclerAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    TextView tvContactDetails, tvDescription, tvCourseName;
    GLCourse course;

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
        mRecyclerAdapter = new CourseSearchRecyclerAdapter();

        tvNoItems = (TextView) findViewById(R.id.tv_no_items);

        tvContactDetails = (TextView) findViewById(R.id.tv_contact);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvCourseName = (TextView) findViewById(R.id.tv_course_name);

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
        mRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                course = (GLCourse) model;
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                setup(course);
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourses();
    }

    private void setup(GLCourse course) {
        tvContactDetails.setText(course.getContactDetails());
        tvDescription.setText(course.getDefinition());
        tvCourseName.setText(course.getCourseName());
    }

    private void getCourses() {
        CourseViewInterface courseViewInterface = new CourseViewInterface() {
            @Override
            public void onCourseGetSucces(ArrayList<GLCourse> courses) {
                updateData(courses);
            }

            @Override
            public void onCourseGetFailed(AppError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayInfo.dismissLoader(mContext);
                    }
                });
            }
        };
        if (AppUtility.checkInternetConnection()) {
            CloudCourseManager manager = new CloudCourseManager(mContext);
            manager.getCourse(courseViewInterface);
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
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
