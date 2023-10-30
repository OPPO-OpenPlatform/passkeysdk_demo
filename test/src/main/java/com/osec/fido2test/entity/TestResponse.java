package com.osec.fido2test.entity;

public class TestResponse {
    public boolean success;
    public String resultStr;

    @Override
    public String toString() {
        return "TestResponse{" +
                "success=" + success +
                ", resultStr='" + resultStr + '\'' +
                '}';
    }
}
