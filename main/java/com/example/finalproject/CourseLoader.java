package com.example.finalproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

// region  CourseLoader - AsyncTaskLoader override methods
//
public class CourseLoader extends AsyncTaskLoader<String> {
    private static final String TRACE_TAG = "TRACE";
    private static final String LOG_TAG = CourseLoader.class.getSimpleName();
    private String mQueryString;
    private String mDeptName;
    private String mQuarterName;
    private String mCourseNbr;
    private String mItemNbr;

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG,"Enter onStartLoading");
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Log.d(LOG_TAG,"Enter loadInBackground, calling geCourseInfo");
        String retStr = NetworkUtils.getCourseInfo(mQueryString,mQuarterName,mDeptName,
                                                mCourseNbr,mItemNbr);
        Log.d(TRACE_TAG, "Return from getCourseInfo");
        return retStr;
    }


    public CourseLoader(@NonNull Context context, String queryString,
                        String inQuarterName,
                        String inDeptName,
                        String inCourseNbr,
                        String inItemNbr) {
        super(context);
        Log.d(LOG_TAG, "Query String entered = " + queryString);
        mQueryString = queryString;
        mQuarterName = inQuarterName;
        mDeptName = inDeptName;
        mCourseNbr = inCourseNbr;
        mItemNbr = inItemNbr;

    }
}
// endregion
