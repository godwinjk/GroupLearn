package com.grouplearn.project.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Godwin Joseph on 23-05-2016 20:36 for Group Learn application.
 */
public class InputValidator {


    static Context mCotext;
    private static String TAG = "MyInputValidator";

    public InputValidator(Context ctx) {
        this.mCotext = ctx;

    }

    final static Set<String> protocols, protocolsWithHost;

    static {

        protocolsWithHost = new HashSet<String>(Arrays.asList(new String[]{"file", "ftp", "http", "https"}));
        protocols = new HashSet<String>(Arrays.asList(new String[]{"mailto", "news", "urn"}));
        protocols.addAll(protocolsWithHost);
    }

    public static boolean isURI(String str) {
        int colon = str.indexOf(':');
        if (colon < 3) return false;
        String proto = str.substring(0, colon).toLowerCase();
        if (!protocols.contains(proto)) return false;

        try {

            URI uri = new URI(str);

            if (protocolsWithHost.contains(proto)) {

                if (uri.getHost() == null) return false;

                String path = uri.getPath();
                if (path != null) {

                    for (int i = path.length() - 1; i >= 0; i--) {
                        if ("?<>:*|\"".indexOf(path.charAt(i)) > -1)
                            return false;
                    }
                }
            }

            return true;
        } catch (Exception ex) {
        }

        return false;
    }

    public int validateUserName(EditText et_userName) {
        String userName = et_userName.getText().toString().trim();

        int status = 0;


        if (hasSpecialCharactersForUserName(userName)) {
            Log.d(TAG, "Invalid user name||not alpha numeric");
            status = ErrorCode.INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTERS;
            et_userName.requestFocus();
            et_userName.setError(ErrorMessage.INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTER);
        }

        if (hasSpaceInUserName(userName)) {
            Log.d(TAG, "Invalid user name||should not contain spaces");
            status = ErrorCode.INVALID_USER_NAME_CONTAINS_SPACE;
            et_userName.requestFocus();
            et_userName.setError(ErrorMessage.INVALID_USER_NAME_CONTAINS_SPACE);
        }
        if (userName.length() < 3 || userName.length() > 32) {

            Log.d(TAG, "Invalid user name||should be >3 and < 32");
            status = ErrorCode.INVALID_USER_NAME_LENGTH;
            et_userName.setError(ErrorMessage.INVALID_USER_NAME_LENGTH);
            et_userName.requestFocus();

        }
        if (TextUtils.isEmpty(userName)) {
            Log.d(TAG, "Invalid user name || empty");
            status = ErrorCode.INVALID_USER_NAME;
            et_userName.setError(ErrorMessage.INVALID_USER_NAME);
            et_userName.requestFocus();
        }
        return status;
    }

    public int validateUserName(String userName) {
        userName = userName.trim();

        int status = 0;


        if (hasSpecialCharactersForUserName(userName)) {
            Log.d(TAG, "Invalid user name||not alpha numeric");
            status = ErrorCode.INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTERS;
        }

        if (hasSpaceInUserName(userName)) {
            Log.d(TAG, "Invalid user name||should not contain spaces");
            status = ErrorCode.INVALID_USER_NAME_CONTAINS_SPACE;
        }
        if (userName.length() < 3 || userName.length() > 32) {

            Log.d(TAG, "Invalid user name||should be >3 and < 32");
            status = ErrorCode.INVALID_USER_NAME_LENGTH;

        }
        if (TextUtils.isEmpty(userName)) {
            Log.d(TAG, "Invalid user name || empty");
            status = ErrorCode.INVALID_USER_NAME;
        }
        return status;
    }

    public int validatePassword(EditText et_password) {

        String value = et_password.getText().toString();
        int status = 0;


        // else if (value.equals(savedOldPassword)){return 3;}


        if (hasAtleastOneAlphabet(value) == false || !hasAtleastOnSpecialCharecter(value)) {
            Log.d(TAG, "Invalid password||not contains special character");
            et_password.setError(ErrorMessage.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC);
            et_password.requestFocus();
            status = -1;
        }

        if (isAlphaNumeric(value)) {
            Log.d(TAG, "Invalid password||not contains alpha numeric character");
            et_password.setError(ErrorMessage.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC);
            et_password.requestFocus();
            status = -1;
        }


        if (!hasNumericValues(value)) {
            Log.d(TAG, "Invalid password||not contains alpha numeric character");
            et_password.setError(ErrorMessage.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC);
            et_password.requestFocus();
            status = -1;
        }

        if (value.trim().length() < 6 || value.trim().length() > 32) {
            Log.d(TAG, "Invalid password||should be >6 and < 32");
            et_password.setError(ErrorMessage.INVALID_PASSWORD_LENGTH);
            et_password.requestFocus();
            status = -1;
        }
        if (TextUtils.isEmpty(value)) {
            Log.e(TAG, "Password is empty||");
            et_password.setError(ErrorMessage.INVALID_PASSWORD);

            et_password.requestFocus();
            status = -1;
        }

        return status;
    }

