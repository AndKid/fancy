package com.andkid.fancy.target;

import com.andkid.fancy.resource.Resource;

/**
 * Created by yuguan.chen on 2017/7/17.
 */

public class NoTarget implements Target {
    @Override
    public void setPlaceHolder(int resId) {

    }

    @Override
    public void setResource(Resource resource) {

    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public Class<?> getResourceClass() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void setResourceClass(Class resourceClass) {

    }
}
