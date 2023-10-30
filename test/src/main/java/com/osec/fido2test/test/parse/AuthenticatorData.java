package com.osec.fido2test.test.parse;

import android.os.Build;
import android.util.Log;

import com.osec.fido2test.utils.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

import co.nstant.in.cbor.CborException;

public class AuthenticatorData {

    private static final String TAG = "AuthenticatorData";

    private final byte[] rpIdHash;
    private byte flags;
    private int signCount;
    private final byte[] extensions;

    // optional
    private final AttestationData attData;

    public byte[] getExtensions() {
        return extensions;
    }

    /**
     * @param rpIdHash
     * @param flags
     * @param signCount
     * @param attData
     */
    public AuthenticatorData(byte[] rpIdHash, byte flags, int signCount, AttestationData attData, byte[] extensions) {
        this.rpIdHash = rpIdHash;
        this.flags = flags;
        this.signCount = signCount;
        this.attData = attData;
        this.extensions = extensions;
    }

    AuthenticatorData() {
        rpIdHash = new byte[32];
        attData = new AttestationData();
        this.extensions = null;
    }

    AuthenticatorData(byte[] rpIdHash, byte flags, int signCount) {
        this.rpIdHash = rpIdHash;
        this.flags = flags;
        this.signCount = signCount;
        this.attData = null;
        this.extensions = null;
    }

    /**
     * @param authData
     * @return Decoded AuthenticatorData object
     */
    public static AuthenticatorData decode(byte[] authData) throws CborException {

        if (authData.length < 37) {
            throw new CborException("Invalid input");
        }
        Log.d(TAG, "decode:" + ByteUtil.byte2hex(authData));

        int index = 0;
        byte[] rpIdHash = new byte[32];
        System.arraycopy(authData, 0, rpIdHash, 0, 32);
        index += 32;
        byte flags = authData[index++];
        int signCount = ByteUtil.array2Int(new byte[]{authData[index++], authData[index++], authData[index++], authData[index++]});
        int definedIndex = index;

        AttestationData attData = null;
        // Bit 6 determines whether attestation data was included
        if ((flags & 1 << 6) != 0) {
            byte[] remainder = new byte[authData.length - index];
            System.arraycopy(authData, index, remainder, 0, authData.length - index);
            Log.d(TAG, "attestationData:" + ByteUtil.byte2hex(remainder));
            try {
                attData = AttestationData.decode(remainder);
            } catch (CborException e) {
                throw new CborException("Error decoding");
            }
        }

        byte[] extensions = null;
        // Bit 7 determines whether extensions are included.
        if ((flags & 1 << 7) != 0) {
            try {
                int start = definedIndex + attData.encode().length;
                if (authData.length > start) {
                    byte[] remainder = new byte[authData.length - start];
                    System.arraycopy(authData, start, remainder, 0, authData.length - start);
                    extensions = remainder;
                }
            } catch (CborException e) {
                throw new CborException("Error decoding authenticator extensions");
            }
        }

        return new AuthenticatorData(rpIdHash, flags, signCount, attData, extensions);
    }

    /**
     * 设备标识 认证返回报文中 无 attData
     *
     * @param authData
     * @return Decoded AuthenticatorData object
     */
    public static AuthenticatorData decodeNoAttData(byte[] authData) throws CborException {

        if (authData.length < 37) {
            throw new CborException("Invalid input");
        }
        Log.d(TAG, "decode:" + ByteUtil.byte2hex(authData));

        int index = 0;
        byte[] rpIdHash = new byte[32];
        System.arraycopy(authData, 0, rpIdHash, 0, 32);
        index += 32;
        byte flags = authData[index++];
        int signCount = ByteUtil.array2Int(new byte[]{authData[index++], authData[index++], authData[index++], authData[index++]});
        int definedIndex = index;

        byte[] extensions = null;
        // Bit 7 determines whether extensions are included.
        if ((flags & 1 << 7) != 0) {
            // int start = definedIndex + attData.encode().length;
            if (authData.length > definedIndex) {
                byte[] remainder = new byte[authData.length - definedIndex];
                System.arraycopy(authData, definedIndex, remainder, 0, authData.length - definedIndex);
                extensions = remainder;
            }
        }

        return new AuthenticatorData(rpIdHash, flags, signCount, null, extensions);
    }

    /**
     * @return the rpIdHash
     */
    public byte[] getRpIdHash() {
        return rpIdHash;
    }

    /**
     * @return the flags
     */
    public byte getFlags() {
        return flags;
    }

    /**
     * @return the UP bit of the flags
     */
    public boolean isUP() {
        return (flags & 1) != 0;
    }

    /**
     * @return the UP bit of the flags
     */
    public boolean isUV() {
        return (flags & 1 << 2) != 0;
    }

    /**
     * @return the UP bit of the flags
     */
    public boolean hasAttestationData() {
        return (flags & 1 << 6) != 0;
    }

    /**
     * @return the UP bit of the flags
     */
    public boolean hasExtensionData() {
        return (flags & 1 << 7) != 0;
    }

    /**
     * @return the signCount
     */
    public int getSignCount() {
        return signCount;
    }

    /**
     * @return the attData
     */
    public AttestationData getAttData() {
        return attData;
    }

    /**
     * @return Encoded byte array
     * @throws CborException
     */
    public byte[] encode() throws CborException {
        byte[] flags = {this.flags};
        byte[] signCount = ByteBuffer.allocate(4).putInt(this.signCount).array();
        byte[] result;
        if (this.attData != null) {
            byte[] attData = this.attData.encode();
            result = ByteUtil.concat(rpIdHash, flags, signCount, attData);
        } else {
            result = ByteUtil.concat(rpIdHash, flags, signCount);
        }
        if (this.extensions != null) {
            result = ByteUtil.concat(result, extensions);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof AuthenticatorData) {
                AuthenticatorData other = (AuthenticatorData) obj;
                if (flags == other.flags) {
                    if (Arrays.equals(other.rpIdHash, rpIdHash)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (Integer.compareUnsigned(signCount, other.signCount) == 0) {
                                if (attData == null && other.attData == null) {
                                    return true;
                                }
                                if (attData.equals(other.attData)) {
                                    return true;
                                }
                            }
                        } else {
                            if (attData == null && other.attData == null) {
                                return true;
                            }
                            if (attData.equals(other.attData)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

}
