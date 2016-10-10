package com.grouplearn.project.cloud.networkManagement;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Godwin Joseph on 13-05-2016 15:14 for Group Learn application.
 */

public class CloudHttpMethod extends AsyncTask<Void, Void, String> {
    public static final int GET_METHOD = 0,
            POST_METHOD = 1,
            PUT_METHOD = 2,
            HEAD_METHOD = 3,
            DELETE_METHOD = 4,
            TRACE_METHOD = 5,
            OPTIONS_METHOD = 6;
    private static final String TAG = "WiseConnectHttpMethod";
    final String timeOutException = "TIMED_OUT";
    final String BAD_REQUEST = "Response Code : ";

    Context mContext;
    HashMap<String, String> headerMap;
    String entityString;
    String url;
    int requestType = -1;
    CloudAPICallback callback;
    int TIME_OUT = 60 * 1000;

    public CloudHttpMethod(Context mContext, CloudAPICallback callback) {
        this.mContext = mContext;
        this.callback = callback;
    }


    public CloudHttpMethod(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Void... params) {

        int requestType = getRequestType();
        String response = "";
        try {
            if (!TextUtils.isEmpty(getUrl().toString()) && requestType != -1) {

                URL url = getUrl();
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection = setRequestMethod(urlConnection, requestType);
                urlConnection.setConnectTimeout(TIME_OUT);
                urlConnection.setReadTimeout(TIME_OUT);

                urlConnection.setDoInput(true);
                if (requestType != GET_METHOD) {                                                    // For GET, you have nothing to pass to the connection, so an OutputStream is not necessary.
                    urlConnection.setDoOutput(true);
                }
                if (requestType == GET_METHOD || requestType == DELETE_METHOD) {                    // For GET, you have nothing to pass to the connection, so an OutputStream is not necessary.
                    urlConnection.setDoOutput(false);
                } else
                    urlConnection.setDoOutput(true);
                urlConnection = setHeaderData(urlConnection);
                urlConnection = setEntity(urlConnection);

                int responseCode = urlConnection.getResponseCode();

                Log.d(TAG, "Response code....." + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    response = readResponseStream(urlConnection.getInputStream());
                    Log.v(TAG, response);
                } else {
                    try {
                        response = readResponseStream(urlConnection.getErrorStream());
                    } catch (Exception e) {

                    }
                    if (TextUtils.isEmpty(response))
                        response = BAD_REQUEST + responseCode;
                }
                urlConnection.disconnect();
                return response;

            } else {
                if (requestType == -1) {
                    Log.e(TAG, "INVALID REQUEST TYPE ||INVALID  REQUEST TYPE ||INVALID ");
                }
                Log.e(TAG, "INVALID URL ||INVALID URL ||INVALID URL ||INVALID URL ");
                return null;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            return timeOutException;
        } catch (SocketTimeoutException e) {
            return timeOutException;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, "ALREADY CONNECTED");
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (TextUtils.isEmpty(response)) {
            if (callback != null) {
                callback.onFailure(new CloudError(ErrorHandler.EMPTY_RESPONSE, ErrorHandler.ErrorMessage.EMPTY_RESPONSE));
            }
        } else if (response != null && response.equals(timeOutException)) {
            if (callback != null) {
                callback.onFailure(new CloudError(ErrorHandler.TIMEOUT_EXCEPTION, ErrorHandler.ErrorMessage.TIMEOUT_EXCEPTION));
            }
        } else if (response != null && response.contains(BAD_REQUEST)) {
            if (callback != null) {
                callback.onFailure(new CloudError(ErrorHandler.BAD_REQUEST, ErrorHandler.ErrorMessage.BAD_REQUEST));
            }
        } else {
            Log.d(TAG, "Cloud Response:" + response);
            try {
                if (callback != null) {

                    int index = response.indexOf("{");
                    if (index == -1) {
                        index = response.indexOf("[");
                    }
                    if (index != -1) {
                        response = response.substring(index);
                    }
                    callback.onSuccess(new JSONObject(response));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure(new CloudError(ErrorHandler.INVALID_RESPONSE_JSON_EXCEPTION, ErrorHandler.ErrorMessage.INVALID_RESPONSE_JSON_EXCEPTION));
                }
            }
        }
    }

    public Context getContext() {
        return mContext;
    }


    /**
     * Get the entity string.
     *
     * @return
     */
    public String getEntityString() {
        return entityString;
    }

    /**
     * Set the entity string.
     *
     * @param entity String
     */
    public void setEntityString(String entity) {
        this.entityString = entity;
    }

    /**
     * Reading the response stream.
     *
     * @param in
     * @return response string.
     */
    private String readResponseStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            Log.v(TAG, "Response from Server is : " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    /**
     * Set the entity to http url stream.
     *
     * @param urlConnection
     * @return httpUrlConnection
     * @throws IOException
     */
    private HttpURLConnection setEntity(HttpURLConnection urlConnection) throws IOException {
        if (getEntityString() != null) {
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(getEntityString());
            writer.flush();
            writer.close();
            outputStream.close();

            Log.w(TAG, "SET ENTITY TO STREAM  : " + getEntityString());
        } else {
            Log.w(TAG, "NO ENTITY DATA TO APPEND ||NO ENTITY DATA TO APPEND ||NO ENTITY DATA TO APPEND");
        }
        return urlConnection;
    }

    /**
     * Setting the header data.
     *
     * @param urlConnection
     * @return httpUrlConnection
     * @throws UnsupportedEncodingException
     */
    private HttpURLConnection setHeaderData(HttpURLConnection urlConnection) throws UnsupportedEncodingException {
        urlConnection.setRequestProperty("accept", "application/json");
        boolean flag = false;
        Log.v(TAG, "HEADER DATA >> accept : application/json");

        if (getHeaderMap() != null) {
            for (Map.Entry<String, String> entry : getHeaderMap().entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                Log.v(TAG, "HEADER DATA >> " + entry.getKey() + " :" + entry.getValue());
                if (entry.getKey().toLowerCase().equalsIgnoreCase("Content-Type"))
                    flag = true;
            }
        } else {
            Log.w(TAG, "NO HEADER DATA TO APPEND ||NO HEADER DATA TO APPEND ||NO HEADER DATA TO APPEND");
        }
        if (!flag) {
            urlConnection.setRequestProperty("Content-Type", "application/json");
            Log.v(TAG, "HEADER DATA >> Content-Type : application/json");
        }

        return urlConnection;
    }

    /**
     * Setting the request method to http stream.
     *
     * @param urlConnection
     * @param requestMethod
     * @return
     */
    private HttpURLConnection setRequestMethod(HttpURLConnection urlConnection, int requestMethod) {
        try {
            switch (requestMethod) {
                case GET_METHOD:
                    urlConnection.setRequestMethod("GET");
                    Log.v(TAG, "REQUEST METHOD : GET");
                    break;
                case POST_METHOD:
                    urlConnection.setRequestMethod("POST");
                    Log.v(TAG, "REQUEST METHOD : POST");
                    break;
                case PUT_METHOD:
                    urlConnection.setRequestMethod("PUT");
                    Log.v(TAG, "REQUEST METHOD : PUT");
                    break;
                case DELETE_METHOD:
                    urlConnection.setRequestMethod("DELETE");
                    Log.v(TAG, "REQUEST METHOD : DELETE");
                    break;
                case OPTIONS_METHOD:
                    urlConnection.setRequestMethod("OPTIONS");
                    Log.v(TAG, "REQUEST METHOD : OPTIONS");
                    break;
                case HEAD_METHOD:
                    urlConnection.setRequestMethod("HEAD");
                    Log.v(TAG, "REQUEST METHOD : HEAD");
                    break;
                case TRACE_METHOD:
                    urlConnection.setRequestMethod("TRACE");
                    Log.v(TAG, "REQUEST METHOD : TRACE");
                    break;
            }
        } catch (ProtocolException e) {
            Log.e(TAG, "CAN'T Set Request method\n\n " + e.getLocalizedMessage());
        }
        return urlConnection;
    }

    /**
     * Get the request type.
     *
     * @return
     */
    public int getRequestType() {
        return requestType;
    }

    /**
     * Set the request type.
     *
     * @param requestType
     */
    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    /**
     * Get the url.
     *
     * @return url
     * @throws MalformedURLException
     */
    private URL getUrl() throws MalformedURLException {
        Log.w(TAG, "URL : " + url);
        return new URL(url);
    }

    /**
     * Set the url.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlString() {
        return url;
    }

    /**
     * Get the hash map of header params.
     *
     * @return
     */
    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    /**
     * Set the header params  as hash map.
     *
     * @param headerMap
     */
    public void setHeaderMap(HashMap<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    private String getHeaderString() {
        String result = "n/a";
        if (headerMap == null || (headerMap != null && headerMap.size() <= 0))
            return result;
        result = "";
        for (Map.Entry<String, String> entry : getHeaderMap().entrySet()) {
            result = result + entry.getKey() + " :" + entry.getValue() + " \n ";
        }
        return result;
    }

    /**
     * Method to connect to server if not.
     *
     * @param urlConnection
     * @return true if already connected otherwise false.
     */
    public boolean isConnected(HttpURLConnection urlConnection) {
        try {
            urlConnection.connect();
        } catch (IOException e) {
            return true;
        }
        return false;
    }
}