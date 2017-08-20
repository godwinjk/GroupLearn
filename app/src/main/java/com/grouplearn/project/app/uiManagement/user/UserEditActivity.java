package com.grouplearn.project.app.uiManagement.user;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;
import com.grouplearn.project.cloud.userManagement.upload.CloudUploadProfileRequest;
import com.grouplearn.project.cloud.userManagement.upload.CloudUploadProfileResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.views.DisplayInfo;
import com.grouplearn.project.utilities.views.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UserEditActivity extends BaseActivity {

    private static final int ACTIVITY_IMAGE_GET_REQUEST_CODE = 101;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;

    private static final String TAG = UserEditActivity.class.getSimpleName();
    private Context mContext;
    private RoundedImageView rivPic;
    private ImageView ivNameEdit, ivStatusEdit;
    private AppSharedPreference mPref;
    private TextView tvPick, tvDisplayName, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        setupToolbar("Edit User", true);
        mToolbar.setSubtitle("Edit User");
        mContext = this;
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        mPref = new AppSharedPreference(mContext);

        ivNameEdit = (ImageView) findViewById(R.id.iv_name_edit);
        ivStatusEdit = (ImageView) findViewById(R.id.iv_status_edit);
        rivPic = (RoundedImageView) findViewById(R.id.iv_profile);
        tvPick = (TextView) findViewById(R.id.tv_pick);
        tvDisplayName = (TextView) findViewById(R.id.tv_display_name);
        tvStatus = (TextView) findViewById(R.id.tv_status);
    }

    @Override
    public void registerListeners() {
        ivStatusEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StatusActivity.class);
                startActivity(intent);
            }
        });
        ivNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameAlert();
            }
        });
        tvPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private boolean checkPermission() {
        if (AppUtility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            return false;
        } else
            return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (ACTIVITY_IMAGE_GET_REQUEST_CODE == requestCode) {
                Uri filePath = data.getData();
                try {
                    Bitmap bitmap = decodeUri(filePath);
                    if (bitmap != null) {
                        updateImage1(bitmap);
                    } else {
                        DisplayInfo.showToast(mContext, "Unable to find the file from directory. Please check the permission");
                    }
                    //call the standard crop action intent (the user device may not support it)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserDetails();
    }

    private void setUserDetails() {
        tvDisplayName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
        tvStatus.setText(mPref.getStringPrefValue(PreferenceConstants.USER_STATUS));
        String imagePath = mPref.getStringPrefValue(PreferenceConstants.DP_PATH);
        setProfilePic(imagePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showNameAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final EditText etName = new EditText(mContext);
        etName.setHint("Display name");
        etName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
        etName.setPadding(20, 20, 20, 20);
        builder.setView(etName);

        builder.setMessage("Enter your new display name");
        builder.setTitle("Display name");
        AlertDialog dialog = builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    DisplayInfo.showToast(mContext, "Display name is mandatory");
                    showNameAlert();
                } else if (name.length() < 3 || name.length() > 32) {
                    DisplayInfo.showToast(mContext, "Display name should be in between of 3-32");
                    showNameAlert();
                } else {
                    setDisplayName(name);
                }
                dialogInterface.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        dialog.show();
    }

    private void showFileChooser() {
        if (checkPermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_IMAGE_GET_REQUEST_CODE);
        }
    }

    private void setDisplayName(final String displayName) {
        final String status = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS);

        CloudStatusRequest request = new CloudStatusRequest();
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setStatus(status);
        request.setUserDisplayName(displayName);

        boolean value = mPref.getBooleanPrefValue(PreferenceConstants.USER_PRIVACY_STATUS);
        int privacy = 0;
        if (value)
            privacy = 1;
        request.setPrivacyValue(privacy);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                tvDisplayName.setText(displayName);
                mPref.setStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME, displayName);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Some thing went wrong. Please try again.");
            }
        };
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, "Updating display name");
            CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).setStatus(request, callback);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    private void setProfilePic(final String imageUri) {
//        final Bitmap bitmap = BitmapFactory.decodeFile(imageUri);

        Glide.with(this).load(imageUri).centerCrop().into(rivPic);
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, null);
    }

    private void updateImage1(final Bitmap bitmap) {
        final String image = getStringImage(bitmap);
        final CloudUploadProfileRequest request = new CloudUploadProfileRequest();
        request.setToken(new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setImageBase64(image);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                Log.v(TAG, "SUCCESS || PROFILE UPOAD SUCCESS|| PROFILE UPOAD SUCCESS|| PROFILE UPOAD SUCCESS|| PROFILE UPOAD SUCCESS");
//                createDirectoryAndSaveFile(bitmap, true);
                CloudUploadProfileResponse response = (CloudUploadProfileResponse) cloudResponse;
                String imageUri = response.getIconUrl();
                Log.d(TAG, imageUri);
                setProfilePic(imageUri);
                mPref.setStringPrefValue(PreferenceConstants.DP_PATH, imageUri);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                Log.v(TAG, "FAILED || PROFILE UPOAD FAILED|| PROFILE UPOAD FAILED|| PROFILE UPOAD FAILED|| PROFILE UPOAD FAILED");
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Updating failed");
            }
        };
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
            CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).uploadImage(request, callback);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
