package com.andkid.fancy.resource;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class GifDrawableResource extends RecyclerResource<GifDrawable> {

    public GifDrawableResource(GifDrawable gifDrawable) {
        super(gifDrawable);
    }

    @Override
    public void recycle() {
    }

    @Override
    public int getSize() {
        return 0;
    }

}
