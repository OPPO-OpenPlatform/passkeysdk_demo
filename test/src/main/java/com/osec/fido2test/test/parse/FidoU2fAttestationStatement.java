package com.osec.fido2test.test.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;

public class FidoU2fAttestationStatement extends AttestationStatement {
    public byte[] sig;
    public byte[] attestnCert;
    public List<byte[]> caCert;

    /**
     * @param sig
     * @param attestnCert
     * @param caCert
     */
    public FidoU2fAttestationStatement(byte[] sig, byte[] attestnCert, List<byte[]> caCert) {
        super();
        this.sig = sig;
        this.attestnCert = attestnCert;
        this.caCert = caCert;
    }

    public FidoU2fAttestationStatement() {

    }

    /**
     * @param attStmt
     * @return Decoded FidoU2fAttestationStatement
     */
    public static FidoU2fAttestationStatement decode(DataItem attStmt) {
        FidoU2fAttestationStatement result = new FidoU2fAttestationStatement();
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
                }
            }
        }
        return result;
    }

    @Override
    public DataItem encode() throws CborException {
        Map result = new Map();
        Array x5c = new Array();
        x5c.add(new ByteString(attestnCert));
        if(this.caCert != null){
            for (byte[] cert : this.caCert) {
                x5c.add(new ByteString(cert));
            }
        }
        result.put(new UnicodeString("x5c"), x5c);
        result.put(new UnicodeString("sig"), new ByteString(sig));

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FidoU2fAttestationStatement) {
            FidoU2fAttestationStatement other = (FidoU2fAttestationStatement) obj;
            if (Arrays.equals(attestnCert, other.attestnCert)) {
                if (Arrays.equals(sig, other.sig)) {
                    if (caCert.size() == other.caCert.size()) {
                        for (int i = 0; i < caCert.size(); i++) {
                            if (!Arrays.equals(caCert.get(i), other.caCert.get(i))) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "FIDO U2F Authenticator";
    }
}
