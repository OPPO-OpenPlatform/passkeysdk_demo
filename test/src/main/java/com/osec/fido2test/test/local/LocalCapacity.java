package com.osec.fido2test.test.local;

import android.app.Activity;
import android.util.Log;


import com.osec.fido2sdk.Fido2Request;
import com.osec.fido2sdk.Fido2Sdk;
import com.osec.fido2test.entity.TestResponse;
import com.omes.fido2test.R;

public class LocalCapacity extends BaseLocalCase {
    private static final String TAG = "LocalReset";

    public LocalCapacity(Activity activity) {
        super(activity, null);
    }

    @Override
    protected Fido2Request createRequest(String message) {
        return null;
    }

    @Override
    public void startTest(String message, int count) {
        Log.d(TAG, "startTest: count=" + count);
        for (int i = 0; i < count; i++) {
            int finalIndex = i;
            Fido2Sdk.getInstance().isAuthenticatorCapacityAvailable(mActivity,
                    Fido2Sdk.AuthenticatorCapacity.PLATFORM_PASSKEY, result -> {
                        TestResponse testResponse = new TestResponse();
                        if (result) {
                            // true 认证器支持此能力
                            testResponse.success = true;
                            testResponse.resultStr = mActivity.getString(R.string.success);
                        } else {
                            // false 认证器不支持此能力
                            testResponse.success = false;
                            testResponse.resultStr = mActivity.getString(R.string.authType_not_support);
                        }
                        mListener.onTestComplete(finalIndex, testResponse);
                    });
        }
    }
}
