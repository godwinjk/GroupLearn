package com.grouplearn.project.cloud;

import android.net.Uri;

/**
 * Created by WiSilica on 14-01-2017 20:20.
 *
 * @Author : Godwin Joseph Kurinjikattu
 */

public interface ThumbNailLoaderCallback {
    void onThumbNailLoaded(String siteName, Uri thumbNailLoaded);

    void onThumbNailLoadingFailed();
}
