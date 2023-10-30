package com.osec.fido2test.test.local;

import android.app.Activity;
import android.util.Log;

import com.osec.fido2sdk.Fido2Exception;
import com.osec.fido2sdk.Fido2Request;
import com.osec.fido2sdk.Fido2Response;
import com.osec.fido2test.entity.LocalMessage;
import com.osec.fido2test.entity.LocalRegInfo;
import com.osec.fido2test.global.LocalMessageManager;
import com.osec.fido2test.test.parse.AttestationObject;
import com.osec.fido2test.utils.ByteUtil;

public class LocalReg extends BaseLocalCase {
    private static final String TAG = "LocalReg";


    public LocalReg(Activity activity, LocalMessage localMessage) {
        super(activity, localMessage);
        Log.d(TAG, "LocalReg()");
    }

    @Override
    protected Fido2Request createRequest(String message) throws Fido2Exception {
        return new Fido2Request.Registration(message).build();
    }

    @Override
    protected boolean saveTestResponse(Fido2Response response) {
        try {
            byte[] attestationBytes = ByteUtil.base642byte((String) response.getCredentials().getResponse().get("attestationObject"));
            AttestationObject attestationObject = AttestationObject.decodeServer(attestationBytes);
            byte[] credentialId = attestationObject.authData.getAttData().credentialId;
            LocalRegInfo regInfo = new LocalRegInfo();
            regInfo.credentialId = ByteUtil.byte2base64(credentialId);
            LocalMessageManager.getInstance().saveRegInfo(mLocalMessage.title, regInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
