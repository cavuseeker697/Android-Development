package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
                            implements AdapterView.OnItemSelectedListener,
                                    LoaderManager.LoaderCallbacks<String> {

    private static final String TRACE_TAG = "TRACE";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    String mSearchDept;
    String mSearchQuarter;
    String mSearchCourse;
    String mSearchItem;

    public static final String SEARCH_DEPT =
            "com.example.android.FinalProject.DEPT";
    public static final String SEARCH_QUARTER =
            "com.example.android.FinalProject.QUARTER";
    public static final String SEARCH_COURSE =
            "com.example.android.FinalProject.COURSE";
    public static final String SEARCH_ITEM =
            "com.example.android.FinalProject.ITEM";
    public static final String JSON_QUERY_RESULT =
            "com.example.android.FinalProject.JSONRESULTSTRING";

    public static final int NET_WAIT_INTERVAL = 500;

    public enum QueryTypes {QUARTERS,DEPARTMENTS,COURSES}

    private Spinner mSpinnerDepartment;
    private Spinner mSpinnerQuarter;
    private Spinner mSpinnerCourse;
    private Spinner mSpinnerItem;
    int currDeptPos = 0;
    int currQuarterPos = 0;
    int currCoursePos = 0;
    int currItemPos = 0;
    String currDeptName = "";
    String currQuarterName = "";
    String currCourseNbr = "";
    String currCourseTitle = "";
    String currItemLiteral = "";
    ArrayAdapter deptAdapter;
    ArrayAdapter quartersAdapter;
    ArrayAdapter courseAdapter;
    ArrayAdapter itemAdapter;
    TextView mStatusMessage;
    String inputQueryString;
    QueryTypes inputQueryType;
    String outputReturnedData;

    ClassEntryModel currentClassEntryModel;
    ClassSectionModel currentClassSectionModel;
    JSONObject mCurrentClassJSONObject;
    JSONObject mCurrentClassSectionJSONObject;
    private ArrayList mClassList = new ArrayList<ClassEntryModel>();

    ArrayList<String> quarterNames = new ArrayList<String>();

    // These two arrays are paird. Deptstrings concatenates the 4-6 digit department name
    // and the department description -  ACCT Accounting
    // deptNames is just the 4-6 digit codes. The strings will be used in the spinner
    // and the Names will be used for Subsequent searchs
    ArrayList<String> deptStrings = new ArrayList<String>();
    ArrayList<String> deptNames = new ArrayList<String>();

    ArrayList<String> sectionCodes = new ArrayList<String>();
    ArrayList<String> itemNbrs = new ArrayList<String>();
    ArrayList<String> itemLiterals = new ArrayList<String>();

    // these two arrays will be in pairs. One containing the course nbrs, pair with currDeptName
    // and the other the course titles, which will be in the spinner on the main page
    // When one of the courses is selected, the corresponding number will be entered into currCourseNbr;
    ArrayList<String> courseNbrs = new ArrayList<String>();
    ArrayList<String> courseTitles =  new ArrayList<String>();

    boolean initializing = true;
    boolean initSpinnerOnClicks = true;
    boolean retrievingSingleDepartment = false;
    boolean initializingSpinners = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinnerDepartment = findViewById(R.id.spinnerDepartment);
        mSpinnerDepartment.setVisibility(View.INVISIBLE);
        mSpinnerQuarter = findViewById(R.id.spinnerQuarter);
        mSpinnerQuarter.setVisibility(View.INVISIBLE);
        mSpinnerCourse = findViewById(R.id.spinnerCourse);
        mSpinnerCourse.setVisibility(View.INVISIBLE);
        mSpinnerItem = findViewById(R.id.spinnerItem);
        mSpinnerItem.setVisibility(View.INVISIBLE);
        mStatusMessage = findViewById(R.id.statusMessage);
        mStatusMessage.setVisibility(View.VISIBLE);
        mStatusMessage.setText("Loading Initial Data...");

        // will further call searchDepts to initialize data for Departments spinner
        retrieveQuarters();

        mSpinnerDepartment.setOnItemSelectedListener(this);
        mSpinnerQuarter.setOnItemSelectedListener(this);
        mSpinnerCourse.setOnItemSelectedListener(this);
        mSpinnerItem.setOnItemSelectedListener(this);
        initSpinnerOnClicks = false;
    }
    // Initialize all the spinners from here.  The data arrays will be changed externally. This is
    // really only called when the system is initailizing or when the "reset" button is pressed
    // Note that the new ArrayAdapter calls causes the onItemSelected listener to be activated
    private void reInitializeSpinners() {
        initializingSpinners = true;
        deptAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                deptStrings);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDepartment.setAdapter(deptAdapter);
        currDeptName = deptStrings.get(currDeptPos);

        quartersAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                quarterNames);
        quartersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQuarter.setAdapter(quartersAdapter);
        currQuarterName = quarterNames.get(currQuarterPos);

        courseAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                courseTitles);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourse.setAdapter(courseAdapter);
        currCourseNbr = courseNbrs.get(currCoursePos);
        currCourseTitle = courseTitles.get(currCoursePos);

        String[] itemLiterals = getResources().getStringArray(R.array.items_array);
        itemAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                itemLiterals);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerItem.setAdapter(itemAdapter);
        currItemLiteral = itemLiterals[currItemPos];

        mSpinnerDepartment.setVisibility(View.VISIBLE);
        mSpinnerCourse.setVisibility(View.VISIBLE);
        mSpinnerQuarter.setVisibility(View.VISIBLE);
        mSpinnerItem.setVisibility(View.VISIBLE);
        initializingSpinners = false;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TRACE_TAG,"Entered onItemSelected, position = " + position + ",parent.getId() = " + parent.getId());
        if (parent.getId() == R.id.spinnerDepartment) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();
            currDeptPos = position;
            currDeptName = deptNames.get(position);
            // If we are selecting an actual department but not initializing all the spinners, this gets called
            if (!initializingSpinners) {
                retrieveCoursesForDept();
            }
        } else if (parent.getId() == R.id.spinnerQuarter) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();
            currQuarterPos = position;
            currQuarterName = valueFromSpinner;
        }
        if (parent.getId() == R.id.spinnerCourse) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();
            currCoursePos = position;
            currCourseNbr = courseNbrs.get(position);
            currCourseTitle = courseTitles.get(position);
            if (currCoursePos > 0) // will be zero when still being initalized.  "ALL"
                reloadItemSpinner();
        } else if (parent.getId() == R.id.spinnerItem) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();
            currItemPos = position;
            currItemLiteral = valueFromSpinner;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TRACE_TAG,"Entered onNothingSelected");
    }
    // Reset button pressed
    public void resetToStart(View view) {
        Log.d(TRACE_TAG,"Entered resetToStart");
        initializing = true;
        courseTitles.clear();
        courseTitles.add("ANY");
        currCoursePos = 0;
        itemLiterals.clear();
        itemLiterals.add("ANY");
        currItemPos = 0;
        reInitializeSpinners();
        initializing = false;
    }
    // Retrieves all the quarters listed in the data base. Needed for all the queries
    void retrieveQuarters() {
        inputQueryString = "quarters.json";
        inputQueryType = QueryTypes.QUARTERS;
        doWhidbeyWebSearch(inputQueryString,"","","","");
        Log.d(TRACE_TAG, "After searchQuarters, outputReturnData = " + outputReturnedData);
     }
     // will retrieve all the departments to initialize the dept spinner
    void retrieveDepartments() {
        inputQueryString = "departments.json";
        inputQueryType = QueryTypes.DEPARTMENTS;
        doWhidbeyWebSearch(inputQueryString,"","","","");
        Log.d(TRACE_TAG, "After searchDepartments, outputReturnData = " + outputReturnedData);
    }
    // retrieves all the courses for a single department to initialize the courses spinner
    void retrieveCoursesForDept () {
        inputQueryString = "courses.json";
        inputQueryType = QueryTypes.COURSES;
        retrievingSingleDepartment = true;
        doWhidbeyWebSearch(inputQueryString,currQuarterName,currDeptName,"","");
    }
    // called when a new course is selected from the course spinner. Old items are cleared and new ones
    // loaded
    void reloadItemSpinner() {
        sectionCodes.clear();
        itemNbrs.clear();
        itemLiterals.clear();
        itemLiterals.add("Any");
        itemNbrs.add("");
        sectionCodes.add("");
        currentClassEntryModel = (ClassEntryModel) mClassList.get(currCoursePos-1); // -1 to account for ANY at entry 0
        for (int i=0;i < currentClassEntryModel.getNbrSections(); i++) {
            currentClassSectionModel = currentClassEntryModel.getSectionByIndex(i);
            sectionCodes.add(currentClassSectionModel.getSectionCode());
            itemNbrs.add(currentClassSectionModel.getItemNbr());
            itemLiterals.add(currentClassSectionModel.getInstructor().trim() + " " +
                             currentClassSectionModel.getDays());
        }
        itemAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                itemLiterals);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerItem.setAdapter(itemAdapter);
        currItemPos = 0;
    }
    // This routine will parse the arraylist ClassList selecting only the classes chosen. We will look at the
    // current value of the spinners to do this selection. Then we create a new classlist that will be passed
    // to the next activity
    public void searchClasses(View view) {
        ArrayList localClassList = new ArrayList<ClassEntryModel>();
        Log.d(TRACE_TAG, "Entered searchClasses");
        for (int i=0;i < mClassList.size(); i++) {
            int j=0;
            if ((currCoursePos == 0) || ((currCoursePos-1) == i)) { // == 0 implies "ALL"
                currentClassEntryModel = new ClassEntryModel ((ClassEntryModel) mClassList.get(i));
                if (currCoursePos > 0) {                        // only one class selected, so we check the sections
                    if (currItemPos > 0) {                      // if == 0, then all sections go, which are already present
                        currentClassEntryModel.clearSections();
                        currentClassEntryModel.addSection(((ClassEntryModel) mClassList.get(i)).getSectionByIndex(currItemPos - 1));
                    }
                } // if currCoursePos == 0, then all courses and sections to be transferred
                localClassList.add(currentClassEntryModel);
            }
        }
        Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
        Log.d(TRACE_TAG, "Putting Extra mLocalClassList");
        intent.putExtra("Class_List_Array",localClassList);
        startActivity(intent);
    }
    // region - Network search initiation and main JSON string processing
    //

    private void doWhidbeyWebSearch(String queryString,
                                    String inQuarterName,
                                    String inDeptName,
                                    String inCourseNbr,
                                    String inItemNbr) {
        Log.d(TRACE_TAG,"Enter doWhidbeyWebSearch, input Query String = " + queryString);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()
                && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            queryBundle.putString("inQuarterName",inQuarterName);
            queryBundle.putString("inDeptName",inDeptName);
            queryBundle.putString("inCourseNbr",inCourseNbr);
            queryBundle.putString("inItemNbr",inItemNbr);
            LoaderManager.getInstance(this).restartLoader(0,queryBundle, this);
        } else {
            if (queryString.length() == 0) {
                Log.d(TRACE_TAG, "Network Query returned 0 length");
            } else {
                Log.d(TRACE_TAG, "Network Query returned No Network");
            }
        }

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        String inQuarterName = "";
        String inDeptName = "";
        String inCourseNbr = "";
        String inItemNbr = "";

        if (args != null) {
            queryString = args.getString("queryString");
            inQuarterName = args.getString("inQuarterName");
            inDeptName = args.getString("inDeptName");
            inCourseNbr = args.getString("inCourseNbr");
            inItemNbr = args.getString("inItemNbr");
        }

        return new CourseLoader(this, queryString,inQuarterName,
                                            inDeptName,inCourseNbr,inItemNbr);
    }
    // endregion
    // region OnLoadFinished - JSON processing of the string returned from the Query
    //
    // Much of the core logic in the app is here, especially that that is called during initialization
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        JSONObject currentJSONObject;

