package com.grouplearn.project.app.uiManagement.course;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudCourseManager;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.InputValidator;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class CourseAddingActivity extends BaseActivity implements View.OnClickListener {
    TextInputLayout etCourseName, etCourseDefinition, etContactDetails, etSiteUrl;
    FloatingActionButton fab;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_adding);
        setupToolbar("GroupLearn", true);
        mToolbar.setSubtitle("Add Course");
        mContext = this;
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        etCourseName = (TextInputLayout) findViewById(R.id.et_course_name);
        etCourseDefinition = (TextInputLayout) findViewById(R.id.et_course_description);
        etContactDetails = (TextInputLayout) findViewById(R.id.et_course_contact);
        etSiteUrl = (TextInputLayout) findViewById(R.id.et_course_url);

    }

    @Override
    public void registerListeners() {
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (validate()) {
                    saveCourse();
                }
                break;
        }
    }

    private boolean validate() {
        String courseName = etCourseName.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(courseName)) {
            etCourseName.setError("Course name is mandatory");
            return false;
        } else if (courseName.length() < 3 && courseName.length() > 32) {
            etCourseName.setError("Course name should be 3-32 character long");
            return false;
        } else if (TextUtils.isEmpty(etCourseDefinition.getEditText().getText().toString())) {
            etCourseDefinition.setError("Course description is mandatory");
            return false;
        } else if (etCourseDefinition.getEditText().getText().toString().length() < 10 && etCourseDefinition.getEditText().getText().toString().length() > 140) {
            etCourseDefinition.setError("Course description should be 10-140 character long");
            return false;
        } else if (TextUtils.isEmpty(etContactDetails.getEditText().getText().toString())) {
            etContactDetails.setError("Contact is mandatory");
            return false;
        } else if (etContactDetails.getEditText().getText().toString().length() < 10) {
            etContactDetails.setError("Contact should be atleast 10 character long");
            return false;
        } else if (TextUtils.isEmpty(etSiteUrl.getEditText().getText().toString())) {
            etSiteUrl.setError("Web address is mandatory");
        } else if (!TextUtils.isEmpty(etSiteUrl.getEditText().getText().toString())) {
            if (!InputValidator.isURI(etSiteUrl.getEditText().getText().toString())) {
                etSiteUrl.setError("Invalid web address");
                return false;
            }
        }

        return true;
    }

    private void saveCourse() {
        String courseName = etCourseName.getEditText().getText().toString();
        String description = etCourseDefinition.getEditText().getText().toString();
        String contact = etContactDetails.getEditText().getText().toString();
        String url = etSiteUrl.getEditText().getText().toString();

        GLCourse model = new GLCourse();
        model.setCourseName(courseName);
        model.setDefinition(description);
        model.setContactDetails(contact);
        model.setUrl(url);
        AppSharedPreference mPref = new AppSharedPreference(mContext);

        model.setCourseUserId(mPref.getLongPrefValue(PreferenceConstants.USER_ID));
        CloudOperationCallback callback = new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayInfo.dismissLoader(mContext);
                        DisplayInfo.showToast(mContext, "Course created successfully");
                        finish();
                    }
                });
            }

            @Override
            public void onCloudOperationFailed(AppError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayInfo.dismissLoader(mContext);
                        DisplayInfo.showToast(mContext, "Course name already exist . Please choose a different one.");
                    }
                });
            }
        };
        CloudCourseManager manager = new CloudCourseManager(mContext);
        if (AppUtility.checkInternetConnection()) {
            manager.createCourse(model, callback);
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
        hideSoftKeyboard();
    }
}