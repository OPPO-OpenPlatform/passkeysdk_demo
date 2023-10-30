package com.osec.fido2test.entity;

import androidx.annotation.Keep;

@Keep
public class LocalMessage {

    public static final String OPERATION_REG = "Reg";
    public static final String OPERATION_AUTH = "Auth";
    public static final String OPERATION_CAPACITY = "Capacity";

    public String operation;
    public String title;
    public Object requestMessage;

    @Override
    public final String toString() {
        return operation + " " + title;
    }
}
