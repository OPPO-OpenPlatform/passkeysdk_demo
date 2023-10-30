package com.osec.fido2test.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.util.Log;
import androidx.fragment.app.Fragment;

public class BaseTestFragment extends Fragment {
    private static final String TAG = "BaseTestFragment";

    protected AlertDialog mDialog;

    protected void showLoading() {
        Log.d(TAG, "showLoading");
        dismissDialog();
        mDialog = new ProgressDialog(requireActivity());
        mDialog.setTitle("Loading");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    protected void showErrorDialog(String title, String content) {
        Log.d(TAG, "showErrorDialog: title=" + title + ",content=" + content);
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dismissDialog();
        });
        mDialog = builder.show();
    }

    protected void dismissDialog() {
        Log.d(TAG, "dismissDialog");
        if (mDialog == null) return;
        mDialog.dismiss();
        mDialog = null;
    }

    /**
     * 当前Fragment进入可见状态
     */
    public void onVisible() {
    }
}
