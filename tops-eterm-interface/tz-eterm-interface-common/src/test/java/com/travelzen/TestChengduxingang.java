package com.travelzen;

import java.io.UnsupportedEncodingException;

public class TestChengduxingang {

    public static String hexToString(String hex) {
        if (hex == null || (hex.length() % 2 != 0)) {
            return null;
        }
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            String value = hex.substring(2 * i, 2 * i + 2);
            temp = Integer.valueOf(value, 16);
            bytes[i] = (byte) (temp & 0xff);
        }
        String rs = null;
        try {
            rs = new String(bytes, "ISO646-US");
            // rs = new String(bytes,"GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;

    }

    public static String stringToHex(String cmd) {
        if (cmd == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            bytes = cmd.getBytes("ISO646-US");
            // bytes = cmd.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}
