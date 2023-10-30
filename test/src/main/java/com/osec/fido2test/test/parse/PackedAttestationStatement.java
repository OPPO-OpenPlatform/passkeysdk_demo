package com.osec.fido2test.test.parse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.UnicodeString;

public class PackedAttestationStatement extends AttestationStatement {

    public byte[] sig;
    public byte[] attestnCert;
    public List<byte[]> caCert;
    public Algorithm alg;
    public byte[] ecdaaKeyId;

    /**
     * @param sig
     * @param attestnCert
     * @param caCert
     */
    public PackedAttestationStatement(byte[] sig, byte[] attestnCert, List<byte[]> caCert,
                                      String alg) {
        super();
        this.sig = sig;
        this.attestnCert = attestnCert;
        this.caCert = caCert;
        this.alg = Algorithm.decode(alg);
        this.ecdaaKeyId = null;
    }

    /**
     * PackedAttestationStatement
     *
     * @param sig
     * @param ecdaaKeyId
     * @param alg
     */
    public PackedAttestationStatement(byte[] sig, byte[] ecdaaKeyId, String alg) {
        super();
        this.sig = sig;
        this.ecdaaKeyId = ecdaaKeyId;
        this.alg = Algorithm.decode(alg);
        this.caCert = null;
        this.attestnCert = null;
    }

    public PackedAttestationStatement() {
        this.sig = null;
        this.attestnCert = null;
        this.caCert = null;
        this.alg = null;
        this.ecdaaKeyId = null;
    }

    /**
     * @param attStmt
     * @return Decoded FidoU2fAttestationStatement
     */
    public static PackedAttestationStatement decode(DataItem attStmt) {
        PackedAttestationStatement result = new PackedAttestationStatement();
        Map given = null;

        if (attStmt instanceof ByteString) {
            byte[] temp = ((ByteString) attStmt).getBytes();
            List<DataItem> dataItems = null;
            try {
                dataItems = CborDecoder.decode(temp);
            } catch (Exception e) {
            }
            given = (Map) dataItems.get(0);
        } else {
            given = (Map) attStmt;
        }

        for (DataItem data : given.getKeys()) {
            if (data instanceof UnicodeString) {
                if (((UnicodeString) data).getString().equals("x5c")) {
                    Array array = (Array) given.get(data);
                    List<DataItem> list = array.getDataItems();
                    if (list.size() > 0) {
                        result.attestnCert = ((ByteString) list.get(0)).getBytes();
                    }
                    result.caCert = new ArrayList<byte[]>();
                    for (int i = 1; i < list.size(); i++) {
                        result.caCert.add(((ByteString) list.get(i)).getBytes());
                    }
                } else if (((UnicodeString) data).getString().equals("sig")) {
                    result.sig = ((ByteString) (given.get(data))).getBytes();
                } else if (((UnicodeString) data).getString().equals("alg")) {
                    if (given.get(data) instanceof NegativeInteger) {
                        int algInt = new BigDecimal(((NegativeInteger) (given.get(data))).getValue()).intValueExact();
                        result.alg = Algorithm.decode(algInt);
                    } else if (given.get(data) instanceof UnicodeString) {
                        String algInt = ((UnicodeString) (given.get(data))).getString();
                        result.alg = Algorithm.decode(algInt);
                    }
                } else if (((UnicodeString) data).getString().equals("ecdaaKeyId")) {
                    result.ecdaaKeyId = ((ByteString) (given.get(data))).getBytes();
                }
            }
        }
        return result;
    }

    @Override
    public DataItem encode() throws CborException {
        Map result = new Map();
        if (attestnCert != null) {
            Array x5c = new Array();
            x5c.add(new ByteString(attestnCert));
            for (byte[] cert : this.caCert) {
                x5c.add(new ByteString(cert));
            }
            result.put(new UnicodeString("x5c"), x5c);
        }
        if (ecdaaKeyId != null) {
            result.put(new UnicodeString("ecdaaKeyId"), new ByteString(ecdaaKeyId));
        }
        result.put(new UnicodeString("sig"), new ByteString(sig));
        result.put(new UnicodeString("alg"), new NegativeInteger(alg.encodeToInt()));

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PackedAttestationStatement) {
            PackedAttestationStatement other = (PackedAttestationStatement) obj;
            if (attestnCert == other.attestnCert || Arrays.equals(attestnCert, other.attestnCert)) {
                if (Arrays.equals(sig, other.sig)) {
                    if (caCert == other.caCert || caCert.size() == other.caCert.size()) {
                        if (caCert != null) {
                            for (int i = 0; i < caCert.size(); i++) {
                                if (!Arrays.equals(caCert.get(i), other.caCert.get(i))) {
                                    return false;
                                }
                            }
                        }
                        return other.alg == alg;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Packed Attestation";
    }
}
