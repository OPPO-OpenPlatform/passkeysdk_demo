package com.osec.fido2test.test.parse;

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;

public class NoneAttestationStatement extends AttestationStatement {

    public NoneAttestationStatement() {
    }

    @Override
    public DataItem encode() throws CborException {
        Map result = new Map();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoneAttestationStatement;
    }

    @Override
    public String getName() {
        return "NONE ATTESTATION";
    }
}
