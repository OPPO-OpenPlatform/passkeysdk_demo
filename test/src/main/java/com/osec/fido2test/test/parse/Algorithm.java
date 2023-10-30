package com.osec.fido2test.test.parse;

/**
 * Algorithm enum differentiating between the supported asymmetric key algorithms
 */
public enum Algorithm {
    ES256("ES256"), ES384("ES384"), ES512("ES512"), RS256("RS256"), RS384("RS384"), RS512(
            "RS512"), PS256("PS256"), PS384("PS384"), PS512("PS512"), UNDEFINED("undefined"),ECDH("ECDH"),GM("GM");

    private final String name;

    /**
     * @param name The string representation of the algorithm name
     */
    Algorithm(String name) {
        this.name = name;
    }

    /**
     * @param alg The Algorithm to check
     * @return If the Algorithm is an ECC Algorithm
     */
    public static boolean isEccAlgorithm(Algorithm alg) {
        return alg == ES256 || alg == ES384 || alg == ES512 || alg == ECDH || alg == GM;
    }

    /**
     * @param alg The Algorithm to check
     * @return If the Algorithm is an RSA Algorithm
     */
    public static boolean isRsaAlgorithm(Algorithm alg) {
        return alg == RS256 || alg == RS384 || alg == RS512 || alg == PS256 || alg == PS384
                || alg == PS512;
    }

    /**
     * @param s Input string to decode
     * @return Transport corresponding to the input string
     */
    public static Algorithm decode(String s) {
        for (Algorithm t : Algorithm.values()) {
            if (t.name.equals(s)) {
                return t;
            }
        }

        // COSE Algorithm Identifiers
        if (s.equals("-7")) {
            return ES256;
        }

        throw new IllegalArgumentException(s + " not a valid Algorithm");
    }

    /**
     * @param alg Input integer to decode
     * @return Transport corresponding to the input string
     */
    public static Algorithm decode(int alg) {
        switch (alg) {
            case -25:
                return ECDH;
            case -7:
                return ES256;
            case -35:
                return ES384;
            case -36:
                return ES512;
            case -37:
                return PS256;
            case -38:
                return PS384;
            case -39:
                return PS512;
            case -257:
                return RS256;
            case -258:
                return RS384;
            case -259:
                return RS512;
            case -260:
                return ES256;
            case -261:
                return ES512;
            case -101:
                return GM;
        }
        return Algorithm.UNDEFINED;
    }

    public int encodeToInt() {
        switch (this) {
            case ECDH:
                return -25;
            case ES256:
                return -7;
            case ES384:
                return -35;
            case ES512:
                return -36;
            case PS256:
                return -37;
            case PS384:
                return -38;
            case PS512:
                return -39;
            case RS256:
                return -257;
            case RS384:
                return -258;
            case RS512:
                return -259;
            case GM:
                return -101;
            default:
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }


    public Object toReadableString() {
        return name;
    }
}
