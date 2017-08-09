package com.andkid.fancy;

import android.content.Context;

import com.andkid.fancy.cache.bitmap_recycle.BitmapPool;
import com.andkid.fancy.cache.bitmap_recycle.LruBitmapPool;
import com.andkid.fancy.cache.MemorySizeCalculator;
import com.andkid.fancy.newloader.BasicNetwork;
import com.andkid.fancy.newloader.DiskBasedCache;
import com.andkid.fancy.newloader.HurlStack;
import com.andkid.fancy.newloader.implement.Network;
import com.andkid.fancy.loader.RequestDispatcher;

import java.io.File;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class FancyContext {

    private static final String DEFAULT_CACHE_DIR = "fanli_image";
    public static BitmapPool bitmapPool;
    public static RequestDispatcher requestDispatcher;
    private MemorySizeCalculator memorySizeCalculator;

    FancyContext(Context context) {
        memorySizeCalculator = new MemorySizeCalculator.Builder(context).build();
        bitmapPool = new LruBitmapPool(memorySizeCalculator.getBitmapPoolSize());
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        Network network = new BasicNetwork(new HurlStack());
        requestDispatcher = new RequestDispatcher(new DiskBasedCache(cacheDir), network);
    }

}
