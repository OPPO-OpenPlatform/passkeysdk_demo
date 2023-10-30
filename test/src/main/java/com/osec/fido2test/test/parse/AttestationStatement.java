package com.osec.fido2test.test.parse;

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;

public abstract class AttestationStatement {

    /**
     * @param fmt
     * @param attStmt
     * @return Attestation statement of provided format
     */
    public static AttestationStatement decode(String fmt, DataItem attStmt) {
        if (fmt.equals("fido-u2f")) {
            FidoU2fAttestationStatement stmt = FidoU2fAttestationStatement.decode(attStmt);
            return stmt;
        } else if (fmt.equals("android-safetynet")) {
            AndroidSafetyNetAttestationStatement stmt;
            try {
                stmt = AndroidSafetyNetAttestationStatement.decode(attStmt);
            } catch (CborException e) {
                return null;
            }
            return stmt;
        } else if (fmt.equals("packed")) {
            return PackedAttestationStatement.decode(attStmt);
        } else if (fmt.equals("none")) {
            return new NoneAttestationStatement();
        } else if (fmt.equals("android-key")){
            return AndroidKeyAttestationStatement.decode(attStmt);
        }

        return null;
    }

    /**
     * @return Encoded AttestationStatement
     * @throws CborException
     */
    public abstract DataItem encode() throws CborException;

    public abstract String getName();


}
