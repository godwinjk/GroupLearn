package com.grouplearn.project.app.uiManagement.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.MyApplication;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.adapter.InterestRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.contact.ContactListActivity;
import com.grouplearn.project.app.uiManagement.group.GroupListActivity;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.bean.GLUser;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
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
import com.grouplearn.project.utilities.views.SpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int ACTIVITY_IMAGE_GET_REQUEST_CODE = 101;
    private static final int ACTIVITY_IMAGE_CROP_REQUEST_CODE = 2;
    private static final String TAG = "UserProfileActivity";
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar mToolbar;
    Context mContext;
    TextView tvName, tvStatus, tvContacts, tvSubscribedGroups;
    CheckBox cbVisibility;
    ImageView ivProfile, ivSelect;

    RecyclerView rvInterests;

    InterestRecyclerAdapter mInterestRecyclerAdapter;
    AppSharedPreference mPref;
    ImageView ivNameEdit, ivStatusEdit;
    CardView cvVisibility, cvUserSpecificDetails;
    boolean isOtherUser;
    String userName;
    GLUser userModel;
    ImageView ivAddInterest;

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
        initializeWidgets();
        registerListeners();
        String imagePath = null;
        if (appUserName.equals(userName)) {
            isOtherUser = false;
            ivAddInterest.setVisibility(View.VISIBLE);
            cvUserSpecificDetails.setVisibility(View.VISIBLE);
            imagePath = mPref.getStringPrefValue(PreferenceConstants.DP_PATH);
        } else {
            isOtherUser = true;
            ivAddInterest.setVisibility(View.GONE);
            cvUserSpecificDetails.setVisibility(View.GONE);
            imagePath = userModel.getIconUrl();
        }
        if (imagePath != null) {
            setProfilePic(imagePath);
        }

        setUserPreference();
    }

    @Override
    public void initializeWidgets() {
        cbVisibility = (CheckBox) findViewById(R.id.cb_visibility);

        cvUserSpecificDetails = (CardView) findViewById(R.id.cv_user_details);

        tvName = (TextView) findViewById(R.id.tv_display_name);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvContacts = (TextView) findViewById(R.id.tv_contacts);
        tvSubscribedGroups = (TextView) findViewById(R.id.tv_subscribed_group);

        ivNameEdit = (ImageView) findViewById(R.id.iv_name_edit);
        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        ivSelect = (ImageView) findViewById(R.id.iv_select);
        ivAddInterest = (ImageView) findViewById(R.id.iv_add_interest);
        ivStatusEdit = (ImageView) findViewById(R.id.iv_status_edit);
        cvVisibility = (CardView) findViewById(R.id.cv_visibility);

        rvInterests = (RecyclerView) findViewById(R.id.rv_interests);
        mInterestRecyclerAdapter = new InterestRecyclerAdapter();
        rvInterests.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        rvInterests.setAdapter(mInterestRecyclerAdapter);

        rvInterests.addItemDecoration(new SpaceItemDecoration(10));
        /**
         * Settings for other User
         */
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOtherUser) {
//            setDataToAdapter();
        }
        fetchInterests();
    }

    private void setUserDetails() {
        if (isOtherUser) {
            collapsingToolbarLayout.setTitle(userModel.getUserDisplayName());
            tvName.setText(userModel.getUserDisplayName());
            tvStatus.setText(userModel.getUserStatus());
            cbVisibility.setVisibility(View.GONE);
        } else {
            collapsingToolbarLayout.setTitle(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
            tvName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
            tvStatus.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS));
            cbVisibility.setVisibility(View.VISIBLE);
            cbVisibility.setChecked(mPref.getBooleanPrefValue(PreferenceConstants.USER_PRIVACY_STATUS));
        }
    }

    @Override
    public void registerListeners() {
        ivStatusEdit.setOnClickListener(this);
        ivAddInterest.setOnClickListener(this);
        tvContacts.setOnClickListener(this);
        tvSubscribedGroups.setOnClickListener(this);

        mInterestRecyclerAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GLInterest interest = (GLInterest) model;
                deleteInterest(interest);
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });

        cbVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(cbVisibility.isChecked());
            }
        });

        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_IMAGE_GET_REQUEST_CODE);
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
//                        createDirectoryAndSaveFile(bitmap, true);
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
//                createDirectoryAndSaveFile(thePic, true);
                ivProfile.setImageBitmap(thePic);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_status_edit:
                startActivity(new Intent(mContext, StatusActivity.class));
                break;
            case R.id.iv_add_interest:
                addInterest();
                break;
            case R.id.tv_contacts:
                startActivity(new Intent(mContext, ContactListActivity.class));
                break;
            case R.id.tv_subscribed_group:
                startActivity(new Intent(mContext, GroupListActivity.class));
                break;
        }
    }

    private void cropImage(Uri imageUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(imageUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("outputX", 500);
        cropIntent.putExtra("outputY", 500);
        cropIntent.putExtra("return-data", true);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(cropIntent, ACTIVITY_IMAGE_CROP_REQUEST_CODE);
    }

    private void addInterest() {
        AlertDialog.Builder alertDialog = AppAlertDialog.getAlertDialog(mContext);
        View v = getLayoutInflater().inflate(R.layout.layout_interest_alert, null);
        final TextInputLayout etTextInput = (TextInputLayout) v.findViewById(R.id.et_interest);
        alertDialog.setTitle("Interest");
        alertDialog.setView(v);
        final Dialog dialog = alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String interestName = etTextInput.getEditText().getText().toString();
                if (!TextUtils.isEmpty(interestName) && interestName.length() > 3 && interestName.length() < 30) {
                    addInterest(interestName);
                } else {
                    DisplayInfo.showToast(mContext, "Invalid interest name");
                    addInterest();
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

    private void deleteInterest(final GLInterest interest) {
        AppAlertDialog alertDialog = AppAlertDialog.getAlertDialog(mContext);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Are you sure want to remove the interest?");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFromCloud(interest);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void deleteFromCloud(final GLInterest interest) {
        CloudDeleteInterestRequest request = new CloudDeleteInterestRequest();
        request.setToken(new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setInterests(interest);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                mInterestRecyclerAdapter.getInterests().remove(interest);
                mInterestRecyclerAdapter.notifyDataSetChanged();
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

    private void addInterest(String interestName) {
        CloudAddInterestRequest request = new CloudAddInterestRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        final GLInterest interest = new GLInterest();
        interest.setInterestName(interestName);
        request.setInterests(interest);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                final ArrayList<GLInterest> interests = mInterestRecyclerAdapter.getInterests();
                CloudAddInterestResponse response = (CloudAddInterestResponse) cloudResponse;
                ArrayList<GLInterest> interestsFromCloud = response.getInterests();
                interests.addAll(interestsFromCloud);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInterestRecyclerAdapter.setInterests(interests);
                    }
                });
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                Log.e(TAG, "Interest fetch error");
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudInterestManager(mContext).addInterest(request, callback);
    }

    private void setVisibility(final boolean value) {
        final String status = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS);
        final String displayName = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME);

        CloudStatusRequest request = new CloudStatusRequest();
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setStatus(status);
        request.setUserDisplayName(displayName);
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

    private void setProfilePic(final String imageUri) {
//        final Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
        mPref.setStringPrefValue(PreferenceConstants.DP_PATH, imageUri);
        Glide.with(mContext)
                .load(imageUri)
                .asBitmap()
                .centerCrop()
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(new BitmapImageViewTarget(ivProfile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivProfile.setImageDrawable(circularBitmapDrawable);
                        Palette palette = Palette.from(resource).generate();

                        Palette.Swatch swatch = palette.getDarkMutedSwatch();
                        if (swatch != null) {
                            int rgb = swatch.getRgb();
                            if (collapsingToolbarLayout != null) {
                                collapsingToolbarLayout.setStatusBarScrimColor(rgb);
                                collapsingToolbarLayout.setContentScrimColor(rgb);
                                collapsingToolbarLayout.setBackgroundColor(rgb);
                            }
                            if (mToolbar != null)
                                mToolbar.setBackgroundColor(swatch.getRgb());
                        }
                    }
                });
    }

    private void uploadProPic(final Bitmap bitmap) {
        String image = getStringImage(bitmap);
        final File file1 = new File(new File(Environment.getExternalStorageDirectory() + "/GroupLearn"), "base64Pic.txt");


        JSONObject object = new JSONObject();
        try {
            FileWriter writer = new FileWriter(file1);
            writer.append(image);
            writer.flush();
            writer.close();
            object.put("image", image);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, CloudConstants.getBaseUrl() + "profile-upload", object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new ArrayMap<String, String>();
                String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
                mHeaders.put("token", token);
                return mHeaders;
            }
        };
        // Adding request to request queue
        MyApplication.getAppContext().addToRequestQueue(jsonObjReq);
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

    private void createDirectoryAndSaveFile(final Bitmap imageToSave, final boolean show) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                imageToSave.compress(Bitmap.CompressFormat.PNG, 10, outputStream);

                File directory = new File(Environment.getExternalStorageDirectory() + "/GroupLearn");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                int i = new Random().nextInt(5000);
                final File file = new File(new File(Environment.getExternalStorageDirectory() + "/GroupLearn"), "profilePic" + i + ".png");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Uri uri = Uri.parse(file.getAbsolutePath());
                            setProfilePic(uri.toString());