    private boolean hasNumericValues(String value) {
        return value.matches(".*\\d+.*");

    }

    public int validatePassword(String password) {

        int status = 0;


        // else if (value.equals(savedOldPassword)){return 3;}


        if (hasAtleastOneAlphabet(password) == false || !hasAtleastOnSpecialCharecter(password)) {
            Log.d(TAG, "Invalid password||not contains special character");

            status = ErrorCode.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC;
        }
        if (isAlphaNumeric(password)) {
            Log.d(TAG, "Invalid password||not contains alpha numeric character");
            status = ErrorCode.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC;
        }
        if (password.trim().length() < 6 || password.trim().length() > 32) {
            Log.d(TAG, "Invalid password||should be >6 and < 32");
            status = ErrorCode.INVALID_PASSWORD_LENGTH;
        }
        if (TextUtils.isEmpty(password)) {
            Log.e(TAG, "Password is empty||");

            status = ErrorCode.INVALID_PASSWORD;
        }

        return status;
    }

    public boolean hasAtleastOneAlphabetAndNumber(String s) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        String sa = ".*[a-z].*";
        return s.matches(n) && s.matches(a) && s.matches(a);
    }

    public int validateEmail(EditText et_email) {
        int status = 0;
        String email = et_email.getText().toString();
        Log.d(TAG, "EMAIL || EMAIL " + email);

        if (isValidEmail(email) == false) {
            Log.d(TAG, "Invalid Email.");
            status = ErrorCode.INVALID_EMAIL_PATTERN_NOT_MATCHED;
            et_email.setError(ErrorMessage.INVALID_EMAIL_PATTERN_NOT_MATCHED);
        }

        if (TextUtils.isEmpty(email)) {
            Log.d(TAG, "Email is empty.");
            status = ErrorCode.INVALID_EMAIL;
            et_email.setError(ErrorMessage.INVALID_EMAIL);
        }


        return status;
    }

    public int validateEmail(String et_email) {
        int status = 0;
        String email = et_email;
        Log.d(TAG, "EMAIL || EMAIL " + email);

        if (email != null && isValidEmail(email) == false) {
            Log.d(TAG, "Invalid Email.");
            status = ErrorCode.INVALID_EMAIL_PATTERN_NOT_MATCHED;
            //et_email.setError(ErrorMessage.INVALID_EMAIL_PATTERN_NOT_MATCHED);
        }

        if (TextUtils.isEmpty(email)) {
            Log.d(TAG, "Email is empty.");
            status = ErrorCode.INVALID_EMAIL;
            //et_email.setError(ErrorMessage.INVALID_EMAIL);
        }


        return status;
    }

    public int validateConfirmPassword(EditText et_confirmPassword, EditText et_password) {
        AppSharedPreference pref = new AppSharedPreference(mCotext);
        int status = 0;
        String value = et_confirmPassword.getText().toString();
        String password = et_password.getText().toString();

        Log.d(TAG, "OLD PASSWORD " + password + " CONFIRM PASSWORD " + value);

        if (!value.equals(password)) {
            Log.d(TAG, "Password mismatch.");
            status = ErrorCode.INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD;
            et_confirmPassword.setError(ErrorMessage.INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD);
        }
        if (TextUtils.isEmpty(value)) {
            Log.d(TAG, "Confirm password empty.");
            status = ErrorCode.INVALID_CONFIRM_PASSWORD;
            et_confirmPassword.setError(ErrorMessage.INVALID_CONFIRM_PASSWORD);
        }


        return status;

    }

    public int validateConfirmPassword(String confirmPassword, String password) {
        int status = 0;


        Log.d(TAG, "OLD PASSWORD " + password + " CONFIRM PASSWORD " + confirmPassword);

        if (!confirmPassword.equals(password)) {
            Log.d(TAG, "Password mismatch.");
            status = ErrorCode.INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Log.d(TAG, "Confirm password empty.");
            status = ErrorCode.INVALID_CONFIRM_PASSWORD;
        }


        return status;

    }

    public int validateFullName(EditText et_fullname) {
        int status = 0;
        String value = et_fullname.getText().toString();
        Log.d(TAG, " FULL NAME || FULL NAME " + value);

        if (value != null && (value.trim().length() < 2 || value.trim().length() >= 32)) {
            Log.d(TAG, "Invalid full name||should be >2 and < 32");
            status = ErrorCode.INVALID_FULL_NAME_LENGTH;
            et_fullname.setError(ErrorMessage.INVALID_FULL_NAME_LENGTH);
        }
        if (!hasAtleastOneAlphabet(value)) {
            Log.d(TAG, "Invalid Full Name.. no alpha bets");
            status = ErrorCode.INVALID_FULL_NAME;
            et_fullname.setError(ErrorMessage.INVALID_FULL_NAME);

        }
        if (TextUtils.isEmpty(value)) {
            Log.d(TAG, "Invalid Full Name");
            status = ErrorCode.INVALID_FULL_NAME;
            et_fullname.setError(ErrorMessage.INVALID_FULL_NAME);
        }


        return status;
    }


    public int validateFullName(String value) {
        int status = 0;
        Log.d(TAG, " FULL NAME || FULL NAME " + value);
        if (value != null && (value.trim().length() < 2 || value.trim().length() >= 32)) {
            Log.d(TAG, "Invalid full name||should be >2 and < 32");
            status = ErrorCode.INVALID_FULL_NAME_LENGTH;
        }
        if (!hasAtleastOneAlphabet(value)) {
            Log.d(TAG, "Invalid Full Name.. no alpha bets");
            status = ErrorCode.INVALID_FULL_NAME;

        }
        if (TextUtils.isEmpty(value)) {
            Log.d(TAG, "Invalid Full Name");
            status = ErrorCode.INVALID_FULL_NAME;
        }


        return status;
    }

    private boolean hasSpecialCharactersForUserName(String value) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        boolean b = m.find();
        Log.d(TAG, "Special Character >>>>  :" + b + ":" + value);
        return b;
    }

    private boolean hasAtleastOneAlphabet(String s) {

        if (!TextUtils.isEmpty(s))
            return s.matches(".*[a-zA-Z]+.*");
        return false;

    }

    private boolean hasAtleastOnSpecialCharecter(String s) {
        String special = "!@#$%^&*()_-+=?><.,{}[]\"\\|/'";
        String pattern = ".*[" + Pattern.quote(special) + "].*";
        return s.matches(pattern);
    }

    public boolean hasSpaceInUserName(String value) {

        if (value.contains(" "))
            return true;
        else
            return false;

    }

    public final boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target.toString().trim()).matches();
    }

    private boolean isAlphaNumeric(String value) {
        return value.matches("^[a-zA-Z0-9]*$");
    }

    private boolean isContainsSpecialCharecter(String value) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        boolean b = m.find();
        Log.d(TAG, "Special Character :" + b + ":" + value);
        return b;
    }

    public boolean isValidName(EditText editText) {
        String userName = editText.getText().toString().trim();

        boolean status = true;


        if (isContainsSpecialCharecter(userName)) {
            Log.d(TAG, "Invalid GroupName name||not alpha numeric");
            editText.setError(ErrorMessage.INVALID_NAME_CONTAINS_SPECIAL_CHARECTER);
            status = false;
        } else {
            status = true;
        }
        if (userName.length() < 3 || userName.length() > 32) {

            Log.d(TAG, "Invalid GroupName name||should be >3 and < 32");
            status = false;
            editText.setError(ErrorMessage.INVALID_NAME_LENGTH);
            editText.requestFocus();

        }
        if (TextUtils.isEmpty(userName)) {
            Log.d(TAG, "Invalid user name || empty");
            editText.setError(ErrorMessage.INVALID_NAME_EMPTY);
            editText.requestFocus();
            status = false;

        }
        return status;
    }

    private static class ErrorCode {
        public static final int INVALID_USER_NAME = -1;
        public static final int INVALID_USER_NAME_LENGTH = -2;
        public static final int INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTERS = -3;
        public static final int INVALID_USER_NAME_NOT_CONTAINS_ALPHA_NUMERIC = -4;
        public static final int INVALID_USER_NAME_CONTAINS_SPACE = -5;

        public static final int INVALID_PASSWORD = -6;
        public static final int INVALID_PASSWORD_LENGTH = -7;
        public static final int INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC = -8;


        public static final int INVALID_EMAIL = -9;
        public static final int INVALID_EMAIL_PATTERN_NOT_MATCHED = -10;

        public static final int INVALID_CONFIRM_PASSWORD = -11;
        public static final int INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD = -12;

        public static final int INVALID_FULL_NAME = -13;
        public static final int INVALID_FULL_NAME_LENGTH = -14;


    }

    private static class ErrorMessage {
        public static final String INVALID_USER_NAME = mCotext.getString(R.string.invalid_user_name);
        public static final String INVALID_USER_NAME_LENGTH = mCotext.getString(R.string.invalid_user_name_length);
        public static final String INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTER = mCotext.getString(R.string.invalid_user_name_alpha_numeric);
        public static final String INVALID_USER_NAME_CONTAINS_SPACE = mCotext.getString(R.string.invalid_user_name_contains_space);

        public static final String INVALID_PASSWORD = mCotext.getString(R.string.invalid_password);
        public static final String INVALID_PASSWORD_LENGTH = mCotext.getString(R.string.invalid_password_length);
        public static final String INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC = mCotext.getString(R.string.invalid_password_not_alpha_numeric);

        public static final String INVALID_EMAIL = mCotext.getString(R.string.invalid_email);
        public static final String INVALID_EMAIL_PATTERN_NOT_MATCHED = mCotext.getString(R.string.invalid_email_pattern_not_matched);

        public static final String INVALID_CONFIRM_PASSWORD = mCotext.getString(R.string.invalid_confirm_password);
        public static final String INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD = mCotext.getString(R.string.invalid_confirm_password_not_matching_with_old);

        public static final String INVALID_FULL_NAME = mCotext.getString(R.string.invalid_full_name);
        public static final String INVALID_FULL_NAME_LENGTH = mCotext.getString(R.string.invalid_full_name_length);

        public static final String INVALID_NAME_CONTAINS_SPECIAL_CHARECTER = mCotext.getString(R.string.invalid_title_contains_special_character);
        public static final String INVALID_NAME_EMPTY = mCotext.getString(R.string.invalid_name_empty);
        public static final String INVALID_NAME_LENGTH = mCotext.getString(R.string.invalid_name_length);

    }

    public String getErrorMessage(int errorCode) {
        String message = null;

        if (errorCode == ErrorCode.INVALID_USER_NAME)
            message = ErrorMessage.INVALID_USER_NAME;

        else if (errorCode == ErrorCode.INVALID_USER_NAME_LENGTH)
            message = ErrorMessage.INVALID_USER_NAME_LENGTH;

        else if (errorCode == ErrorCode.INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTERS)
            message = ErrorMessage.INVALID_USER_NAME_CONTAINS_SPECIAL_CHARACTER;

        else if (errorCode == ErrorCode.INVALID_USER_NAME_CONTAINS_SPACE)
            message = ErrorMessage.INVALID_USER_NAME_CONTAINS_SPACE;

        else if (errorCode == ErrorCode.INVALID_PASSWORD)
            message = ErrorMessage.INVALID_PASSWORD;

        else if (errorCode == ErrorCode.INVALID_PASSWORD_LENGTH)
            message = ErrorMessage.INVALID_PASSWORD_LENGTH;

        else if (errorCode == ErrorCode.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC)
            message = ErrorMessage.INVALID_PASSWORD_NOT_CONTAINS_ALPHA_NUMERIC;

        else if (errorCode == ErrorCode.INVALID_EMAIL)
            message = ErrorMessage.INVALID_EMAIL;

        else if (errorCode == ErrorCode.INVALID_EMAIL_PATTERN_NOT_MATCHED)
            message = ErrorMessage.INVALID_EMAIL_PATTERN_NOT_MATCHED;

        else if (errorCode == ErrorCode.INVALID_CONFIRM_PASSWORD)
            message = ErrorMessage.INVALID_CONFIRM_PASSWORD;

        else if (errorCode == ErrorCode.INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD)
            message = ErrorMessage.INVALID_CONFIRM_PASSWORD_NOT_MATCHED_WITH_OLD;

        else if (errorCode == ErrorCode.INVALID_FULL_NAME)
            message = ErrorMessage.INVALID_FULL_NAME;

        else if (errorCode == ErrorCode.INVALID_FULL_NAME_LENGTH)
            message = ErrorMessage.INVALID_FULL_NAME_LENGTH;

        return message;
    }
}

