package com.osec.fido2test.ui.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.osec.fido2test.MyApplication;


public class RecyclerViewSpacesItem extends RecyclerView.ItemDecoration {

    private final int mTopSpace;

    public RecyclerViewSpacesItem(int dpSpace) {
        this.mTopSpace = dip2px(dpSpace);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = mTopSpace;
    }

    private int dip2px(int dipValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
