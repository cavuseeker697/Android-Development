package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
// implements the 3rd Activity page, for a single class. Displays course #, title, etc. and then
// a scrollview of the various sections
//
public class ClassDetailPage extends AppCompatActivity {

    private static final String TRACE_TAG = "TRACE";
    private static final String LOG_TAG = ClassDetailPage.class.getSimpleName();

    TextView mCourseNumber;
    TextView mCourseTitle;
    TextView mCourseDesc;
    TextView mCourseCredits;
    TextView mPreCoRequisites;
    TextView mItemTitle;

    private static final int MAX_SECTION_TEXT_VIEWS = 8; // 0-7
    TextView[] mSectionTextViews = new TextView[MAX_SECTION_TEXT_VIEWS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DetailActivity);
        setContentView(R.layout.activity_class_detail_listing);

        // courseNumber is the concatentation of courseDept and courseNbr.
        // if Dept < 4 characters, pad with spaces
        mCourseNumber = findViewById(R.id.courseNumber);
        mCourseTitle = findViewById(R.id.courseTitle);
        mCourseDesc = findViewById(R.id.courseDesc);
        mCourseCredits = findViewById(R.id.courseCredits);
        mPreCoRequisites = findViewById(R.id.preCoRequisites);
        mItemTitle = findViewById(R.id.itemTitle);

        for (int i=0;i < MAX_SECTION_TEXT_VIEWS; i++) {
            String scrollViewName = "scrollText" + i;
            mSectionTextViews[i] = (TextView) findViewById(getResources().getIdentifier(scrollViewName,"id",getPackageName()));
        }
        String courseDepartment = "";
        String courseNbr = "";
        String courseNumber = "";
        String courseTitle = "";
        String courseCredits = "";
        String courseDesc = "";
        String coursePreCoRequisites = "";

        Intent intent = getIntent();

        ClassEntryModel mCurrentClassEntry = (ClassEntryModel) intent.getSerializableExtra("Current_Class_Object");
        courseDepartment = mCurrentClassEntry.getClassDept();
        courseNbr = mCurrentClassEntry.getClassCourseNbr();
        courseTitle = mCurrentClassEntry.getClassTitle();
        courseCredits = mCurrentClassEntry.getClassCredits();
        courseDesc = mCurrentClassEntry.getClassDesc();
        coursePreCoRequisites = mCurrentClassEntry.getPreCoRequisites();

        courseNumber = courseDepartment + courseNbr;
        mCourseNumber.setText(courseNumber);
        mCourseTitle.setText(courseTitle);
        mCourseDesc.setText(courseDesc);
        mCourseCredits.setText(courseCredits + " Credits");
        mPreCoRequisites.setText(coursePreCoRequisites);
        ClassSectionModel mCurrentSectionEntry;
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        String blanks = "    "; // currently 4

        // using SpannableStringBuilder to build string for each of the scrollviews lines.
        // imbed HTML <b> to bold the item names
        //
        for (int i = 0; i < mCurrentClassEntry.getNbrSections(); i++) {
            mCurrentSectionEntry = mCurrentClassEntry.getSectionByIndex(i);

            SpannableStringBuilder scrollLine = new SpannableStringBuilder();
            scrollLine.append(Html.fromHtml("<b>ITEM:<b> "));
            scrollLine.append(mCurrentSectionEntry.getItemNbr().trim());
            scrollLine.append(blanks);

            scrollLine.append(Html.fromHtml("<b>SECTION:<b> "));
            scrollLine.append(mCurrentSectionEntry.getSectionCode().trim());
            scrollLine.append(blanks);

            scrollLine.append(Html.fromHtml("<b>INSTRUCTOR:<b> "));
            scrollLine.append(mCurrentSectionEntry.getInstructor().trim());

            scrollLine.append("\n");

            scrollLine.append(Html.fromHtml("<b>DAYS:<b> "));
            scrollLine.append(mCurrentSectionEntry.getDays().trim());
            scrollLine.append(blanks);

            scrollLine.append(Html.fromHtml("<b>TIME/LOC:<b> "));
            scrollLine.append(mCurrentSectionEntry.getTimeLocation().trim());

            mSectionTextViews[i].setText(scrollLine);
            mSectionTextViews[i].setVisibility(View.VISIBLE);
        }
    }
}