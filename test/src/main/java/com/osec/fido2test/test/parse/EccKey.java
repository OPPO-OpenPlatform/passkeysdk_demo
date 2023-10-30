package com.osec.fido2test.test.parse;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.UnsignedInteger;

public class EccKey extends CredentialPublicKey {

    public static final ECParameterSpec P256 = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")), new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"), new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")), new ECPoint(new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"), new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")), new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"), 1);
    public static final ECParameterSpec P384 = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112319")), new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112316"), new BigInteger("27580193559959705877849011840389048093056905856361568521428707301988689241309860865136260764883745107765439761230575")), new ECPoint(new BigInteger("26247035095799689268623156744566981891852923491109213387815615900925518854738050089022388053975719786650872476732087"), new BigInteger("8325710961489029985546751289520108179287853048861315594709205902480503199884419224438643760392947333078086511627871")), new BigInteger("39402006196394479212279040100143613805079739270465446667946905279627659399113263569398956308152294913554433653942643"), 1);
    public static final ECParameterSpec P512 = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151")), new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057148"), new BigInteger("1093849038073734274511112390766805569936207598951683748994586394495953116150735016013708737573759623248592132296706313309438452531591012912142327488478985984")), new ECPoint(new BigInteger("2661740802050217063228768716723360960729859168756973147706671368418802944996427808491545080627771902352094241225065558662157113545570916814161637315895999846"), new BigInteger("3757180025770020463545507224491183603594455134769762486694567779615544477440556316691234405012945539562144444537289428522585666729196580810124344277578376784")), new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449"), 1);

    byte[] x, y;
    int crv;

    public EccKey() {
        x = null;
        y = null;
        alg = Algorithm.UNDEFINED;
        kty = 2;
    }

    public EccKey(Algorithm alg, byte[] x, byte[] y) {
        this.alg = alg;
        this.x = x;
        this.y = y;
        this.kty = 2;
        this.crv = 1;
    }

    /**
     * @param x
     * @param y
     */
    public EccKey(byte[] x, byte[] y) {
        super();
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof EccKey) {
                EccKey other = (EccKey) obj;
                if (Arrays.equals(x, other.x) && Arrays.equals(y, other.y) && alg == other.alg) {
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
                        .put(new NegativeInteger(CRV_LABEL), new UnsignedInteger(crv))
                        .put(new NegativeInteger(X_LABEL), new ByteString(x))
                        .put(new NegativeInteger(Y_LABEL), new ByteString(y)).end().build();
        new CborEncoder(output).encode(dataItems);
        return output.toByteArray();
    }

    @Override
    public DataItem encode() throws CborException {
        List<DataItem> dataItems =
                new CborBuilder().addMap().put(new UnsignedInteger(KTY_LABEL), new UnsignedInteger(kty))
                        .put(new UnsignedInteger(ALG_LABEL), new NegativeInteger(alg.encodeToInt()))
                        .put(new NegativeInteger(CRV_LABEL), new UnsignedInteger(crv))
                        .put(new NegativeInteger(X_LABEL), new ByteString(x))
                        .put(new NegativeInteger(Y_LABEL), new ByteString(y)).end().build();
        return dataItems.get(0);
    }

    @Override
    public PublicKey convertPubkey() {
        BigInteger x = new BigInteger(1, getX());
        BigInteger y = new BigInteger(1, getY());
        ECPoint w = new ECPoint(x, y);

        ECParameterSpec spec = null;
        switch (crv) {
            case 1:
                spec = P256;
                break;
            case 2:
                spec = P384;
                break;
            case 3:
                spec = P512;
                break;
            default:
                return null;
        }
        KeySpec keySpec = new java.security.spec.ECPublicKeySpec(w, spec);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the x
     */
    public byte[] getX() {
        return x;
    }

    /**
     * @return the y
     */
    public byte[] getY() {
        return y;
    }

    @Override
    public String toString() {
        return "alg:" +
                alg.toReadableString() +
                " x:" +
                byte2hex(x) +
                " y:" +
                byte2hex(y);
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
