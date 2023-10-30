package com.osec.fido2test.test.local;

import com.osec.fido2test.entity.TestResponse;

public interface TestCaseListener {
    void onTestComplete(int index, TestResponse response);

    void onTestError(String describe);
}
