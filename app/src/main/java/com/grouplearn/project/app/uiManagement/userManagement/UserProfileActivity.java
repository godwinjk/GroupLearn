package com.grouplearn.project.app.uiManagement.userManagement;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.adapter.ProfileRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.groupManagement.GroupChatActivity;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.models.UserModel;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int ACTIVITY_IMAGE_GET_REQUEST_CODE = 101;
    private static final int ACTIVITY_IMAGE_CROP_REQUEST_CODE = 2;
    private static final String TAG = "UserProfileActivity";
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar mToolbar;
    Context mContext;
    TextView tvName, tvStatus;
    CheckBox cbVisibility;
    ImageView ivProfile;

    RecyclerView rvSubscribedGroups;
    ProfileRecyclerAdapter mRecyclerAdapter;
    AppSharedPreference mPref;
    ImageView ivNameEdit, ivStatusEdit;
    CardView cvVisibility;
    boolean isOtherUser;
    String userName;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mToolbar = setupToolbar("Profile", true);
        mContext = this;
        mPref = new AppSharedPreference(mContext);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("User Info");
        userModel = getIntent().getParcelableExtra("user");

        if (userModel == null) {
            userName = mPref.getStringPrefValue(PreferenceConstants.USER_NAME);
        } else {
            userName = userModel.getUserName();
        }
        String appUserName = mPref.getStringPrefValue(PreferenceConstants.USER_NAME);
        if (appUserName.equals(userName)) {
            isOtherUser = false;
        } else {
            isOtherUser = true;
        }

        initializeWidgets();
        registerListeners();

        String imagePath = mPref.getStringPrefValue(PreferenceConstants.DP_PATH);

        if (!isOtherUser && imagePath != null) {
            setProfilePic(imagePath);
        }
    }

    @Override
    public void initializeWidgets() {

        cbVisibility = (CheckBox) findViewById(R.id.cb_visibility);
        tvName = (TextView) findViewById(R.id.tv_display_name);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        ivNameEdit = (ImageView) findViewById(R.id.iv_name_edit);
        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        ivStatusEdit = (ImageView) findViewById(R.id.iv_status_edit);
        cvVisibility = (CardView) findViewById(R.id.cv_visibility);

        rvSubscribedGroups = (RecyclerView) findViewById(R.id.rv_subscribed_groups);
        mRecyclerAdapter = new ProfileRecyclerAdapter(ProfileRecyclerAdapter.GROUP_LIST);
        rvSubscribedGroups.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSubscribedGroups.setAdapter(mRecyclerAdapter);

        collapsingToolbarLayout.setTitle(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
        tvName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
        tvStatus.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS));
        cbVisibility.setChecked(mPref.getBooleanPrefValue(PreferenceConstants.USER_PRIVACY_STATUS));

        /**
         * Settings for other User
         */
        setUserPreference();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOtherUser) {
            setDataToAdapter();
        }
    }

    @Override
    public void registerListeners() {
        ivStatusEdit.setOnClickListener(this);
        mRecyclerAdapter.setItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GroupModel groupModel = (GroupModel) model;
                Intent i = new Intent(mContext, GroupChatActivity.class);
                i.putExtra("groupCloudId", groupModel.getGroupUniqueId());
                startActivity(i);
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
        cbVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(isChecked);
            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), ACTIVITY_IMAGE_GET_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (ACTIVITY_IMAGE_GET_REQUEST_CODE == requestCode) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                try {
                    Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.d(TAG, picturePath);
//                    Intent viewMediaIntent = new Intent();
//                    viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
//                    File file = new File(picturePath);
//                    viewMediaIntent.setDataAndType(Uri.fromFile(file), "image/*");
//                    viewMediaIntent.putExtra("crop", "true");
//                    viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivityForResult(viewMediaIntent, 1);

                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    //indicate image type and Uri
                    cropIntent.setDataAndType(selectedImageUri, "image/*");
                    //set crop properties
                    cropIntent.putExtra("crop", "true");
                    //indicate aspect of desired crop
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    //indicate output X and Y
                    cropIntent.putExtra("outputX", 256);
                    cropIntent.putExtra("outputY", 256);
                    //retrieve data on return
                    cropIntent.putExtra("return-data", true);
                    //start the activity - we handle returning in onActivityResult
                    startActivityForResult(cropIntent, ACTIVITY_IMAGE_CROP_REQUEST_CODE);

                    mPref.setStringPrefValue(PreferenceConstants.DP_PATH, picturePath);
//                    setProfilePic(picturePath);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } else if (ACTIVITY_IMAGE_CROP_REQUEST_CODE == requestCode) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                createDirectoryAndSaveFile(thePic);
                ivProfile.setImageBitmap(thePic);

            }
        }
    }

    private void setDataToAdapter() {
        ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();
        groupModelArrayList = new GroupDbHelper(mContext).getSubscribedGroups();
        mRecyclerAdapter.setGroupList(groupModelArrayList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_status_edit:
                startActivity(new Intent(mContext, StatusActivity.class));
                break;
        }
    }

    private void setVisibility(final boolean value) {
        final String status = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS);
        CloudStatusRequest request = new CloudStatusRequest();
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setStatus(status);
        int privacy = 0;
        if (value)
            privacy = 1;
        request.setPrivacyValue(privacy);
        DisplayInfo.showLoader(mContext, "Updating privacy...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                mPref.setBooleanPrefValue(PreferenceConstants.USER_PRIVACY_STATUS, value);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).setStatus(request, callback);
    }

    private Bitmap setProfilePic(String imagePath) {
        Bitmap b = BitmapFactory.decodeFile(imagePath);
        ivProfile.setImageBitmap(b);
        Palette palette = Palette.from(b).generate();

        Palette.Swatch swatch = palette.getDarkMutedSwatch();
        int rgb = swatch.getRgb();
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setStatusBarScrimColor(rgb);
            collapsingToolbarLayout.setContentScrimColor(rgb);
            collapsingToolbarLayout.setBackgroundColor(rgb);
        }
        if (mToolbar != null)
            mToolbar.setBackgroundColor(swatch.getRgb());
        return b;
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageToSave.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        imageToSave = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        File directory = new File(Environment.getExternalStorageDirectory() + "/GroupLearn");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(new File(Environment.getExternalStorageDirectory() + "/GroupLearn"), "profilePic");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            setProfilePic(file.getAbsolutePath());
            mPref.setStringPrefValue(PreferenceConstants.DP_PATH, file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserPreference() {
        if (isOtherUser) {
            ivNameEdit.setVisibility(View.GONE);
            ivStatusEdit.setVisibility(View.GONE);
            cvVisibility.setVisibility(View.GONE);

            collapsingToolbarLayout.setTitle(userModel.getUserDisplayName());
            tvName.setText(userModel.getUserDisplayName());
            tvStatus.setText(userModel.getUserStatus());

        } else {
            ivNameEdit.setVisibility(View.VISIBLE);
            ivStatusEdit.setVisibility(View.VISIBLE);
            cvVisibility.setVisibility(View.VISIBLE);
            ivProfile.setVisibility(View.VISIBLE);
        }
    }
}
