package com.osec.fido2test.entity;

import androidx.annotation.Keep;

import java.util.Arrays;

@Keep
public class LocalRegInfo {
    public String credentialId;

    @Override
    public String toString() {
        return "LocalRegInfo{" +
                "credentialId=" + credentialId +
                '}';
    }
}
