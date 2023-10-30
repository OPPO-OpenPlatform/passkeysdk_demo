package com.osec.fido2test.test.local;

import android.app.Activity;

import com.osec.fido2sdk.Fido2Exception;
import com.osec.fido2sdk.Fido2Request;
import com.osec.fido2test.entity.LocalMessage;


public class LocalAuth extends BaseLocalCase {

    public LocalAuth(Activity activity, LocalMessage localMessage) {
        super(activity, localMessage);
    }

    @Override
    protected Fido2Request createRequest(String message) throws Fido2Exception {
        return new Fido2Request.Authentication(message).build();
    }

}
