package com.grouplearn.project.app.uiManagement.interfaces;

import android.view.View;

/**
 * Created by Godwin Joseph on 03-08-2016 15:14 for Group Learn application.
 */
public interface OnRecyclerItemClickListener {
    public void onItemClicked(int position, Object model, int action, View v);

    public void onItemLongClicked(int position, Object model, int action, View v);
}
