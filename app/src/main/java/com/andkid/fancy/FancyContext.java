package com.andkid.fancy;

import android.content.Context;

import com.andkid.fancy.cache.MemorySizeCalculator;
import com.andkid.fancy.cache.bitmap_recycle.BitmapPool;
import com.andkid.fancy.cache.bitmap_recycle.LruBitmapPool;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class FancyContext {

    public static BitmapPool bitmapPool;
    private MemorySizeCalculator memorySizeCalculator;

    FancyContext(Context context) {
        memorySizeCalculator = new MemorySizeCalculator.Builder(context).build();
        bitmapPool = new LruBitmapPool(memorySizeCalculator.getBitmapPoolSize());
    }

}
