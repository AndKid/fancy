package com.andkid.fancy.target;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public interface Target<T> {

    void setResource(T resource);

    T getResource();

    void setResourceClass(Class<?> resourceClass);

    Class<?> getResourceClass();

    void clear();

}
