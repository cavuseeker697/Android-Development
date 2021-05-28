package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.finalproject.MainActivity.JSON_QUERY_RESULT;
// Implements 2nd Activity listing the courses selected for consideration
//
public class ClassListActivity extends AppCompatActivity
                                implements ItemAdapter.ListItemClickListener {

    private static final String TRACE_TAG = "TRACE";
    private static final String LOG_TAG = ClassListActivity.class.getSimpleName();

    TextView mClassTitle;
    String mSearchDept;
    String mSearchQuarter;
    String mSearchCourseNbr;
    String mSearchItem;
    String mSearchTitle;
    String mSearchDesc;
    String mSearchCredits;
    String mSearchPreCoRequisites;
    JSONObject mCurrentClassJSONObject;
    JSONObject mCurrentClassSectionJSONObject;
    RecyclerView mRecyclerView;
    ItemAdapter mAdapter;

    private ArrayList mClassList = new ArrayList<ClassEntryModel>();

    ClassEntryModel currentClassEntryModel;
    ClassSectionModel currentClassSectionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ListActivity);
        setContentView(R.layout.activity_recycler_view);

        mRecyclerView = findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        mClassList = (ArrayList<ClassEntryModel>) intent.getSerializableExtra("Class_List_Array");
        Log.d(TRACE_TAG, "In OnCreate Class List, mClassList.size = " + mClassList.size());


        mAdapter = new ItemAdapter(this,mClassList,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    // when a course is selected, put the current Class Object and start the Detail Activity
    public void onListItemClick (int clickedItemIndex) {

        Intent intent = new Intent(ClassListActivity.this, ClassDetailPage.class);
        intent.putExtra("Current_Class_Object", (Serializable) mClassList.get(clickedItemIndex));
        Log.d(TRACE_TAG, "Putting Extra mClassList(" + clickedItemIndex + ")");
        startActivity(intent);

    }
}
