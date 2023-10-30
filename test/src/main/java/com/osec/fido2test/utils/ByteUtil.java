package com.osec.fido2test.utils;

import android.text.TextUtils;
import android.util.Base64;

public class ByteUtil {
    private static final String TAG = "ByteUtil";

    public static String byte2hex(byte[] raw) {
        if (raw == null) {
            return "";
        }
        StringBuilder hex = new StringBuilder(2 * raw.length);
        for (byte b : raw) {
            hex.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4)).append("0123456789ABCDEF".charAt(b & 0xF));
        }
        return hex.toString();
    }

    public static byte[] hex2byte(String hexs) {
        if (TextUtils.isEmpty(hexs)) {
            return new byte[]{};
        }
        byte[] res = new byte[hexs.length() / 2];
        char[] chs = hexs.toCharArray();
        for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
            res[c] = (byte) (Integer.parseInt(new String(chs, i, 2), 16));
        }
        return res;
    }

    public static byte[] base642byte(String base64) {
        return Base64.decode(base64, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    public static String byte2base64(byte[] raw) {
        return Base64.encodeToString(raw, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }


    public static int array2Int(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            return -1;
        }
        return bytes[3] & 0xFF |
                (bytes[2] & 0xFF) << 8 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[0] & 0xFF) << 24;
    }

    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        byte[][] tmp = arrays;
        int pos = arrays.length;

        for (int i$ = 0; i$ < pos; ++i$) {
            byte[] array = tmp[i$];
            length += array.length;
        }

        byte[] result = new byte[length];
        pos = 0;
        int len$ = arrays.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            byte[] array = tmp[i$];
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }

        return result;
    }

}
