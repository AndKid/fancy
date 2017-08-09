package com.andkid.fancy.newloader;

public class FanliImageError extends Exception {

    public Type type;
    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public enum Type {
        NONE,
        NETWORK_ERROR,
        NO_CONNECTION_ERROR,
        PARSE_ERROR,
        CLIENT_ERROR,
        SERVER_ERROR,
        TIMEOUT_ERROR,
        AUTH_FAILURE_ERROR,
        PARSE_MEMORY,
        CANCEL_ERROR
    }

    public FanliImageError(Type type) {
        this.type = type;
        networkResponse = null;
    }

    public FanliImageError(NetworkResponse response, Type type) {
        this.type = type;
        networkResponse = response;
    }

    public FanliImageError(String exceptionMessage, Type type) {
        super(exceptionMessage);
        this.type = type;
        networkResponse = null;
    }

    public FanliImageError(String exceptionMessage, Throwable reason, Type type) {
        super(exceptionMessage, reason);
        this.type = type;
        networkResponse = null;
    }

    public FanliImageError(Throwable cause, Type type) {
        super(cause);
        this.type = type;
        networkResponse = null;
    }

    void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }

    public long getNetworkTimeMs() {
        return networkTimeMs;
    }
}
