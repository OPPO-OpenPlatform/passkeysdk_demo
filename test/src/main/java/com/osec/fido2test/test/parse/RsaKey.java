package com.osec.fido2test.test.parse;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.List;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.UnsignedInteger;

public class RsaKey extends CredentialPublicKey {
    byte[] n, e;

    RsaKey() {
        n = null;
        e = null;
        alg = Algorithm.UNDEFINED;
    }

    public RsaKey(Algorithm alg, byte[] n, byte[] e) {
        this.alg = alg;
        this.n = n;
        this.e = e;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof RsaKey) {
                RsaKey other = (RsaKey) obj;
                if (Arrays.equals(n, other.n) && Arrays.equals(e, other.e) && alg == other.alg) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
        }
        return false;
    }

    @Override
    public byte[] encodeBytes() throws CborException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<DataItem> dataItems =
                new CborBuilder().addMap().put(new UnsignedInteger(KTY_LABEL), new UnsignedInteger(kty))
                        .put(new UnsignedInteger(ALG_LABEL), new NegativeInteger(alg.encodeToInt()))
                        .put(new NegativeInteger(N_LABEL), new ByteString(n))
                        .put(new NegativeInteger(E_LABEL), new ByteString(e)).end().build();
        new CborEncoder(output).encode(dataItems);
        return output.toByteArray();
    }

    @Override
    public DataItem encode() throws CborException {
        List<DataItem> dataItems =
                new CborBuilder().addMap().put(new UnsignedInteger(KTY_LABEL), new UnsignedInteger(kty))
                        .put(new UnsignedInteger(ALG_LABEL), new NegativeInteger(alg.encodeToInt()))
                        .put(new NegativeInteger(N_LABEL), new ByteString(n))
                        .put(new NegativeInteger(E_LABEL), new ByteString(e)).end().build();
        return dataItems.get(0);
    }

    @Override
    public PublicKey convertPubkey() {
        BigInteger nBig = new BigInteger(1, n);
        BigInteger eBig = new BigInteger(1, e);
        KeySpec keySpec = new RSAPublicKeySpec(nBig, eBig);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (InvalidKeySpecException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "alg:" +
                alg.toReadableString() +
                " n:" +
                byte2hex(n) +
                " e:" +
                byte2hex(e);
    }

    public byte[] getN() {
        return n;
    }

    public byte[] getE() {
        return e;
    }


    private String byte2hex(byte[] raw) {
        String arHex = "0123456789ABCDEF";
        StringBuilder hex = new StringBuilder(2 * raw.length);
        for (byte b : raw) {
            hex.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4)).append("0123456789ABCDEF".charAt(b & 0xF));
        }
        return hex.toString();
    }
}
