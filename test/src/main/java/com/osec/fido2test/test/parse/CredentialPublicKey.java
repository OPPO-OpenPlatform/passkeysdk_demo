package com.osec.fido2test.test.parse;

import java.security.InvalidParameterException;
import java.security.PublicKey;
import java.util.List;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.UnsignedInteger;

public abstract class CredentialPublicKey {
    static final int CRV_LABEL = -1;
    static final int X_LABEL = -2;
    static final int Y_LABEL = -3;
    static final int N_LABEL = -1;
    static final int E_LABEL = -2;
    static final int KTY_LABEL = 1;
    static final int ALG_LABEL = 3;
    Algorithm alg;
    int kty;
    byte[] cborEncodedKey;

    /**
     * @param cbor
     * @return CredentialPublicKey object decoded from cbor byte array
     * @throws CborException
     */
    public static CredentialPublicKey decode(byte[] cbor) throws CborException {
        List<DataItem> dataItems = CborDecoder.decode(cbor);
        if (dataItems.size() < 1 || !(dataItems.get(0) instanceof Map)) {
            return null;
        }

        Map map = (Map) dataItems.get(0);
        // If there are 4 keys in the map, the key should be RSA. If there are 5, then it is ECC.
        if (map.getKeys().size() == 4) {
            RsaKey rsaKey = new RsaKey();

            for (DataItem d : map.getKeys()) {
                int tmp = 0;
                if (d instanceof NegativeInteger) {
                    tmp = ((NegativeInteger) d).getValue().intValue();
                } else if (d instanceof UnsignedInteger) {
                    tmp = ((UnsignedInteger) d).getValue().intValue();
                }

                switch (tmp) {
                    case N_LABEL:
                        rsaKey.n = ((ByteString) map.get(d)).getBytes();
                        break;
                    case E_LABEL:
                        rsaKey.e = ((ByteString) map.get(d)).getBytes();
                        break;
                    case KTY_LABEL:
                        rsaKey.kty = ((UnsignedInteger) map.get(d)).getValue().intValue();
                        break;
                    case ALG_LABEL:
                        rsaKey.alg = Algorithm.decode(((NegativeInteger) map.get(d)).getValue().intValue());
                        if (!Algorithm.isRsaAlgorithm(rsaKey.alg))
                            throw new InvalidParameterException("Unsupported RSA algorithm");
                        break;
                }
            }
            return rsaKey;

        } else if (map.getKeys().size() == 5) {
            EccKey eccKey = new EccKey();

            for (DataItem d : map.getKeys()) {
                int tmp = 0;
                if (d instanceof NegativeInteger) {
                    tmp = ((NegativeInteger) d).getValue().intValue();
                } else if (d instanceof UnsignedInteger) {
                    tmp = ((UnsignedInteger) d).getValue().intValue();
                }

                switch (tmp) {
                    case CRV_LABEL:
                        eccKey.crv = ((UnsignedInteger) map.get(d)).getValue().intValue();
                        break;
                    case X_LABEL:
                        eccKey.x = ((ByteString) map.get(d)).getBytes();
                        break;
                    case Y_LABEL:
                        eccKey.y = ((ByteString) map.get(d)).getBytes();
                        break;
                    case KTY_LABEL:
                        eccKey.kty = ((UnsignedInteger) map.get(d)).getValue().intValue();
                        break;
                    case ALG_LABEL:
                        eccKey.alg = Algorithm.decode(((NegativeInteger) map.get(d)).getValue().intValue());
                        if (!Algorithm.isEccAlgorithm(eccKey.alg))
                            throw new InvalidParameterException("Unsupported ECC algorithm");
                        break;
                }
            }
            return eccKey;
        }

        throw new InvalidParameterException("Unsupported COSE public key sent");
    }

    public static CredentialPublicKey decode(DataItem cbor) throws CborException {
        if(cbor instanceof Map){
            Map map = (Map) cbor;
            // If there are 4 keys in the map, the key should be RSA. If there are 5, then it is ECC.
            if (map.getKeys().size() == 4) {
                RsaKey rsaKey = new RsaKey();

                for (DataItem d : map.getKeys()) {
                    int tmp = 0;
                    if (d instanceof NegativeInteger) {
                        tmp = ((NegativeInteger) d).getValue().intValue();
                    } else if (d instanceof UnsignedInteger) {
                        tmp = ((UnsignedInteger) d).getValue().intValue();
                    }

                    switch (tmp) {
                        case N_LABEL:
                            rsaKey.n = ((ByteString) map.get(d)).getBytes();
                            break;
                        case E_LABEL:
                            rsaKey.e = ((ByteString) map.get(d)).getBytes();
                            break;
                        case KTY_LABEL:
                            rsaKey.kty = ((UnsignedInteger) map.get(d)).getValue().intValue();
                            break;
                        case ALG_LABEL:
                            rsaKey.alg = Algorithm.decode(((NegativeInteger) map.get(d)).getValue().intValue());
                            if (!Algorithm.isRsaAlgorithm(rsaKey.alg))
                                throw new InvalidParameterException("Unsupported RSA algorithm");
                            break;
                    }
                }
                return rsaKey;

            } else if (map.getKeys().size() == 5) {
                EccKey eccKey = new EccKey();

                for (DataItem d : map.getKeys()) {
                    int tmp = 0;
                    if (d instanceof NegativeInteger) {
                        tmp = ((NegativeInteger) d).getValue().intValue();
                    } else if (d instanceof UnsignedInteger) {
                        tmp = ((UnsignedInteger) d).getValue().intValue();
                    }

                    switch (tmp) {
                        case CRV_LABEL:
                            eccKey.crv = ((UnsignedInteger) map.get(d)).getValue().intValue();
                            break;
                        case X_LABEL:
                            eccKey.x = ((ByteString) map.get(d)).getBytes();
                            break;
                        case Y_LABEL:
                            eccKey.y = ((ByteString) map.get(d)).getBytes();
                            break;
                        case KTY_LABEL:
                            eccKey.kty = ((UnsignedInteger) map.get(d)).getValue().intValue();
                            break;
                        case ALG_LABEL:
                            eccKey.alg = Algorithm.decode(((NegativeInteger) map.get(d)).getValue().intValue());
                            if (!Algorithm.isEccAlgorithm(eccKey.alg))
                                throw new InvalidParameterException("Unsupported ECC algorithm");
                            break;
                    }
                }
                return eccKey;
            }
        }
        throw new InvalidParameterException("Unsupported COSE public key sent");
    }

    /**
     * @return Cbor encoded byte array
     * @throws CborException
     */
    public abstract byte[] encodeBytes() throws CborException;

    public abstract DataItem encode() throws CborException;

    public abstract PublicKey convertPubkey();

    /**
     * @return human-readable hex string of the key
     */
    @Override
    public abstract String toString();

    /**
     * Get algorithm info
     *
     * @return algorithm
     */
    public Algorithm getAlg() {
        return alg;
    }
}
