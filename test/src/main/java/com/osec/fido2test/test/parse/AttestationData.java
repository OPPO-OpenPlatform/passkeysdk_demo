package com.osec.fido2test.test.parse;

import com.osec.fido2test.utils.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

import co.nstant.in.cbor.CborException;

/**
 * Object representation of the attestation data
 */
public class AttestationData {

    private static final String TAG = "AttestationData";

    public byte[] aaguid;
    public byte[] credentialId;
    public CredentialPublicKey publicKey;

    public AttestationData() {
        aaguid = new byte[16];
        credentialId = new byte[]{};
        publicKey = new EccKey();
    }

    /**
     * @param aaguid       Authenticator Attestation GUID
     * @param credentialId Credential ID
     * @param publicKey    CredentialPublicKey
     */
    public AttestationData(byte[] aaguid, byte[] credentialId, CredentialPublicKey publicKey) {
        this.aaguid = aaguid;
        this.credentialId = credentialId;
        this.publicKey = publicKey;
    }

    /**
     * Decodes the input byte array into the AttestationData object
     *
     * @param data
     * @return AttestationData object created from the byte sequence
     * @throws CborException
     */
    public static AttestationData decode(byte[] data) throws CborException {
        AttestationData result = new AttestationData();
        int index = 0;
        if (data.length < 18)
            throw new CborException("Invalid input");
        System.arraycopy(data, 0, result.aaguid, 0, 16);
        index += 16;

        int length = (data[index++] << 8) & 0xFF;
        length += data[index++] & 0xFF;

        result.credentialId = new byte[length];
        System.arraycopy(data, index, result.credentialId, 0, length);
        index += length;

        byte[] cbor = new byte[data.length - index];
        System.arraycopy(data, index, cbor, 0, data.length - index);
        result.publicKey = CredentialPublicKey.decode(cbor);

        return result;
    }

    /**
     * @return Encoded byte array created from AttestationData object
     * @throws CborException
     */
    public byte[] encode() throws CborException {
        byte[] result = ByteUtil.concat(aaguid,
                ByteBuffer.allocate(2).putShort((short) credentialId.length).array(), credentialId,
                publicKey.encodeBytes());
        return result;
    }

    /**
     *
     */
    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof AttestationData) {
                AttestationData other = (AttestationData) obj;
                if (Arrays.equals(other.aaguid, aaguid) && Arrays.equals(credentialId, other.credentialId)
                        && ((publicKey == null && other.publicKey == null)
                        || (publicKey.equals(other.publicKey)))) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    /**
     * @return the aaguid
     */
    public byte[] getAaguid() {
        return aaguid;
    }

    /**
     * @return the credentialId
     */
    public byte[] getCredentialId() {
        return credentialId;
    }

    /**
     * @return the publicKey
     */
    public CredentialPublicKey getPublicKey() {
        return publicKey;
    }
}
