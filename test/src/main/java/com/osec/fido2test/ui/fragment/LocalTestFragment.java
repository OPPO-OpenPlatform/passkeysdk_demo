package com.osec.fido2test.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omes.fido2test.R;
import com.omes.fido2test.databinding.FragmentLocalBinding;
import com.osec.fido2test.entity.LocalMessage;
import com.osec.fido2test.entity.TestResponse;
import com.osec.fido2test.global.LocalMessageManager;
import com.osec.fido2test.test.local.BaseLocalCase;
import com.osec.fido2test.test.local.LocalAuth;
import com.osec.fido2test.test.local.LocalCapacity;
import com.osec.fido2test.test.local.LocalReg;
import com.osec.fido2test.test.local.TestCaseListener;
import com.osec.fido2test.ui.adapter.RecyclerViewSpacesItem;
import com.osec.fido2test.ui.adapter.TestResultAdapter;
import com.osec.fido2test.utils.JsonUtil;

import java.util.List;

@SuppressLint("NonConstantResourceId")
public class LocalTestFragment extends BaseTestFragment implements TestCaseListener {
    private static final String TAG = "LocalTestFragment";
    private ArrayAdapter<LocalMessage> mCaseAdapter;
    private TestResultAdapter mResultAdapter;
    private BaseLocalCase mTestCase;
    private FragmentLocalBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mBinding = FragmentLocalBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        mBinding = null;
        dismissDialog();
    }

    private void initData() {
        mBinding.btnStartTest.setOnClickListener(view -> startTest());

        mBinding.rvResult.addItemDecoration(new RecyclerViewSpacesItem(10));
        mBinding.rvResult.setLayoutManager(new LinearLayoutManager(getContext()));
        mResultAdapter = new TestResultAdapter(getContext());
        mBinding.rvResult.setAdapter(mResultAdapter);

        List<LocalMessage> messageList = LocalMessageManager.getInstance().getMessageList();
        mCaseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, messageList);
        mBinding.spinnerCase.setAdapter(mCaseAdapter);
        mBinding.spinnerCase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTestCase(mCaseAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateTestCase(mCaseAdapter.getItem(0));
    }

    @Override
    public void onVisible() {
        Log.d(TAG, "onVisible: ");
    }

    private void updateTestCase(LocalMessage message) {
        Log.d(TAG, "updateTestCase:" + message);
        mTestCase = null;
        mBinding.etRequestMessage.setText("");
        mResultAdapter.clear();
        if (message == null) {
            return;
        }
        switch (message.operation) {
            case LocalMessage.OPERATION_REG:
                mTestCase = new LocalReg(getActivity(), message);
                mBinding.etRequestMessage.setText(JsonUtil.toJson(message.requestMessage));
                break;
            case LocalMessage.OPERATION_AUTH:
                mTestCase = new LocalAuth(getActivity(), message);
                String authMessage = LocalMessageManager.getInstance().getAuthMessage(message, null);
                mBinding.etRequestMessage.setText(authMessage);
                break;
            case LocalMessage.OPERATION_CAPACITY:
                mTestCase = new LocalCapacity(getActivity());
                break;
        }
        if (mTestCase != null) {
            mTestCase.setTestCaseListener(this);
        }
        int visibility = mTestCase instanceof LocalCapacity ? View.GONE : View.VISIBLE;
        mBinding.tvRequestMessage.setVisibility(visibility);
        mBinding.etRequestMessage.setVisibility(visibility);
    }

    /**
     * 开始进行测试
     */
    private void startTest() {
        Log.d(TAG, "startTest()");
        if (mTestCase == null) {
            showErrorDialog(getString(R.string.test_failed), getString(R.string.select_case_error));
            return;
        }
        showLoading();
        String message = mBinding.etRequestMessage.getText().toString().trim();
        mResultAdapter.clear();
        mTestCase.startTest(message, 1);
    }

    @Override
    public void onTestComplete(int index, TestResponse response) {
        Log.d(TAG, "onTestComplete index=" + index + "," + response);
        mResultAdapter.addData(response);
        Log.d(TAG, "itemCount=" + mResultAdapter.getItemCount());
        dismissDialog();
    }

    @Override
    public void onTestError(String describe) {
        Log.d(TAG, "onTestError describe=" + describe);
        showErrorDialog(getString(R.string.test_failed), describe);
    }
}
