package com.andkid.fancy.resource;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public abstract class RecyclerResource<T> implements Resource<T> {

    T content;

    private int refCount;

    public RecyclerResource(T content) {
        this.content = content;
    }

    public T get() {
        refCount += 1;
        return content;
    }

    public void release() {
        refCount -= 1;
        if (refCount <= 0) {
            recycle();
        }
    }

    abstract protected void recycle();

}