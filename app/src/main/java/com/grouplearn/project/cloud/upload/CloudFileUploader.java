package com.grouplearn.project.cloud.upload;

import android.content.Context;
import android.os.AsyncTask;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.utilities.ChatUtilities;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.grouplearn.project.cloud.BaseManager.RESPONSE;

/**
 * Created by Godwin on 07-05-2017 16:39 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class CloudFileUploader extends AsyncTask<String, Integer, String> {
    private static final String TAG = CloudFileUploader.class.getName();
    private OnFileUploadListener onFileUploadListener;
    private Context mContext;
    private File mFile;
    private GLMessage mMessage;

    public CloudFileUploader(Context mContext, GLMessage mMessage, OnFileUploadListener onFileUploadListener) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.mFile = mMessage.getFile();
        this.onFileUploadListener = onFileUploadListener;
    }

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int serverResponseCode = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(mFile);
            URL url = new URL(CloudConstants.getFileUploadBaseUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("fileToUpload", mFile.getName());

            AppSharedPreference mPref = new AppSharedPreference(mContext);
            String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);

            conn.setRequestProperty("token", token);
            conn.setRequestProperty("groupId", "" + mMessage.getReceiverId());
            conn.setRequestProperty("message", "" + mMessage.getMessageBody());
            conn.setRequestProperty("messageType", "" + mMessage.getMessageType());
            conn.setRequestProperty("senderName", "" + mMessage.getSenderName());

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + "fileToUpload" + "\";filename="
                    + mFile.getName() + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i(TAG, "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            int progress = 0;
            long totalSize = mFile.length();
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                progress += bytesRead; // Here progress is total uploaded bytes

                publishProgress((int) ((progress * 100) / totalSize)); // sending progress percent to publishProgress
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException | FileNotFoundException | ProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                if (onFileUploadListener != null) {
                    onFileUploadListener.onUploadFailed(mMessage, new AppError(ErrorHandler.ERROR_FROM_SERVER, ErrorHandler.ErrorMessage.ERROR_FROM_SERVER));
                }
            }
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject resObject = new JSONObject(s);
            parseResponse(resObject);

        } catch (JSONException e) {
            e.printStackTrace();
            if (onFileUploadListener != null) {
                onFileUploadListener.onUploadFailed(mMessage, new AppError(ErrorHandler.INVALID_RESPONSE_JSON_EXCEPTION, ErrorHandler.ErrorMessage.INVALID_RESPONSE_JSON_EXCEPTION));
            }
        }
        Log.v(TAG, s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (onFileUploadListener != null) {
            onFileUploadListener.onUploadInProgress(mMessage, values[0]);
        }
    }

    public void setOnFileUploadListener(OnFileUploadListener onFileUploadListener) {
        this.onFileUploadListener = onFileUploadListener;
    }

    private void parseResponse(JSONObject jsonObject) {

        if (jsonObject != null) {
            jsonObject = jsonObject.optJSONObject(RESPONSE);
            if (jsonObject != null) {
                JSONObject statusObject = jsonObject.optJSONObject("Status");
                JSONObject dataObject = jsonObject.optJSONObject("Data");
                if (dataObject != null) {
                    String cloudUrl = dataObject.optString("fileUrl");
                    long messageId = dataObject.optLong("messageId");
                    int messageType = dataObject.optInt("messageType");
                    String messageBody = dataObject.optString("messageBody");
                    mMessage.setMessageType(messageType);
                    mMessage.setMessageId(messageId);
                    mMessage.setMessageBody(messageBody);
                    mMessage.setCloudFilePath(cloudUrl);
                    mMessage.setSentStatus(ChatUtilities.SENT_SUCCESS);

                    if (onFileUploadListener != null) {
                        onFileUploadListener.onUploadSuccess(mMessage);
                    }
                } else {
                    if (onFileUploadListener != null) {
                        onFileUploadListener.onUploadFailed(mMessage, new AppError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                    }
                }
            } else {
                if (onFileUploadListener != null) {
                    onFileUploadListener.onUploadFailed(mMessage, new AppError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                }
            }
        } else {
            if (onFileUploadListener != null) {
                onFileUploadListener.onUploadFailed(mMessage, new AppError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
            }
        }
    }
}
