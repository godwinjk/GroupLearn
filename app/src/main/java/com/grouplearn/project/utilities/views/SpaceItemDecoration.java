package com.grouplearn.project.utilities.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by WiSilica on 17-12-2016 16:06 for GroupLearn application.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        private final int mSpaceHeight;

        public SpaceItemDecoration(int mSpaceHeight) {
            this.mSpaceHeight = mSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mSpaceHeight;
            outRect.top = mSpaceHeight;
            outRect.left = mSpaceHeight;
            outRect.right = mSpaceHeight;
        }
}
