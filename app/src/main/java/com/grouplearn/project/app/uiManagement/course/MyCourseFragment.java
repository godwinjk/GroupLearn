package com.grouplearn.project.app.uiManagement.course;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.CourseSearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.CourseDbHelper;
import com.grouplearn.project.app.uiManagement.group.GroupListNewActivity;
import com.grouplearn.project.app.uiManagement.interfaces.CourseViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.courseManagement.delete.CloudDeleteCourseRequest;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCourseFragment extends BaseFragment {
    RecyclerView rvSearchList;
    TextView tvNoItems;
    CourseSearchRecyclerAdapter mRecyclerAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    TextView tvContactDetails, tvDescription, tvCourseName, tvCourseDelete;
    GLCourse course;

    GroupListNewActivity mContext;

    public MyCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_course, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = (GroupListNewActivity) getActivity();
        initializeWidgets(view);
        registerListeners();
        getCourses();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCourses();
    }

    @Override
    protected void initializeWidgets(View v) {
        mRecyclerAdapter = new CourseSearchRecyclerAdapter();

        tvNoItems = (TextView) v.findViewById(R.id.tv_no_items);

        tvContactDetails = (TextView) v.findViewById(R.id.tv_contact);
        tvDescription = (TextView) v.findViewById(R.id.tv_description);
        tvCourseName = (TextView) v.findViewById(R.id.tv_course_name);
        tvCourseDelete = (TextView) v.findViewById(R.id.tv_course_delere);

        rvSearchList = (RecyclerView) v.findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        rvSearchList.setAdapter(mRecyclerAdapter);

        bottomSheet = v.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(20);
    }

    @Override
    protected void registerListeners() {
        tvCourseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCourse();
            }
        });
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

    private void deleteCourse() {
        AppAlertDialog alertDialog = AppAlertDialog.getAlertDialog(mContext);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Are you sure want to delete the course?");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFromCloud();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void deleteFromCloud() {
        CloudDeleteCourseRequest request = new CloudDeleteCourseRequest();
        request.setToken(new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setGlCourses(course);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                mRecyclerAdapter.getCourses().remove(course);
                CourseDbHelper mDbHelper = new CourseDbHelper(mContext);
                mDbHelper.deleteCourse(course);
                mRecyclerAdapter.notifyDataSetChanged();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Something went wrong");
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        };
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
            CloudConnectManager.getInstance(mContext).getCloudCourseManager(mContext).deleteCourses(request, callback);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mContext != null) {
                getCourses();
                DisplayInfo.dismissLoader(mContext);
            }
        } else {
            if (mContext != null) {
                DisplayInfo.dismissLoader(mContext);
            }
        }
    }

    private void setup(GLCourse course) {
        tvContactDetails.setText(course.getContactDetails());
        tvDescription.setText(course.getDefinition());
        tvCourseName.setText(course.getCourseName());
    }

    private void getCourses() {
        ArrayList<GLCourse> courses = new CourseDbHelper(mContext).getCourses();
        updateData(courses);
        CourseViewInterface courseViewInterface = new CourseViewInterface() {
            @Override
            public void onCourseGetSucces(ArrayList<GLCourse> courses) {
                updateData(courses);
            }

            @Override
            public void onCourseGetFailed(AppError error) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        DisplayInfo.dismissLoader(mContext);
                    }
                });
            }
        };
        if (AppUtility.checkInternetConnection()) {
            com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager manager = new com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager(mContext);
            manager.getCourse(courseViewInterface);
//            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    public void updateData(final ArrayList<GLCourse> courses) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                DisplayInfo.dismissLoader(mContext);
                if (courses != null && courses.size() > 0) {
                    mRecyclerAdapter.setCourses(courses);
                } else {
//                    DisplayInfo.showToast(mContext, ".");
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
