package com.andkid.fancy.newloader;

import com.andkid.fancy.newloader.implement.Cache;

public class Response<T> {

    public interface Listener<T> {
        void onResponse(T response);
    }

    public interface ErrorListener {
        void onErrorResponse(FanliImageError error);
    }

    public static <T> Response<T> success(T result, Cache.Entry cacheEntry) {
        return new Response<T>(result, cacheEntry);
    }

    public static <T> Response<T> error(FanliImageError error) {
        return new Response<T>(error);
    }

    public final T result;

    public final Cache.Entry cacheEntry;

    public final FanliImageError error;

    public boolean isSuccess() {
        return error == null;
    }

    private Response(T result, Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    private Response(FanliImageError error) {
        this.result = null;
        this.cacheEntry = null;
        this.error = error;
    }
}
