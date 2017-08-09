package com.andkid.fancy.resource;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public interface Resource<T> {

    T get();

    int getSize();

}
