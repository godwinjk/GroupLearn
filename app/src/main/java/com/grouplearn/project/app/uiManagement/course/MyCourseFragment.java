package com.grouplearn.project.app.uiManagement.course;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.adapter.CourseSearchRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.databaseHelper.CourseDbHelper;
import com.grouplearn.project.app.uiManagement.group.GroupListNewActivity;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.CourseViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.settings.BrowserActivity;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.ThumbNailLoader;
import com.grouplearn.project.cloud.ThumbNailLoaderCallback;
import com.grouplearn.project.cloud.courseManagement.delete.CloudDeleteCourseRequest;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCourseFragment extends BaseFragment {
    private static final String TAG = "MyCourseFragment";
    private RecyclerView rvSearchList;
    private TextView tvNoItems;
    private CourseSearchRecyclerAdapter mRecyclerAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheet;
    private TextView tvContactDetails,
            tvDescription, tvCourseName,
            tvCourseDelete, tvSiteAddress, tvLearn;
    private ImageView ivSiteIcon, ivClose;
    private GLCourse course;
    private LinearLayout llLoading;
    private GroupListNewActivity mContext;
    private SwipeRefreshLayout srlMain;

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

    }

    @Override
    public void onResume() {
        super.onResume();
        getCourses();
    }

    @Override
    protected void initializeWidgets(View v) {
        mRecyclerAdapter = new CourseSearchRecyclerAdapter(getActivity(), false);
        srlMain = (SwipeRefreshLayout) v.findViewById(R.id.srl_main);
        tvNoItems = (TextView) v.findViewById(R.id.tv_no_items);

        tvContactDetails = (TextView) v.findViewById(R.id.tv_contact);
        tvDescription = (TextView) v.findViewById(R.id.tv_description);
        tvCourseName = (TextView) v.findViewById(R.id.tv_course_name);
        tvCourseDelete = (TextView) v.findViewById(R.id.tv_course_delete);
        tvLearn = (TextView) v.findViewById(R.id.tv_course_learn);

        tvSiteAddress = (TextView) v.findViewById(R.id.tv_course_site);
        ivSiteIcon = (ImageView) v.findViewById(R.id.iv_course_site_icon);
        ivClose = (ImageView) v.findViewById(R.id.iv_close);

        tvSiteAddress.setVisibility(View.GONE);
        ivSiteIcon.setVisibility(View.GONE);

        rvSearchList = (RecyclerView) v.findViewById(R.id.rv_search_list);
        rvSearchList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        rvSearchList.setAdapter(mRecyclerAdapter);

        bottomSheet = v.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(20);
        llLoading = (LinearLayout) v.findViewById(R.id.ll_loading);
        llLoading.setVisibility(View.GONE);
    }

    @Override
    protected void registerListeners() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCourses();
            }
        });
        tvCourseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (course != null) {
                    if (course.isMine()) {
                        deleteCourse();
                    } else {
                        requestCourse();
                    }
                }
            }
        });
        mRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, int action, View v) {
                course = (GLCourse) model;
                if (v instanceof LinearLayout) {
                    setup(course);
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model, int action, View v) {

            }
        });
    }

    private void requestCourse() {
        GLGroup group = new GLGroup();
        group.setGroupUniqueId(course.getGroupId());
        group.setGroupName("" + course.getGroupName());
        group.setGroupIconId("" + course.getGroupIconId());
        group.setGroupDescription(course.getDefinition());
        group.setGroupAdminId(course.getCourseUserId());
        CloudGroupManagement groupManagement = new CloudGroupManagement(mContext);
        DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
        groupManagement.addSubscribedGroup(group, new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Request sent successfully.");
            }

            @Override
            public void onCloudOperationFailed(AppError error) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Something went wrong");
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
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mRecyclerAdapter.remove(course);
                CourseDbHelper mDbHelper = new CourseDbHelper(mContext);
                mDbHelper.deleteCourse(course);
                updateData(mDbHelper.getCourses());
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
                DisplayInfo.dismissLoader(mContext);
            }
        } else {
            if (mContext != null) {
                DisplayInfo.dismissLoader(mContext);
            }
        }
    }

    private void setup(final GLCourse course) {
        tvContactDetails.setText(course.getContactDetails());
        tvDescription.setText(course.getDefinition());
        tvCourseName.setText(course.getCourseName());
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        long userId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);
        if (!course.isMine()) {
            tvCourseDelete.setText("Join Group");
        } else {
            tvCourseDelete.setText("Delete");
        }
        if (course.isMine() && course.getCourseUserId() != userId) {
            tvCourseDelete.setVisibility(View.GONE);
        } else {
            tvCourseDelete.setVisibility(View.VISIBLE);
        }
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
        if (!TextUtils.isEmpty(course.getCourseSiteIconUri())) {
            tvSiteAddress.setVisibility(View.VISIBLE);
            ivSiteIcon.setVisibility(View.VISIBLE);
            String siteName = course.getCourseSiteName();
            if (siteName.length() > 9) {
                siteName = siteName.substring(0, 9) + "...";
            }
            tvSiteAddress.setText(siteName);
            Uri uri = Uri.parse(course.getCourseSiteIconUri());
            Glide.clear(ivSiteIcon);
            Glide.with(mContext)
                    .load(uri)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(ivSiteIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            ivSiteIcon.setImageBitmap(resource);
                        }
                    });
        } else if (!TextUtils.isEmpty(course.getUrl())) {
            ThumbNailLoader loader = new ThumbNailLoader(mContext, course.getUrl(), new ThumbNailLoaderCallback() {
                @Override
                public void onThumbNailLoaded(String siteName, Uri thumbNailLoaded) {
                    tvSiteAddress.setVisibility(View.VISIBLE);
                    ivSiteIcon.setVisibility(View.VISIBLE);

                    String tempName = siteName;

                    course.setCourseSiteIconUri(thumbNailLoaded.toString());
                    course.setCourseSiteName(siteName);

                    new CourseDbHelper(mContext).updateWithSiteDetails(course);
                    if (siteName.length() > 9) {
                        tempName = siteName.substring(0, 9) + "...";
                    }
                    tvSiteAddress.setText(tempName);
                    Glide.with(mContext)
                            .load(thumbNailLoaded)
                            .asBitmap()
                            .centerCrop()
                            .into(new BitmapImageViewTarget(ivSiteIcon) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    ivSiteIcon.setImageBitmap(resource);
                                }
                            });
                }

                @Override
                public void onThumbNailLoadingFailed() {
                    tvSiteAddress.setVisibility(View.GONE);
                    ivSiteIcon.setVisibility(View.GONE);
                }
            });
            loader.execute();
        }
    }

    private void getCourses() {
        ArrayList<GLCourse> courses = new CourseDbHelper(mContext).getCourses();
        updateData(courses);
        CourseViewInterface courseViewInterface = new CourseViewInterface() {
            @Override
            public void onCourseGetSucces(ArrayList<GLCourse> courses) {
                if (getActivity() == null)
                    return;
                updateData(courses);
                getRandomCourses();
                srlMain.setRefreshing(false);
            }

            @Override
            public void onCourseGetFailed(AppError error) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null)
                            return;
                        srlMain.setRefreshing(false);
                    }
                });
            }
        };
        if (AppUtility.checkInternetConnection()) {
            CloudCourseManager manager = new CloudCourseManager(mContext);
            manager.getSubscribedCourse(courseViewInterface);
        } else {
            Log.e(TAG, "NO NETWORK");
//            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    public void updateData(final ArrayList<GLCourse> courses) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (courses != null && courses.size() > 0) {
                    mRecyclerAdapter.clearAndInsert();
                    mRecyclerAdapter.setCourses(courses);
                }
                if (mRecyclerAdapter.getItemCount() > 0) {
                    tvNoItems.setVisibility(View.GONE);
                } else {
                    tvNoItems.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getRandomCourses() {
        CourseViewInterface courseViewInterface = new CourseViewInterface() {
            @Override
            public void onCourseGetSucces(ArrayList<GLCourse> courses) {
                if (getActivity() == null)
                    return;
                llLoading.setVisibility(View.GONE);
                updateData(courses);
            }

            @Override
            public void onCourseGetFailed(AppError error) {
                if (getActivity() == null)
                    return;
                llLoading.setVisibility(View.GONE);
            }
        };

        if (AppUtility.checkInternetConnection()) {
            llLoading.setVisibility(View.VISIBLE);
            CloudCourseManager manager = new CloudCourseManager(mContext);
            manager.getCourse("852", courseViewInterface);
        } else {
            Log.e(TAG, "NO NETWORK");
        }

    }
}
