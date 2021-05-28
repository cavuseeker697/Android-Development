package com.example.finalproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Network;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// region - Format query URL and retrieve json raw string to be returned
//
public class NetworkUtils {
    private static final String TRACE_TAG = "TRACE";
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String COURSE_BASE_URL = "whidbey.lwtech-csd297.com";
    private static final String QUERY_PARAM = "q";


    static String getCourseInfo(String queryString,
                                String inQuarterName,
                                String inDeptName,
                                String inCourseNbr,
                                String inItemNbr) {
        Log.d(TRACE_TAG,"Enter getCourseInfo");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String courseJSONString = null;
        // query to return JSON data does not follow normal servlet GET formats
        // queryString contains "courses.json" etc.
        try {
            Uri.Builder buildURI = new Uri.Builder();
            buildURI.scheme("https").authority(COURSE_BASE_URL)
                    .appendPath(queryString);
            if (inQuarterName.length() > 0) {
                buildURI.appendQueryParameter("quarter",inQuarterName);
            }
            if (inDeptName.length() > 0) {
                buildURI.appendQueryParameter("department",inDeptName);
            }
            if (inCourseNbr.length() > 0) {
                buildURI.appendQueryParameter("course",inCourseNbr);
            }
            if (inItemNbr.length() > 0) {
                buildURI.appendQueryParameter("item",inItemNbr);
            }

            URL requestURL = new URL(buildURI.toString());
            Log.d(TRACE_TAG, "In GetCourseInfo, URL = " + buildURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                //Log.d(LOG_TAG, line);
                builder.append("\n");
            }
            Log.d(LOG_TAG, "builder.length() = " + builder.length());
            if (builder.length() == 0) {
                return null;
            }

            courseJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if (courseJSONString != null && courseJSONString.length() != 0) {
            Log.d(TRACE_TAG, "Exiting getCourseInfo, courseJSONString.length() = " + courseJSONString.length());
            return courseJSONString;
        } else {
            Log.d(TRACE_TAG, "bookJSONString is null or 0 length");
            return null;
        }

    }
}
