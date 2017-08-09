package com.andkid.fancy.newloader.implement;


import com.andkid.fancy.newloader.FanliImageError;
import com.andkid.fancy.newloader.Request;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public interface HttpStack {

    HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, FanliImageError;

}
