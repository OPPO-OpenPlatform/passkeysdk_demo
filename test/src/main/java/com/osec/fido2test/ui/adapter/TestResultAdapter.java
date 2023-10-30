package com.osec.fido2test.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omes.fido2test.R;
import com.osec.fido2test.entity.TestResponse;

import java.util.ArrayList;
import java.util.List;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.ViewHolder> {
    private static final String TAG = "TestResultAdapter";
    private static final int MAX_LINES = 4;

    private final Context mContext;

    private List<TestResponse> mListData;

    public TestResultAdapter(Context context) {
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        if (getItemCount() == 0) {
            return;
        }
        mListData.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(TestResponse response) {
        if (mListData == null) {
            mListData = new ArrayList<>();
        }
        mListData.add(response);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_test_result, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TestResponse response = mListData.get(position);
        viewHolder.tvResponseContent.setText(response.resultStr);
        if (response.success) {
            viewHolder.ivStatus.setImageResource(R.mipmap.result_success);
            viewHolder.ivArrow.setVisibility(View.VISIBLE);
            viewHolder.ivArrow.setTag(false);
            upOrDownContent(viewHolder);
        } else {
            viewHolder.ivStatus.setImageResource(R.mipmap.result_fail);
            viewHolder.ivArrow.setVisibility(View.GONE);
            viewHolder.tvResponseContent.setMaxLines(Integer.MAX_VALUE);
        }
        viewHolder.tvResponseContent.setOnClickListener(view -> {
            upOrDownContent(viewHolder);
        });
        viewHolder.ivArrow.setOnClickListener(v -> {
            upOrDownContent(viewHolder);
        });
    }

    private void upOrDownContent(ViewHolder viewHolder) {
        if (viewHolder.ivArrow.getVisibility() == View.GONE) {
            return;
        }
        if (viewHolder.ivArrow.getTag() == null || (boolean) viewHolder.ivArrow.getTag()) {
            viewHolder.ivArrow.setImageResource(R.mipmap.arrow_up);
            viewHolder.ivArrow.setTag(false);
            viewHolder.tvResponseContent.setMaxLines(Integer.MAX_VALUE);
        } else {
            viewHolder.ivArrow.setImageResource(R.mipmap.arrow_down);
            viewHolder.ivArrow.setTag(true);
            viewHolder.tvResponseContent.setMaxLines(MAX_LINES);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivStatus;
        protected ImageView ivArrow;
        protected TextView tvResponseContent;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivStatus = view.findViewById(R.id.iv_status);
            ivArrow = view.findViewById(R.id.iv_arrow);
            tvResponseContent = view.findViewById(R.id.tv_response_content);
        }
    }
}
