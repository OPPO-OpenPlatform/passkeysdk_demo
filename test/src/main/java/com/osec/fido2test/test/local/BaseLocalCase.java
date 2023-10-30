package com.osec.fido2test.test.local;

import android.app.Activity;
import android.util.Log;

import com.osec.fido2sdk.Fido2Callback;
import com.osec.fido2sdk.Fido2Exception;
import com.osec.fido2sdk.Fido2Request;
import com.osec.fido2sdk.Fido2Response;
import com.osec.fido2sdk.Fido2Sdk;
import com.osec.fido2test.entity.LocalMessage;
import com.osec.fido2test.entity.TestResponse;
import com.osec.fido2test.utils.JsonUtil;

import com.omes.fido2test.R;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseLocalCase {
    private static final String TAG = "BaseLocalCase";

    protected Activity mActivity;
    protected LocalMessage mLocalMessage;
    protected TestCaseListener mListener;

    public BaseLocalCase(Activity activity, LocalMessage localMessage) {
        this.mActivity = activity;
        this.mLocalMessage = localMessage;
    }

    public void setTestCaseListener(TestCaseListener listener) {
        this.mListener = listener;
    }

    /**
     * 开始进行测试
     *
     * @param message 请求的报文
     * @param count   测试的次数
     */
    public void startTest(String message, int count) {
        Log.d(TAG, "startTest message=" + message);
        Log.d(TAG, "startTest count=" + count);
        Fido2Request request;
        try {
            JSONObject optionsJson = new JSONObject();
            optionsJson.put("options", new JSONObject(message));
            request = createRequest(optionsJson.toString());
        } catch (Fido2Exception | JSONException e) {
            e.printStackTrace();
            mListener.onTestError(mActivity.getString(R.string.error_parse_fido2request, e.getMessage()));
            return;
        }

        for (int i = 0; i < count; i++) {
            int finalIndex = i;
            Fido2Sdk.getInstance().execute(mActivity, request, new Fido2Callback<Fido2Response>() {
                @Override
                public void onFidoSuccess(Fido2Response response) {
                    TestResponse testResponse = new TestResponse();
                    if (!saveTestResponse(response)) {
                        testResponse.success = false;
                        testResponse.resultStr = mActivity.getString(R.string.test_failed_credential);
                        mListener.onTestComplete(finalIndex, testResponse);
                        return;
                    }
                    testResponse.success = true;
                    testResponse.resultStr = JsonUtil.toJson(response);
                    mListener.onTestComplete(finalIndex, testResponse);
                }

                @Override
                public void onFidoFailed(int errorCode, String message) {
                    TestResponse testResponse = new TestResponse();
                    testResponse.success = false;
                    testResponse.resultStr = mActivity.getString(R.string.test_failed_sdk, errorCode, message);
                    mListener.onTestComplete(finalIndex, testResponse);
                }
            });
        }
    }

    protected boolean saveTestResponse(Fido2Response response) {
        return true;
    }

    protected abstract Fido2Request createRequest(String message) throws Fido2Exception;

}
