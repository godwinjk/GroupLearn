package com.grouplearn.project.app.uiManagement.group;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ProfileRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.contact.ContactListActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.user.UserProfileActivity;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLUser;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList.GetGroupSubscribersResponse;
import com.grouplearn.project.cloud.groupManagement.upload.CloudUploadGroupImageRequest;
import com.grouplearn.project.cloud.groupManagement.upload.CloudUploadGroupImageResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class GroupInfoActivity extends BaseActivity {
    private static final int ACTIVITY_IMAGE_GET_REQUEST_CODE = 101;
    private static final String TAG = "GroupInfoActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar mToolbar;
    Context mContext;
    TextView tvName, tvStatus;
    ImageView ivNameEdit, ivGroupImage, ivSelect;
    RecyclerView rvSubscribedGroups;
    ProfileRecyclerAdapter mRecyclerAdapter;
    AppSharedPreference mPref;

    GLGroup mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mToolbar = setupToolbar("Profile", true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("User Info");
        mContext = this;
        long groupUniqueId = getIntent().getLongExtra("groupCloudId", 0);
        mModel = new GroupDbHelper(mContext).getGroupInfo(groupUniqueId);
        if (mModel == null) {
            finish();
            return;
        }
        initializeWidgets();
        registerListeners();
        String imageUri = mModel.getIconUrl();
        if (imageUri != null) {
            setProfilePic(imageUri);
        }
        getSubscribers();
    }

    @Override
    public void initializeWidgets() {
        mPref = new AppSharedPreference(mContext);

        tvName = (TextView) findViewById(R.id.tv_group_name);
        tvStatus = (TextView) findViewById(R.id.tv_details);

        ivNameEdit = (ImageView) findViewById(R.id.iv_name_edit);
        ivGroupImage = (ImageView) findViewById(R.id.iv_group_pic);
        ivSelect = (ImageView) findViewById(R.id.iv_select);

        rvSubscribedGroups = (RecyclerView) findViewById(R.id.rv_subscribers);
        mRecyclerAdapter = new ProfileRecyclerAdapter(ProfileRecyclerAdapter.USER_LIST);
        rvSubscribedGroups.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSubscribedGroups.setAdapter(mRecyclerAdapter);

        collapsingToolbarLayout.setTitle(mModel.getGroupName());
        tvName.setText(mModel.getGroupName());
        tvStatus.setText(mModel.getGroupDescription());
    }

    @Override
    public void registerListeners() {
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        mRecyclerAdapter.setItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                GLUser userModel = (GLUser) model;
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("user", userModel);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    private void showFileChooser() {
        if (checkPermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_IMAGE_GET_REQUEST_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_invite:
                invite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void invite() {
        Intent intent = new Intent(mContext, ContactListActivity.class);
        intent.putExtra("groupCloudId", mModel.getGroupUniqueId());

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getSubscribers() {
        GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                GetGroupSubscribersResponse res = (GetGroupSubscribersResponse) cloudResponse;
                ArrayList<GLUser> userModels = res.getUserModels();
                setDataToAdapter(userModels);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {

            }
        };
        if (AppUtility.checkInternetConnection())
            interactor.getGroupSubscribers(mModel.getGroupUniqueId(), callback);
    }

    private void setDataToAdapter(ArrayList<GLUser> userModels) {
        mRecyclerAdapter.setUserList(userModels);
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

    private Bitmap setProfilePic(final String imageUri) {
        final Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
//        mPref.setStringPrefValue(PreferenceConstants.DP_PATH, imageUri);
        Glide.with(mContext)
                .load(imageUri)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(ivGroupImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivGroupImage.setImageDrawable(circularBitmapDrawable);
                        Palette palette = Palette.from(resource).generate();

                        Palette.Swatch swatch = palette.getDarkMutedSwatch();
                        if (swatch != null) {
                            int rgb = swatch.getRgb();
                            if (collapsingToolbarLayout != null) {
                                collapsingToolbarLayout.setStatusBarScrimColor(rgb);
                                collapsingToolbarLayout.setContentScrimColor(rgb);
                                collapsingToolbarLayout.setBackgroundColor(rgb);
                                Window window = getWindow();
                                if (window != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        window.setStatusBarColor(rgb);
                                    }
                                }
                            }
                            if (mToolbar != null)
                                mToolbar.setBackgroundColor(swatch.getRgb());
                        }
                    }
                });
        return bitmap;
    }

    private void updateImage1(final Bitmap bitmap) {
        String image = getStringImage(bitmap);
        CloudUploadGroupImageRequest request = new CloudUploadGroupImageRequest();
        request.setToken(new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setImageBase64(image);
        request.setGroupId(mModel.getGroupUniqueId());
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                Log.v(TAG, "SUCCESS || PROFILE UPLOAD SUCCESS|| PROFILE UPLOAD SUCCESS|| PROFILE UPLOAD SUCCESS|| PROFILE UPLOAD SUCCESS");
                CloudUploadGroupImageResponse response = (CloudUploadGroupImageResponse) cloudResponse;
                String imageUri = response.getIconUrl();
                new GroupDbHelper(mContext).updateImageUri(mModel.getGroupUniqueId(), imageUri);
                setProfilePic(imageUri);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "Updating failed");
                Log.v(TAG, "FAILED || PROFILE UPOAD FAILED|| PROFILE UPOAD FAILED|| PROFILE UPOAD FAILED|| PROFILE UPOAD FAILED");
            }
        };
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
            CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).uploadImage(request, callback);
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
        showFileChooser();
    }
}
