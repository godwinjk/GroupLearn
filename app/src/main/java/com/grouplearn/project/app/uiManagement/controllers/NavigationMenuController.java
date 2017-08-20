package com.grouplearn.project.app.uiManagement.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by Godwin Joseph on 01-06-2016 11:08 for Group Learn application.
 */
public class NavigationMenuController {
    Context mContext;
    AppSharedPreference mPref;
    NavigationView mNavigationView;
    View mHeaderView;
    private int mode = 0;

    public NavigationMenuController(Context mContext, NavigationView navigationView, int mode) {
        this.mContext = mContext;
        this.mPref = new AppSharedPreference(mContext);
        this.mNavigationView = navigationView;
        this.mode = mode;
    }

    public NavigationView createNavigationMenu() {
        if (mode == 0) {
            mNavigationView.getMenu().findItem(R.id.nav_dash_board).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_user_list).setVisible(true);
        } else {
            mNavigationView.getMenu().findItem(R.id.nav_dash_board).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_user_list).setVisible(false);
        }
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

    private NavigationView setUpHeaderView(final View headerView) {
        TextView mDisplayName = (TextView) headerView.findViewById(R.id.tv_display_name);
        TextView mUserName = (TextView) headerView.findViewById(R.id.tv_user_name);
        final ImageView ivProfile = (ImageView) headerView.findViewById(R.id.iv_profile);


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

//                    Bitmap bitmap = Bitmap.createBitmap(resource);
                    Bitmap bitmap = Bitmap.createScaledBitmap(resource, resource.getWidth() + 1, resource.getHeight() + 1, false);
                    int width = headerView.getWidth() <= 0 ? 200 : headerView.getWidth();

                    bitmap = getResizedBitmap(bitmap, width);
                    bitmap = blurRenderScript(mContext, bitmap, 3);
                    bitmap = getBlackOverLayeredBitmap(bitmap);
                    Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);

                }
            });
        }
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

        Bitmap resizedBitmap = createBitmap(bm, 0, 0, width, height, matrix, true);

        bm.recycle();

        return resizedBitmap;
    }

    public Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = createBitmap(
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

    private Bitmap getBlackOverLayeredBitmap(Bitmap bitmap) {

        Bitmap colorOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Bitmap colorOverlayb = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(colorOverlayb);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(100);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
//        canvas.drawBitmap(colorOverlay, 0, 0, paint);

        return colorOverlayb;
    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}