//                            updateImage1(imageToSave);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void uploadImage(final Bitmap bitmap) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CloudConstants.getBaseUrl() + "profile-upload",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading.dismiss();
                        Toast.makeText(mContext, "error", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);

                Map<String, String> params = new Hashtable<String, String>();
                params.put("image", image);
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
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
            ivNameEdit.setVisibility(View.GONE);
            ivStatusEdit.setVisibility(View.GONE);
            cvVisibility.setVisibility(View.GONE);
            ivSelect.setVisibility(View.GONE);
            collapsingToolbarLayout.setTitle(userModel.getUserDisplayName());
            tvName.setText(userModel.getUserDisplayName());
            tvStatus.setText(userModel.getUserStatus());
        } else {
            ivNameEdit.setVisibility(View.VISIBLE);
            ivStatusEdit.setVisibility(View.VISIBLE);
            cvVisibility.setVisibility(View.VISIBLE);
            ivProfile.setVisibility(View.VISIBLE);
            ivSelect.setVisibility(View.VISIBLE);
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
            request.setUserId(userModel.getUserId());
        } else {
            request.setUserId(new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID));
        }
        if (AppUtility.checkInternetConnection()) {
            CloudConnectManager.getInstance(mContext).getCloudInterestManager(mContext).getInterest(request, callback);
        }
    }

    private void setDataToAdapter(ArrayList<GLInterest> interests) {
        mInterestRecyclerAdapter.setInterests(interests);
    }
}
