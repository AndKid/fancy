package com.andkid.fancy.load;

import com.andkid.fancy.resource.Resource;

/**
 * Created by AndKid on 2017/8/20 0020.
 */

public interface ResourceCallback {

    void onLoadStart();

    void onResourceReady(Resource<?> resource, DataSource dataSource);

    void onLoadFailed();

}
