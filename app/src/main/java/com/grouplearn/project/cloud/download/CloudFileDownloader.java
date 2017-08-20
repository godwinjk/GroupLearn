package com.grouplearn.project.cloud.download;

import android.content.Context;
import android.os.AsyncTask;

import com.grouplearn.project.app.file.FileManager;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.cloud.upload.CloudFileUploader;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.grouplearn.project.app.file.FileManager.getFileNameFromUri;

/**
 * Created by Godwin on 11-05-2017 16:35 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class CloudFileDownloader extends AsyncTask<Void, String, Void> {
    private static final String TAG = CloudFileUploader.class.getName();
    private OnFileDownloadListener onFileDownloadListener;
    private Context mContext;
    private GLMessage mMessage;

    public CloudFileDownloader(Context mContext, GLMessage mMessage, OnFileDownloadListener onFileDownloadListener) {
        this.onFileDownloadListener = onFileDownloadListener;
        this.mContext = mContext;
        this.mMessage = mMessage;
    }

    @Override
    protected Void doInBackground(Void... params) {
        int count;
        try {
            if (onFileDownloadListener != null) {
                onFileDownloadListener.onDownloadInStarted(mMessage);
            }
            String f_url = mMessage.getCloudFilePath();
            URL url = new URL(f_url);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.connect();
            conection.setReadTimeout(1000 * 60);
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            FileManager manager = new FileManager();
            File directory = manager.getDirectory(mMessage.getMessageType());
            File file = new File(directory, getFileNameFromUri(f_url));
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                String builder = String.valueOf( count / (1024f * 1024f)) +
                        "/" +
                        (float) total / (1024f * 1024f);

                mMessage.setDownloadSize(builder);

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            mMessage.setLocalFilePath(file.getAbsolutePath());
            updateDatabase(mMessage);

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            if (onFileDownloadListener != null)
                onFileDownloadListener.onDownloadFailed(mMessage, new AppError(ErrorHandler.DOWNLOAD_FAILED, ErrorHandler.ErrorMessage.DOWNLOAD_FAILED + e.getCause()));
        }
        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onFileDownloadListener != null)
            onFileDownloadListener.onDownloadSuccess(mMessage);
    }

    private void updateDatabase(GLMessage message) {
        ChatDbHelper helper = new ChatDbHelper(mContext);
        helper.updateMessageInDb(message);
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        if (onFileDownloadListener != null) {

            int progr = Integer.parseInt(progress[0]);
            Log.d(TAG, "Progress : " + progr);
            onFileDownloadListener.onDownloadInProgress(mMessage, progr);
        }
    }

    public void stopDownoading() {

    }
}
