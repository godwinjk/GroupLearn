package com.grouplearn.project.app.uiManagement.group;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.utilities.AppConstants;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class AddGroupActivity extends BaseActivity {
    TextInputLayout etGroupName;
    TextInputLayout etDescription;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        setupToolbar("GroupLearn", true);
        mToolbar.setSubtitle("Add Topic");
        mContext = this;
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        etGroupName = (TextInputLayout) findViewById(R.id.et_group_name);
        etDescription = (TextInputLayout) findViewById(R.id.et_group_description);
    }

    @Override
    public void registerListeners() {
        etGroupName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etGroupName.setError(null);
                if (s.length() > 0) {
                    etDescription.setHint("Say something about " + s.toString());
                } else {
                    etDescription.setHint("Say something...");
                }
            }
        });
        etDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                etDescription.setError(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_group, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if (validate()) {
                    if (AppUtility.checkInternetConnection()) {
                        saveGroup();
                    } else {
                        DisplayInfo.showToast(mContext, getString(R.string.no_network));
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        String groupName = etGroupName.getEditText().getText().toString();
        String description = etDescription.getEditText().getText().toString();
        if (TextUtils.isEmpty(groupName)) {
            etGroupName.setError("Please enter a name!");
            return false;
        } else if (groupName.length() <= 3 && groupName.length() > 30) {
            etGroupName.setError("Name length should be in between of 3 and 30");
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Say something about " + groupName);
            return false;
        }
        return true;
    }

    private void saveGroup() {
        String groupName = etGroupName.getEditText().getText().toString();
        String description = etDescription.getEditText().getText().toString();

        final GLGroup model = new GLGroup();
        model.setGroupCreatedTime("" + System.currentTimeMillis());
        model.setGroupUpdatedTime("" + System.currentTimeMillis());
        model.setGroupName(groupName);
        model.setGroupDescription(description);

        long userId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);
        model.setGroupAdminId(userId);

        model.setPrivacy(AppConstants.GROUP_VISIBLE_TO_ALL);
        DisplayInfo.showLoader(mContext, "Adding group...");
        GroupListInteractor.getInstance(mContext).addGroup(model, new CloudOperationCallback() {
            @Override
            public void onCloudOperationSuccess() {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Group added successfully");
//                new GroupDbHelper(mContext).addSubscribedGroup(model);
                finish();
            }

            @Override
            public void onCloudOperationFailed(AppError error) {
                DisplayInfo.dismissLoader(mContext);
            }
        });
        etDescription.getEditText().setText("");
        etGroupName.getEditText().setText("");
        hideSoftKeyboard();
    }
}
