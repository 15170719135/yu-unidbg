package com.xingqiu_test.zuiyou;

public class test {
    public static byte[] a(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i2 = 0; i2 < length; i2 += 2) {
            bArr[i2 / 2] = (byte) ((Character.digit(str.charAt(i2), 16) << 4) + Character.digit(str.charAt(i2 + 1), 16));
        }
        return bArr;
    }

    public static void main(String[] args) {
        String str = "117425";
        System.out.println(a(str));
    }
}
