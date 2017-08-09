package com.andkid.fancy.resource;

import android.graphics.Bitmap;

import com.andkid.fancy.FancyContext;
import com.andkid.fancy.util.Util;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class BitmapResource extends RecyclerResource<Bitmap> {

    public BitmapResource(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void recycle() {
        FancyContext.bitmapPool.put(get());
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(get());
    }

}
