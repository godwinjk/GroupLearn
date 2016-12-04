package com.grouplearn.project.app.uiManagement.search.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.search.GoogleSearchCallback;
import com.grouplearn.project.models.GoogleSearchResult;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleSearchFragment extends BaseFragment {

    private static final String TAG = "GoogleSearchFragment";
    SearchView searchAutoComplete;

    ListView lvGoogleSearchResult;
    private ArrayList<String> mSearchResults = new ArrayList<>();
    ArrayAdapter<String> mAdapter;
    ArrayList<GoogleSearchResult> googleSearchResults = new ArrayList<>();

    public GoogleSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeWidgets(view);
        registerListeners();
    }

    @Override
    protected void initializeWidgets(View v) {
        searchAutoComplete = (SearchView) v.findViewById(R.id.sv_google);
        lvGoogleSearchResult = (ListView) v.findViewById(R.id.lv_google_results);

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, mSearchResults);
        lvGoogleSearchResult.setAdapter(mAdapter);
    }

    @Override
    protected void registerListeners() {
        lvGoogleSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoogleSearchResult result = googleSearchResults.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getResultLink()));
                startActivity(browserIntent);
            }
        });
        searchAutoComplete.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DisplayInfo.showLoader(getActivity(), "Please wait. Getting results from web");
                            sendGet(query, new GoogleSearchCallback() {
                                @Override
                                public void onGetGoogleResultSuccess(final ArrayList<GoogleSearchResult> googleSearchResults) {
                                    GoogleSearchFragment.this.googleSearchResults = googleSearchResults;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSearchResults.clear();
                                            DisplayInfo.dismissLoader(getActivity());

                                            for (GoogleSearchResult result : googleSearchResults) {
                                                mSearchResults.add(result.getResultTitle());
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onGetGoogleResultFailed(AppError error) {
                                    DisplayInfo.dismissLoader(getActivity());
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    // HTTP GET request
    private void sendGet(String query, GoogleSearchCallback googleSearchCallback) throws Exception {
        ArrayList<GoogleSearchResult> googleSearchResults = new ArrayList<>();
        String searchQuery = query.replace(" ", "%20");
        String[] searchTags = query.split(" ");
        String searchTagString = query;
        for (int i = 1; searchTags != null && i < searchTags.length - 1; i++) {
            searchTagString = searchTagString + searchTags[i] + "+";
        }
        searchTagString = searchTags[searchTags.length - 1];
        String url = "https://www.google.co.in/search?q=" + searchQuery + "&#q=" + searchTagString + "+tutorial+course";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println(response.toString());
        Document document = Jsoup.parse(response.toString());
        Elements links = document.select("div.g");
        for (Element link : links) {
            Document resultDoc = Jsoup.parse(link.html());
            Elements resultLinks = resultDoc.select("a[href]");
            for (Element resultLink : resultLinks) {
                String temp = resultLink.attr("href");
                if (temp.startsWith("/url?q=")) {
                    Log.i(TAG, "" + temp.replace("/url?q=", "") + "");

                    URL tempUrl = new URL(temp.replace("/url?q=", ""));
                    String path = tempUrl.getFile().substring(0, tempUrl.getFile().lastIndexOf('/'));
                    String base = tempUrl.getProtocol() + "://" + tempUrl.getHost() + path;
                    Log.i(TAG, "" + base);
                    Log.e(TAG, getDomainName(temp));
                    if (!resultLink.text().equalsIgnoreCase("Cached")) {
                        GoogleSearchResult googleSearchResult = new GoogleSearchResult();
                        googleSearchResult.setResultLink(base);
                        googleSearchResult.setResultTitle(resultLink.text());
                        googleSearchResults.add(googleSearchResult);
                    }
                }
            }
        }
        googleSearchCallback.onGetGoogleResultSuccess(googleSearchResults);
    }

    private static Pattern patternDomainName;
    private Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN = "^(http(s)?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    static {
        patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
    }

    private String getDomainName(String url) {

        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;

    }
}
