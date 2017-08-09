package com.andkid.fancy.request;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public interface RequestListener<ResourceType> {

    /**
     * 成功
     */
    void onResourceReady(ResourceType resource);

    /**
     * 失败
     */
    void onLoadFailed();

}
