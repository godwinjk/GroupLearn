package com.grouplearn.project.cloud;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.grouplearn.project.utilities.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by WiSilica on 11-12-2016 17:31 for GroupLearn application.
 */

public class ThumbNailLoader extends AsyncTask<String, Void, String> {
    public static final int GET_METHOD = 0;

    private static final String TAG = "WiseConnectHttpMethod";
    final String timeOutException = "TIMED_OUT";
    private final String BAD_REQUEST = "Response Code : ";

    private Context mContext;
    private String url = "http://stackoverflow.com/";
    private int TIME_OUT = 60 * 1000;

    private ThumbNailLoaderCallback mCallback;

    public ThumbNailLoader(Context mContext, String url, ThumbNailLoaderCallback callback) {
        this.mContext = mContext;
        this.mCallback = callback;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
//        this.url = params[0];
        String response = "";
        try {
            if (!TextUtils.isEmpty(getUrl().toString())) {

                URL url = getUrl();
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection = setRequestMethod(urlConnection, GET_METHOD);

                urlConnection.setConnectTimeout(TIME_OUT);
                urlConnection.setReadTimeout(TIME_OUT);

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

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
//                Document doc = Jsoup.connect("http://stackoverflow.com/").get();
                return response;

            } else {
                Log.e(TAG, "INVALID URL ||INVALID URL ||INVALID URL ||INVALID URL ");
                if (mCallback != null)
                    mCallback.onThumbNailLoadingFailed();
                return null;
            }
        } catch (ProtocolException e) {
            if (mCallback != null)
                mCallback.onThumbNailLoadingFailed();
            e.printStackTrace();
        } catch (IOException e) {
            if (mCallback != null)
                mCallback.onThumbNailLoadingFailed();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            if (mCallback != null)
                mCallback.onThumbNailLoadingFailed();
            Log.e(TAG, "ALREADY CONNECTED");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String doc) {
        if (TextUtils.isEmpty(doc)) {
            return;
        }
        Document document = Jsoup.parse(doc);
        Elements elements = document.select("meta");
        String title = document.title();

        for (Element e : elements) {
            //fetch image url from content attribute of meta tag.
            String imageUrl = e.attr("content");

            //OR more specifically you can check meta property.
            if (e.attr("property").equalsIgnoreCase("og:image")) {
                imageUrl = e.attr("content");
                Uri uri = Uri.parse(imageUrl);
                if (mCallback != null)
                    mCallback.onThumbNailLoaded(title, uri);
                break;
            }
        }
    }

    public Context getContext() {
        return mContext;
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
                    urlConnection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
                    Log.v(TAG, "REQUEST METHOD : GET");
                    break;
            }
        } catch (ProtocolException e) {
            Log.e(TAG, "CAN'T Set Request method\n\n " + e.getLocalizedMessage());
        }
        return urlConnection;
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