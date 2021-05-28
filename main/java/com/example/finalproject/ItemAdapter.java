package com.example.finalproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// region ItemAdapter code for the Recycler View
//
public class ItemAdapter extends
        RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private static final String TRACE_TAG = "TRACE";
    private static final String LOG_TAG = ItemAdapter.class.getSimpleName();
    private List mClassList = new ArrayList<ClassEntryModel>();
    private Context mContext;
    final private ListItemClickListener mOnClickListener;
    private RecyclerView mRecyclerView;

    private LayoutInflater mInflater;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public ItemAdapter(Context context,
                       ArrayList<ClassEntryModel> classList,
                       ListItemClickListener listener) {
        mContext = context;
        Log.d(TRACE_TAG, "Entered ClassListAdapter classList.size = " + classList.size());
        mInflater = LayoutInflater.from(context);
        mOnClickListener = listener;

        this.mClassList = classList;

    }

    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView wordClassTitleView;
        final ItemAdapter mAdapter;


        public ItemViewHolder(View itemView, ItemAdapter adapter) {

            super(itemView);
            Log.d(TRACE_TAG, "Enter ItemViewHolder");
            wordClassTitleView = itemView.findViewById(R.id.classTitle);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TRACE_TAG, "Enter ItemViewHolder.OnClick");
            int mPosition = getLayoutPosition();
            Log.d(TRACE_TAG, "Calling MainActivity.onListItemClick, position = " + mPosition);
            mOnClickListener.onListItemClick(mPosition);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.activity_class_list, parent, false);
        return new ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d(TRACE_TAG, "Entered onBindViewHolder, position = "+ position);
        ClassEntryModel mCurrent = (ClassEntryModel) mClassList.get(position);
        String classTitleEntry;
        classTitleEntry = mCurrent.getClassDept() + mCurrent.getClassCourseNbr() +
                " " + mCurrent.getClassTitle();
        Log.d(TRACE_TAG, "Setting to string " + classTitleEntry);
        holder.wordClassTitleView.setText(classTitleEntry);

    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }
}
