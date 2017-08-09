package com.andkid.fancy.newloader.implement;

import com.andkid.fancy.newloader.FanliImageError;
import com.andkid.fancy.newloader.Request;
import com.andkid.fancy.newloader.Response;

public interface ResponseDelivery {

    void postResponse(Request<?> request, Response<?> response);

    void postResponse(Request<?> request, Response<?> response, Runnable runnable);

    void postError(Request<?> request, FanliImageError error);

}
