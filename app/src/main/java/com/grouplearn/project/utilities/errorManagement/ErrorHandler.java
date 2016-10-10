package com.grouplearn.project.utilities.errorManagement;

/**
 * Created by Godwin Joseph on 10-05-2016 11:29 for Group Learn application.
 */
public class ErrorHandler {

    public static final int TOKEN_MISSING = 10001;
    public static final int APP_VERSION_MISSING = 10002;
    public static final int INVALID_RESPONSE_JSON_EXCEPTION = 11000;
    public static final int EMPTY_RESPONSE = 11001;
    public static final int TIMEOUT_EXCEPTION = 11002;
    public static final int BAD_REQUEST = 11003;
    public static final int INVALID_RESPONSE_FROM_CLOUD = 11004;
    public static final int INVALID_DATA_FROM_CLOUD = 11005;
    public static final int EMPTY_RESPONSE_FROM_CLOUD = 11006;
    public static final int GCM_REG_FAILED = 11007;
    public static final int NO_ITEMS = 11008;

    public static class ErrorMessage {
        public static final String TOKEN_MISSING = "TOKEN_MISSING";
        public static final String APP_VERSION_MISSING = "APP_VERSION_MISSING";
        public static final String INVALID_RESPONSE_JSON_EXCEPTION = "INVALID_RESPONSE_JSON_EXCEPTION";
        public static final String EMPTY_RESPONSE = "EMPTY_RESPONSE";
        public static final String TIMEOUT_EXCEPTION = "TIMEOUT_EXCEPTION";

        public static final String BAD_REQUEST = "BAD_REQUEST";
        public static final String INVALID_RESPONSE_FROM_CLOUD = "INVALID_RESPONSE_FROM_CLOUD";
        public static final String INVALID_DATA_FROM_CLOUD = "INVALID_DATA_FROM_CLOUD";
        public static final String EMPTY_RESPONSE_FROM_CLOUD = "EMPTY_RESPONSE_FROM_CLOUD";
        public static final String GCM_REG_FAILED = "GCM_REG_FAILED";
        public static final String NO_ITEMS = "NO_ITEMS";
    }
}