//        try {
            if (data != null && data.length() != 0) {
                Log.d(TRACE_TAG, "onLoadFinished, data.length =" + data.length());
                Log.d(TRACE_TAG, data);

            } else {
                Log.d(TRACE_TAG, "NO DATA RETURNED");
                return;
            }
            switch (inputQueryType) {
                case QUARTERS:
                    try {
                        JSONArray quartersArray = new JSONArray(data);
                        int len = quartersArray.length();
                        for (int i = 0; i < len; i++) {
                            currentJSONObject = quartersArray.getJSONObject(i);
                            quarterNames.add(currentJSONObject.getString("quarterName"));
                            Log.d(TRACE_TAG, "In onLoadFinished, quarterNames[" + i + "] = " + quarterNames.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (initializing) {
                        currQuarterName = quarterNames.get(0);
                        currQuarterPos = 0;
                        retrieveDepartments(); // Will end up back here with DEPARTMENTS set
                    }
                    ;
                    break; // of case QUARTERS
                case DEPARTMENTS:
                    try {
                        JSONArray departmentsArray = new JSONArray(data);
                        for (int i = 0; i < departmentsArray.length(); i++) {
                            currentJSONObject = departmentsArray.getJSONObject(i);
                            deptStrings.add(currentJSONObject.getString("department") + "  " +
                                    currentJSONObject.getString("deptDescription"));
                            deptNames.add(currentJSONObject.getString("department"));
                            Log.d(TRACE_TAG, "In onLoadFinished, deptNames[" + i + "] = " + deptStrings.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (initializing) {
                        currDeptName = deptNames.get(0);
                        currDeptPos = 0;
                        retrieveCoursesForDept();
                        mStatusMessage.setText("");
                    }
                    ;
                    break; // of case DEPARTMENTS
                case COURSES:
                    try {
                        mClassList.clear();
                        courseTitles.clear();
                        courseNbrs.clear();
                        courseTitles.add("Any");
                        courseNbrs.add("");
                        JSONArray coursesArray = new JSONArray(data);
                        Log.d(TRACE_TAG, "in OLF, Parsing JSON, # of items = " + coursesArray.length());
                        int len = coursesArray.length();
                        // For now, just get the first one
                        if (len > 0) {
                            for (int i = 0; i<len; i++) {  // Will loop through to create recycler view
                                currentJSONObject = coursesArray.getJSONObject(i);
                                try {
                                    mSearchQuarter = currentJSONObject.getString("quarterName");
                                    JSONArray sectionsArray = currentJSONObject.getJSONArray("courseSections");
                                    currentClassEntryModel =
                                            new ClassEntryModel(currentJSONObject.getString("department"),
                                                                currentJSONObject.getString("courseNbr"),
                                                                currentJSONObject.getString("courseTitle"),
                                                                currentJSONObject.getString("courseDescription"),
                                                                currentJSONObject.getString("credits"),
                                                                currentJSONObject.getString("preCoRequisites"));
                                    courseTitles.add(currentJSONObject.getString("courseTitle"));
                                    courseNbrs.add(currentJSONObject.getString("courseNbr"));
                                    for (int j = 0; j<sectionsArray.length();j++) {
                                        mCurrentClassSectionJSONObject = sectionsArray.getJSONObject(j);
                                        currentClassSectionModel= new ClassSectionModel(
                                                mCurrentClassSectionJSONObject.getString("sectionCode"),
                                                mCurrentClassSectionJSONObject.getString("itemNbr"),
                                                mCurrentClassSectionJSONObject.getString("days"),
                                                mCurrentClassSectionJSONObject.getString("timeLocation"),
                                                mCurrentClassSectionJSONObject.getString("instructor"),
                                                mCurrentClassSectionJSONObject.getString("labFee"));
                                        currentClassEntryModel.addSection(currentClassSectionModel);
                                    }
                                    mClassList.add(currentClassEntryModel);
                                } catch (JSONException e) {
                                    Log.d(TRACE_TAG, "JSON Exception #2 - First entry" + e.getMessage());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.d(TRACE_TAG, "JSON Exception #1 - Outer loop " + e.getMessage());
                    }

                    if (initializing) {     // done initializing, we will now be able to get on with letting the user choose
                        initializing = false;
                        currCourseTitle = courseTitles.get(0);
                        currCourseNbr = courseNbrs.get(0);
                        currCoursePos = 0;
                        reInitializeSpinners();
                        mStatusMessage.setText("");
                        break;
                    } else if (retrievingSingleDepartment) { // if we are here and not initializing, then we are doing a single department
                        retrievingSingleDepartment = false;
                        currCourseTitle = courseTitles.get(0);
                        currCourseNbr = courseNbrs.get(0);
                        currCoursePos = 0;
//                        reInitializeSpinners();
                        courseAdapter = new ArrayAdapter(this,
                                android.R.layout.simple_spinner_item,
                                courseTitles);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerCourse.setAdapter(courseAdapter);
                        currCourseNbr = courseNbrs.get(currCoursePos);
                        currCourseTitle = courseTitles.get(currCoursePos);
                    }

            } // switch
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d(TRACE_TAG, "In OnLoaderReset");
    }

}