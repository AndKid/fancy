package com.andkid.fancy.resource;

import android.graphics.drawable.BitmapDrawable;

import com.andkid.fancy.FancyContext;
import com.andkid.fancy.util.Util;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class BitmapDrawableResource extends DrawableResource<BitmapDrawable> {

    public BitmapDrawableResource(BitmapDrawable bitmapDrawable) {
        super(bitmapDrawable);
    }

    @Override
    public void recycle() {
        FancyContext.bitmapPool.put(get().getBitmap());
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(get().getBitmap());
    }

}
