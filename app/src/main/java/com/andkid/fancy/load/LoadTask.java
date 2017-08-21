package com.andkid.fancy.load;

import com.andkid.fancy.resource.Resource;

/**
 * Created by AndKid on 2017/8/20 0020.
 */

public class LoadTask implements Runnable {

    interface LoadCallback {
        void onLoadSuccess(Resource resource, DataSource dataSource);
        void onLoadFailed();
    }

    @Override
    public void run() {

    }

}
