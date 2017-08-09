package com.andkid.fancy.request.target;

/**
 * Created by yuguan.chen on 2017/7/17.
 */

public class NoTarget implements Target {
    @Override
    public void setResource(Object resource) {

    }

    @Override
    public Object getResource() {
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
