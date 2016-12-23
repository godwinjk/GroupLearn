package com.grouplearn.project.app.uiManagement.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;

/**
 * Created by Godwin Joseph on 01-06-2016 11:08 for Group Learn application.
 */
public class NavigationMenuController {
    Context mContext;
    AppSharedPreference mPref;
    NavigationView mNavigationView;
    View mHeaderView;

    public NavigationMenuController(Context mContext, NavigationView navigationView) {
        this.mContext = mContext;
        this.mPref = new AppSharedPreference(mContext);
        this.mNavigationView = navigationView;
    }

    public NavigationView createNavigationMenu() {
        return setupNavigationHeader();
    }

    private NavigationView setupNavigationHeader() {
        mHeaderView = mNavigationView.getHeaderView(0);
        if (mHeaderView == null) {
            mHeaderView = mNavigationView.inflateHeaderView(R.layout.nav_header_home_group_list);
        }
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mContext.startActivity(new Intent(mContext, UserProfileActivity.class));
            }
        });
        return setUpHeaderView(mHeaderView);
    }

    private NavigationView setUpHeaderView(View headerView) {
        TextView mDisplayName = (TextView) headerView.findViewById(R.id.tv_display_name);
        TextView mUserName = (TextView) headerView.findViewById(R.id.tv_user_name);
        final ImageView ivProfile = (ImageView) headerView.findViewById(R.id.iv_profile);
//        LinearLayout ivProfile = (LinearLayout) headerView.findViewById(R.id.ll_nav_header);

        mDisplayName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME));
        mUserName.setText(mPref.getStringPrefValue(PreferenceConstants.USER_NAME));

        String imagePath = mPref.getStringPrefValue(PreferenceConstants.DP_PATH);
        if (imagePath != null) {
            Glide.with(mContext).load(imagePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivProfile.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        /*if (imagePath != null) {
            Bitmap b = BitmapFactory.decodeFile(imagePath);
            int width = headerView.getWidth() <= 0 ? 200 : headerView.getWidth();

            b = getResizedBitmap(b, width);
            b = blurRenderScript(mContext, b, 5);
            Drawable d = new BitmapDrawable(mContext.getResources(), b);
            ivProfile.setBackground(d);
        }*/
        return mNavigationView;
    }

    public void refreshMenu() {
        if (mNavigationView != null && mHeaderView != null) {
            mNavigationView.getMenu().clear();
            mNavigationView.removeHeaderView(mHeaderView);
            mNavigationView.inflateMenu(R.menu.activity_home_group_list_drawer);
        }
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float aspect = (float) width / height;

        float scaleWidth = newWidth;

        float scaleHeight = scaleWidth / aspect;        // yeah!

        // create a matrix for the manipulation

        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth / width, scaleHeight / height);
        // recreate the new Bitmap

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        bm.recycle();

        return resizedBitmap;
    }

    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}