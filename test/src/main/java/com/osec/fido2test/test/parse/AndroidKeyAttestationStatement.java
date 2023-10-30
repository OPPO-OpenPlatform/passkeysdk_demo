package com.osec.fido2test.test.parse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.UnicodeString;

public class AndroidKeyAttestationStatement extends AttestationStatement {

    private final Algorithm alg;

    private final byte[] sig;

    private final List<byte[]> caCert;


    public AndroidKeyAttestationStatement(Algorithm alg, byte[] sig, List<byte[]> caCert){
        this.alg = alg;
        this.sig = sig;
        this.caCert = caCert;
    }

    @Override
    public DataItem encode() throws CborException {
        Map result = new Map();
        if (caCert != null) {
            Array x5c = new Array();
            for (byte[] cert : this.caCert) {
                x5c.add(new ByteString(cert));
            }
            result.put(new UnicodeString("x5c"), x5c);
        }
        result.put(new UnicodeString("sig"), new ByteString(sig));
        result.put(new UnicodeString("alg"), new NegativeInteger(alg.encodeToInt()));

        return result;
    }

    public static AndroidKeyAttestationStatement decode(DataItem attStmt) {
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
        Algorithm algorithm = null;
        byte[] sig = null;
        List<byte[]> certs = null;
        for (DataItem data : given.getKeys()) {
            if (data instanceof UnicodeString) {
                if (((UnicodeString) data).getString().equals("x5c")) {
                    certs = new ArrayList<>();
                    Array array = (Array) given.get(data);
                    List<DataItem> list = array.getDataItems();

                    for (int i = 0; i < list.size(); i++) {
                        certs.add(((ByteString) list.get(i)).getBytes());
                    }
                } else if (((UnicodeString) data).getString().equals("sig")) {
                    sig = ((ByteString) (given.get(data))).getBytes();
                } else if (((UnicodeString) data).getString().equals("alg")) {
                    if(given.get(data) instanceof  NegativeInteger){
                        int algInt = new BigDecimal(((NegativeInteger) (given.get(data))).getValue()).intValueExact();
                        algorithm = Algorithm.decode(algInt);
                    }else if(given.get(data) instanceof UnicodeString){
                        String algInt = ((UnicodeString) (given.get(data))).getString();
                        algorithm = Algorithm.decode(algInt);
                    }
                }
            }
            if(algorithm != null && sig != null && certs != null){
                AndroidKeyAttestationStatement result = new AndroidKeyAttestationStatement(algorithm,sig,certs);
                return result;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return null;
    }


}
