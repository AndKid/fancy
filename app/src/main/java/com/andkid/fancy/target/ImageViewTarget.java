package com.andkid.fancy.target;

import com.andkid.fancy.resource.Resource;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public abstract class ImageViewTarget<T extends Resource> implements Target<T> {

    protected Class<?> resourceClass;

    @Override
    public void setResourceClass(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    @Override
    public Class<?> getResourceClass() {
        return resourceClass;
    }

}
