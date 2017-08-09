package com.andkid.fancy.loader;

import com.andkid.fancy.resource.Resource;

/**
 * Created by yuguan.chen on 2017/7/18.
 */

public interface ResourceCallback {

    void onResourceReady(Resource resource, RequestUnit.DataSource dataSource);

    void onLoadFailed();

}
