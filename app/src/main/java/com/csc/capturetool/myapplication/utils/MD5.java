package com.csc.capturetool.myapplication.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类
 * Created by SirdarYangK on 2018/11/2
 * des: 这里MD5_16取了MD5_32的中间16位
 */
public class MD5 {

    private static String byte2Hex(byte b) {
        int value = (b & 0x7F) + (b < 0 ? 0x80 : 0);
        return (value < 0x10 ? "0" : "") + Integer.toHexString(value).toLowerCase();
    }

    public static String MD5_32(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return null;
        }
        try {
            return MD5_32(pwd.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String MD5_32(byte[] pwd) {

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            StringBuffer strbuf = new StringBuffer();

            md5.update(pwd);
            byte[] digest = md5.digest();

            for (int i = 0; i < digest.length; i++) {
                strbuf.append(byte2Hex(digest[i]));
            }

            return strbuf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String MD5_16(String pwd) {
        return MD5_32(pwd).subSequence(8, 24).toString();
    }

    public static String MD5_16(byte[] pwd) {
        return MD5_32(pwd).subSequence(8, 24).toString();
    }

    /**
     * 对文件本身进行md5加密
     *
     * @param file
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String MD5_32(File file) {
        int bufferSize = 10 * 1024;
        FileInputStream in = null;
        DigestInputStream digestInputStream = null;
        try {
            if (file == null) {
                return null;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            digestInputStream = new DigestInputStream(in, messageDigest);
            byte[] buffer = new byte[bufferSize];
            // read的过程中进行MD5处理，直到读完文件,解决内存溢出问题
            while (digestInputStream.read(buffer) > 0) {
            }
            messageDigest = digestInputStream.getMessageDigest();
            StringBuffer strbuf = new StringBuffer();
            byte[] digest = messageDigest.digest();

            for (int i = 0; i < digest.length; i++) {
                strbuf.append(byte2Hex(digest[i]));
            }

            return strbuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (digestInputStream != null) {
                    digestInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5= MessageDigest.getInstance("MD5");
		/*BASE64Encoder base64en = new BASE64Encoder();
		//加密后的字符串
		String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));*/
        String newstr = Base64.encodeToString(md5.digest(str.getBytes("utf-8")), Base64.NO_WRAP);
        return newstr;
    }
}
