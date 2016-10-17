package com.grouplearn.project.app.uiManagement;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Godwin Joseph on 03-08-2016 11:32 for Group Learn application.
 */
public abstract class BaseFragment extends Fragment {
    protected abstract void initializeWidgets(View v);

    protected abstract void registerListeners();

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if (getActivity() == null)
            return;
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
