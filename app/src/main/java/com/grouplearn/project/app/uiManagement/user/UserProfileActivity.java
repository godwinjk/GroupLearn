package com.grouplearn.project.app.uiManagement.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.InterestRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.contact.ContactListActivity;
import com.grouplearn.project.app.uiManagement.interactor.ContactInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.interest.add.CloudAddInterestRequest;
import com.grouplearn.project.cloud.interest.add.CloudAddInterestResponse;
import com.grouplearn.project.cloud.interest.delete.CloudDeleteInterestRequest;
import com.grouplearn.project.cloud.interest.get.CloudGetInterestRequest;
import com.grouplearn.project.cloud.interest.get.CloudGetInterestResponse;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;
import com.grouplearn.project.cloud.userManagement.upload.CloudUploadProfileRequest;
import com.grouplearn.project.cloud.userManagement.upload.CloudUploadProfileResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;
import com.grouplearn.project.utilities.views.RoundedImageView;
import com.grouplearn.project.utilities.views.SpaceItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int ACTIVITY_IMAGE_GET_REQUEST_CODE = 101;
    private static final int ACTIVITY_IMAGE_CROP_REQUEST_CODE = 2;
    private static final String TAG = "UserProfileActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;

    private Context mContext;
    private TextView tvName, tvStatus, tvEdit, tvMainName, tvEmail;

    private CheckBox cbVisibility;
    private ImageView ivProPicBack, ivBack;
    private RoundedImageView rivProPic;
    private RecyclerView rvInterests, rvSkills;

    private InterestRecyclerAdapter mInterestRecyclerAdapter;
    private InterestRecyclerAdapter mSkillRecyclerAdapter;
    private AppSharedPreference mPref;
    private CardView cvVisibility;
    private boolean isOtherUser;
    private GLContact mContact;
    private ImageView ivAddInterest, ivAddSkills;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mContext = this;
        mPref = new AppSharedPreference(mContext);

        initializeWidgets();
        registerListeners();
        getData();
        fetchInterests();
    }

    @Override
    public void initializeWidgets() {
        cbVisibility = (CheckBox) findViewById(R.id.cb_visibility);
        rivProPic = (RoundedImageView) findViewById(R.id.img_user_round);
        ivProPicBack = (ImageView) findViewById(R.id.img_user_back);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        tvName = (TextView) findViewById(R.id.tv_display_name);
        tvMainName = (TextView) findViewById(R.id.tv_main_display_name);
        tvEmail = (TextView) findViewById(R.id.tv_main_email);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        ivBack = (ImageView) findViewById(R.id.iv_back);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnConnect = (Button) findViewById(R.id.btn_connect);

        ivAddInterest = (ImageView) findViewById(R.id.iv_add_interest);
        ivAddSkills = (ImageView) findViewById(R.id.iv_add_skills);
        cvVisibility = (CardView) findViewById(R.id.cv_visibility);

        rvInterests = (RecyclerView) findViewById(R.id.rv_interests);
        rvSkills = (RecyclerView) findViewById(R.id.rv_skills);

        mInterestRecyclerAdapter = new InterestRecyclerAdapter();
        mSkillRecyclerAdapter = new InterestRecyclerAdapter();

        rvInterests.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        rvInterests.setAdapter(mInterestRecyclerAdapter);

        rvSkills.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        rvSkills.setAdapter(mSkillRecyclerAdapter);

        rvInterests.addItemDecoration(new SpaceItemDecoration(10));
        rvSkills.addItemDecoration(new SpaceItemDecoration(10));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getData() {
        mContact = getIntent().getParcelableExtra("user");
        long userId = mPref.getLongPrefValue(PreferenceConstants.USER_ID);
        String imagePath = null;

        if (mContact == null || userId == mContact.getContactUserId()) {
            isOtherUser = false;
            ivAddInterest.setVisibility(View.VISIBLE);
            ivAddSkills.setVisibility(View.VISIBLE);
            imagePath = mPref.getStringPrefValue(PreferenceConstants.DP_PATH);
            btnConnect.setVisibility(View.GONE);
        } else {
            isOtherUser = true;
            ivAddInterest.setVisibility(View.GONE);
            ivAddSkills.setVisibility(View.GONE);
            imagePath = mContact.getIconUrl();
            btnConnect.setVisibility(View.VISIBLE);
        }
        if (imagePath != null) {
            setProfilePic(imagePath);
        }
        setUserPreference();
    }

    private void setUserDetails() {
        if (isOtherUser) {
            tvEmail.setText(mContact.getContactMailId());
            tvMainName.setText(mContact.getContactName());

            tvName.setText(mContact.getContactName());
            tvStatus.setText(mContact.getContactStatus());
            cbVisibility.setVisibility(View.GONE);
        } else {
            tvMainName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
            tvEmail.setText(mPref.getStringPrefValue(PreferenceConstants.USER_EMAIL_ID));

            tvName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
            tvStatus.setText(mPref.getStringPrefValue(PreferenceConstants.USER_STATUS));
            cbVisibility.setVisibility(View.VISIBLE);
            cbVisibility.setChecked(mPref.getBooleanPrefValue(PreferenceConstants.USER_PRIVACY_STATUS));
        }
    }

    @Override
    public void registerListeners() {
        ivAddInterest.setOnClickListener(this);
        ivAddSkills.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        OnRecyclerItemClickListener clickListener = new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, int action, View v) {
                GLInterest interest = (GLInterest) model;
                deleteInterest(interest, interest.isSkill());
            }

            @Override
            public void onItemLongClicked(int position, Object model, int action, View v) {

            }
        };
        mInterestRecyclerAdapter.setOnRecyclerItemClickListener(clickListener);
        mSkillRecyclerAdapter.setOnRecyclerItemClickListener(clickListener);

        cbVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrivacy(cbVisibility.isChecked());
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, UserEditActivity.class));
            }
        });
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
            } else if (ACTIVITY_IMAGE_CROP_REQUEST_CODE == requestCode) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_interest:
                addInterest(false);
                break;
            case R.id.tv_contacts:
                startActivity(new Intent(mContext, ContactListActivity.class));
                break;
            case R.id.iv_add_skills:
                addInterest(true);
                break;
            case R.id.btn_connect:
                requestContact();
                break;

        }
    }

    private void requestContact() {
        ContactInteractor interactor = new ContactInteractor(mContext);
        DisplayInfo.showLoader(mContext, "Please wait");
        interactor.requestToConnect(mContact, new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                btnConnect.setEnabled(false);
                DisplayInfo.showToast(mContext, "SuccessFully sent request");
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Failed to sent request");
            }
        });
    }

    private void addInterest(final boolean isSkill) {
        AlertDialog.Builder alertDialog = AppAlertDialog.getAlertDialog(mContext);
        View v = getLayoutInflater().inflate(R.layout.layout_interest_alert, null);
        final TextInputLayout etTextInput = (TextInputLayout) v.findViewById(R.id.et_interest);
        String message = "Interest";
        if (isSkill) message = "Skill";
        String hint = "Enter your interest";
        if (isSkill) hint = "Enter your skill";
        etTextInput.setHint(hint);
        alertDialog.setTitle(message);
        alertDialog.setView(v);
        final String finalMessage = message;
        final Dialog dialog = alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String interestName = etTextInput.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(interestName) && interestName.length() > 3 && interestName.length() < 30) {
                    addInterest(interestName, isSkill);
                } else {
                    DisplayInfo.showToast(mContext, "Invalid " + finalMessage + " name");
                    addInterest(isSkill);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        dialog.show();
    }

    private void deleteInterest(final GLInterest interest, final boolean isSkill) {
        AppAlertDialog alertDialog = AppAlertDialog.getAlertDialog(mContext);
        alertDialog.setTitle("Warning");
        String message = "Are you sure want to remove the interest?";
        if (interest.getInterestType() == GLInterest.SKILL)
            message = "Are you sure want to remove the skill?";
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFromCloud(interest, isSkill);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void deleteFromCloud(final GLInterest interest, boolean isSkill) {
        CloudDeleteInterestRequest request = new CloudDeleteInterestRequest();
        request.setToken(new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN));
        if (isSkill) {
            request.setSkills(interest);
        } else {
            request.setInterests(interest);
        }

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                if (!interest.isSkill()) {
                    mInterestRecyclerAdapter.getInterests().remove(interest);
                    mInterestRecyclerAdapter.notifyDataSetChanged();
                } else {
                    mSkillRecyclerAdapter.getInterests().remove(interest);
                    mSkillRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Something went wrong");
            }
        };
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
            CloudConnectManager.getInstance(mContext).getCloudInterestManager(mContext).deleteInterest(request, callback);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    private void addInterest(String interestName, boolean isSkill) {
        CloudAddInterestRequest request = new CloudAddInterestRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        final GLInterest interest = new GLInterest();
        interest.setInterestName(interestName);
        if (isSkill) {
            request.setSkills(interest);
        } else {
            request.setInterests(interest);
        }

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                final ArrayList<GLInterest> interests = mInterestRecyclerAdapter.getInterests();
                CloudAddInterestResponse response = (CloudAddInterestResponse) cloudResponse;
                ArrayList<GLInterest> interestsFromCloud = response.getInterests();
                if (interestsFromCloud != null && interestsFromCloud.size() > 0) {
                    interests.addAll(interestsFromCloud);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mInterestRecyclerAdapter.setInterests(interests, isOtherUser);
                        }
                    });
                }
                final ArrayList<GLInterest> skills = mSkillRecyclerAdapter.getInterests();
                ArrayList<GLInterest> skillsFromCloud = response.getSkills();
                if (skillsFromCloud != null && skillsFromCloud.size() > 0) {
                    skills.addAll(skillsFromCloud);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSkillRecyclerAdapter.setInterests(skills, isOtherUser);
                        }
                    });
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                Log.e(TAG, "Interest fetch error");
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudInterestManager(mContext).addInterest(request, callback);
    }

    private void setPrivacy(final boolean value) {
        final String status = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS);
        final String displayName = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME);

        CloudStatusRequest request = new CloudStatusRequest();
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setStatus(status);
        request.setUserDisplayName(displayName);
        int privacy = 1;
        if (value)
            privacy = 0;
        request.setPrivacyValue(privacy);

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
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, "Updating privacy...");
            CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).setStatus(request, callback);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
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
                tvName.setText(displayName);
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
        int width = rivProPic.getWidth() <= 0 ? BitmapImageViewTarget.SIZE_ORIGINAL : rivProPic.getWidth();
        int height = rivProPic.getHeight() <= 0 ? BitmapImageViewTarget.SIZE_ORIGINAL : rivProPic.getHeight();
        Glide.with(this).load(imageUri).asBitmap().into(new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                rivProPic.setImageBitmap(resource);
                ivProPicBack.setImageBitmap(resource);
            }
        });
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
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

    private void setUserPreference() {
        if (isOtherUser) {
            tvEdit.setVisibility(View.GONE);
            btnConnect.setVisibility(View.VISIBLE);
            cvVisibility.setVisibility(View.GONE);
            tvName.setText(mContact.getContactName());
            tvStatus.setText(mContact.getContactStatus());
        } else {
            tvEdit.setVisibility(View.VISIBLE);
            cvVisibility.setVisibility(View.VISIBLE);
            btnConnect.setVisibility(View.GONE);
        }
        setUserDetails();
    }

    private void fetchInterests() {
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudGetInterestResponse response = (CloudGetInterestResponse) cloudResponse;
                ArrayList<GLInterest> interests = response.getInterests();
                setDataToAdapter(interests);
                setDataToSkillAdapter(response.getSkills());
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                Log.e(TAG, "Interest fetch error");
            }
        };
        CloudGetInterestRequest request = new CloudGetInterestRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        if (isOtherUser) {
            request.setUserId(mContact.getContactUserId());
        } else {
            request.setUserId(new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID));
        }
        if (AppUtility.checkInternetConnection()) {
            CloudConnectManager.getInstance(mContext).getCloudInterestManager(mContext).getInterest(request, callback);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setDataToAdapter(ArrayList<GLInterest> interests) {
        mInterestRecyclerAdapter.setInterests(interests, isOtherUser);
    }

    private void setDataToSkillAdapter(ArrayList<GLInterest> interests) {
        mSkillRecyclerAdapter.setInterests(interests, isOtherUser);
    }
}
