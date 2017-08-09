package com.andkid.fancy.resource;

import android.graphics.drawable.Drawable;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public abstract class DrawableResource<T extends Drawable> extends RecyclerResource<T> {

    public DrawableResource(T drawable) {
        super(drawable);
    }

}
