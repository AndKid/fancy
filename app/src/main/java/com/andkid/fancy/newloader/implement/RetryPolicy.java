package com.andkid.fancy.newloader.implement;

import com.andkid.fancy.newloader.FanliImageError;

public interface RetryPolicy {

    int getCurrentTimeout();

    int getCurrentRetryCount();

    void retry(FanliImageError error) throws FanliImageError;
}
