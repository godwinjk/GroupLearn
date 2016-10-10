package com.grouplearn.project.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Godwin Joseph on 24-05-2016 09:48 for Group Learn application.
 */
public class Helper {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final String TAG = "Helper";

    public static String hashString(String toHash) {
        String hash = null;

        if (!TextUtils.isEmpty(toHash)) {
            try {
                MessageDigest digest = MessageDigest.getInstance("sha512");
                byte[] bytes = toHash.getBytes("UTF-8");
                digest.update(bytes, 0, bytes.length);
                bytes = digest.digest();

                // This is ~55x faster than looping and String.formating()
                hash = bytesToHex(bytes);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return hash;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } else {
            return "";
        }
    }

    public static boolean hasSpecialCharactersForUserName(String value) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        boolean b = m.find();
        Log.d(TAG, "Special Character >>>>  :" + b + ":" + value);
        return b;
    }

    public static boolean hasSpaceInUserName(String value) {

        if (value.contains(" "))
            return true;
        else
            return false;

    }

    public static boolean hasAtleastOneAlphabet(String s) {

        if (!TextUtils.isEmpty(s))
            return s.matches(".*[a-zA-Z]+.*");
        return false;

    }

    public static boolean hasAtleastOnSpecialCharecter(String s) {
        String special = "!@#$%^&*()_-+=?><.,{}[]\"\\|/'";
        String pattern = ".*[" + Pattern.quote(special) + "].*";
        return s.matches(pattern);
    }

    public static boolean isAlphaNumeric(String value) {
        return value.matches("^[a-zA-Z0-9]*$");
    }

    public static final boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target.toString().trim()).matches();
    }

    public static long convertEpochTimeToLong(String epochTime) {
        if (TextUtils.isEmpty(epochTime) || !isNumeric(epochTime)) {
            return System.currentTimeMillis();
        }
        long timeInMillis = Long.parseLong(epochTime);
        return timeInMillis;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String getAppVersion(Context mContext) {
        PackageInfo pInfo;
        String version = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = "" + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
