package com.andkid.fancy.target;

import com.andkid.fancy.resource.Resource;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public interface Target<T extends Resource<?>> {

    void setPlaceHolder(int resId);

    void setResource(T resource);

    T getResource();

    void setResourceClass(Class<?> resourceClass);

    Class<?> getResourceClass();

    void clear();

}
