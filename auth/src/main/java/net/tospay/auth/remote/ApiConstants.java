package net.tospay.auth.remote;

public class ApiConstants {

    /**
     * Connection timeout duration
     */
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    /**
     * Connection Read timeout duration
     */
    public static final int READ_TIMEOUT = 60 * 1000;
    /**
     * Connection write timeout duration
     */
    public static final int WRITE_TIMEOUT = 60 * 1000;
    /**
     * User Endpoint
     */
    public static final String USER_BASE_URL = "https://auth.tospay.net/api/";

    /**
     * Gateway Endpoint
     */
    public static final String GATEWAY_BASE_URL = "https://apigw.tospay.net/api/";

    /**
     * Notification Endpoint
     */
    public static final String NOTIFICATION_URL = "https://taar.tospay.net";
}
