package com.osec.fido2test.test.parse;

import java.util.Arrays;

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;

/**
 * Object representation of the Android SafetyNet attestation statement
 */
public class AndroidSafetyNetAttestationStatement extends AttestationStatement {

    String ver;
    byte[] response;

    public AndroidSafetyNetAttestationStatement() {
        this.ver = null;
        this.response = null;
    }

    /**
     * Decodes a cbor representation of an AndroidSafetyNetAttestationStatement into the object
     * representation
     *
     * @param attStmt Cbor DataItem representation of the attestation statement to decode
     * @return Decoded AndroidSafetyNetAttestationStatement
     * @throws CborException Input was not a valid AndroidSafetyNetAttestationStatement DataItem
     */
    public static AndroidSafetyNetAttestationStatement decode(DataItem attStmt)
            throws CborException {
        AndroidSafetyNetAttestationStatement result = new AndroidSafetyNetAttestationStatement();
        Map given = (Map) attStmt;
        for (DataItem data : given.getKeys()) {
            if (data instanceof UnicodeString) {
                if (((UnicodeString) data).getString().equals("ver")) {
                    UnicodeString version = (UnicodeString) given.get(data);
                    result.ver = version.getString();
                } else if (((UnicodeString) data).getString().equals("response")) {
                    result.response = ((ByteString) (given.get(data))).getBytes();
                }
            }
        }
        if (result.response == null || result.ver == null)
            throw new CborException("Invalid JWT Cbor");
        return result;
    }

    @Override
    public DataItem encode() throws CborException {
        Map map = new Map();
        map.put(new UnicodeString("ver"), new UnicodeString(ver));
        map.put(new UnicodeString("response"), new ByteString(response));
        return map;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AndroidSafetyNetAttestationStatement) {
            AndroidSafetyNetAttestationStatement other = (AndroidSafetyNetAttestationStatement) obj;
            if (ver == other.ver || ((ver != null && other.ver != null) && ver.equals(other.ver))) {
                return Arrays.equals(response, other.response);
            }
        }
        return false;
    }

    /**
     * Return the Google Play Services version used to create the SafetyNet attestation
     *
     * @return the version
     */
    public String getVer() {
        return ver;
    }

    /**
     * @return the response bytes
     */
    public byte[] getResponse() {
        return response;
    }

    @Override
    public String getName() {
        return "Android SafetyNet";
    }
}
