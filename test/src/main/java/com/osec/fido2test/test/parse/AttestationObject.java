package com.osec.fido2test.test.parse;

import java.io.ByteArrayOutputStream;
import java.util.List;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class AttestationObject {
    public AuthenticatorData authData;
    public String fmt;
    public AttestationStatement attStmt;

    public AttestationObject() {

    }

    /**
     * @param authData
     * @param fmt
     * @param attStmt
     */
    public AttestationObject(AuthenticatorData authData, String fmt, AttestationStatement attStmt) {
        this.authData = authData;
        this.fmt = fmt;
        this.attStmt = attStmt;
    }

    /**
     * @param attestationObject
     * @return AttestationObject created from the provided byte array
     * @throws CborException
     * @throws CborException
     */
    public static AttestationObject decodeServer(byte[] attestationObject)
            throws CborException {
        AttestationObject result = new AttestationObject();
        List<DataItem> dataItems = CborDecoder.decode(attestationObject);

        if (dataItems.size() == 1 && dataItems.get(0) instanceof Map) {
            DataItem attStmt = null;
            Map attObjMap = (Map) dataItems.get(0);
            for (DataItem key : attObjMap.getKeys()) {
                if (key instanceof UnicodeString) {
                    if (((UnicodeString) key).getString().equals("fmt")) {
                        UnicodeString value = (UnicodeString) attObjMap.get(key);
                        result.fmt = value.getString();
                    }
                    if (((UnicodeString) key).getString().equals("authData")) {
                        byte[] authData = ((ByteString) attObjMap.get(key)).getBytes();
                        result.authData = AuthenticatorData.decode(authData);
                    }
                    if (((UnicodeString) key).getString().equals("attStmt")) {
                        attStmt = attObjMap.get(key);
                    }
                }
            }

            if (attStmt != null) {
                result.attStmt = AttestationStatement.decode(result.fmt, attStmt);
            }

        }
        return result;
    }

    /**
     * @return Encoded byte array containing AttestationObject data
     * @throws CborException
     */
    public byte[] encodeServer() throws CborException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Map map = new Map();

        map.put(new UnicodeString("fmt"),new UnicodeString(fmt));
        map.put(new UnicodeString("authData"),new ByteString(authData.encode()));
        map.put(new UnicodeString("attStmt"),attStmt.encode());

        new CborEncoder(output).encode(map);
        return output.toByteArray();
    }

    public static AttestationObject decode(byte[] attestationObject)
            throws CborException {
        AttestationObject result = new AttestationObject();
        List<DataItem> dataItems = CborDecoder.decode(attestationObject);

        if (dataItems.size() == 1 && dataItems.get(0) instanceof Map) {
            DataItem attStmt = null;
            Map attObjMap = (Map) dataItems.get(0);
            for (DataItem key : attObjMap.getKeys()) {
                if (key instanceof UnsignedInteger) {
                    if (((UnsignedInteger) key).getValue().intValue() == 01) {
                        UnicodeString value = (UnicodeString) attObjMap.get(key);
                        result.fmt = value.getString();
                    }
                    if (((UnsignedInteger) key).getValue().intValue() == 02) {
                        byte[] authData = ((ByteString) attObjMap.get(key)).getBytes();
                        result.authData = AuthenticatorData.decode(authData);
                    }
                    if (((UnsignedInteger) key).getValue().intValue() == 03) {
                        attStmt = attObjMap.get(key);
                    }
                }
            }

            if (attStmt != null) {
                result.attStmt = AttestationStatement.decode(result.fmt, attStmt);
            }

        }
        return result;
    }

    /**
     * @return Encoded byte array containing AttestationObject data
     * @throws CborException
     */
    public byte[] encode() throws CborException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Map map = new Map();

        map.put(new UnsignedInteger(01),new UnicodeString(fmt));
        map.put(new UnsignedInteger(02),new ByteString(authData.encode()));
        map.put(new UnsignedInteger(03),attStmt.encode());

        new CborEncoder(output).encode(map);
        return output.toByteArray();
    }

    /**
     * @return the authData
     */
    public AuthenticatorData getAuthenticatorData() {
        return authData;
    }

    /**
     * @return the fmt
     */
    public String getFormat() {
        return fmt;
    }

    /**
     * @return the attStmt
     */
    public AttestationStatement getAttestationStatement() {
        return attStmt;
    }
}
