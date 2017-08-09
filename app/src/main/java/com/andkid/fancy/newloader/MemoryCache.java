package com.andkid.fancy.newloader;

import android.app.ActivityManager;
import android.content.Context;
import android.util.LruCache;

import com.andkid.fancy.newloader.implement.ImageData;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

public class MemoryCache implements FanliImageManager.ImageCache {
    //开辟8M硬缓存空间
    protected static final int SOFT_CACHE_CAPACITY = 20;
    protected int hardCachedSize;
    protected LruCache<String, ImageData> hardCache;
    protected LinkedHashMap<String, SoftReference<ImageData>> softCache;
    protected boolean clearAll;

    public MemoryCache(Context context) {
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache. 
        hardCachedSize = 1024 * 1024 * memClass / 8;

        hardCache = new LruCache<String, ImageData>(hardCachedSize) {
            @Override
            public int sizeOf(String key, ImageData value) {
                if (value == null) {
                    return 0;
                }
                return value.computeSize();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, ImageData oldValue, ImageData newValue) {
                synchronized (this) {
                    if (!clearAll) {
                        softCache.put(key, new SoftReference<ImageData>(oldValue));
                    }
                }
            }
        };

        softCache = new LinkedHashMap<String, SoftReference<ImageData>>(SOFT_CACHE_CAPACITY, 0.75f, true) {
            @Override
            public SoftReference<ImageData> put(String key, SoftReference<ImageData> value) {
                return super.put(key, value);
            }

            @Override
            protected boolean removeEldestEntry(Entry<String, SoftReference<ImageData>> eldest) {
                if (size() > SOFT_CACHE_CAPACITY) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void put(String key, ImageData data) {
        if (data != null && key != null) {
            if (get(key) != null) {
                return;
            }
            clearAll = false;
            synchronized (hardCache) {
                if (hardCache.get(key) == null) {
                    hardCache.put(key, data);
                }
            }
        }
    }

    @Override
    public ImageData get(String key) {
        if (key == null) {
            return null;
        }
        synchronized (hardCache) {
            final ImageData imageData = hardCache.get(key);
            if (imageData != null) {
                return imageData;
            }
        }
        synchronized (softCache) {
            SoftReference<ImageData> imgReference = softCache.get(key);
            if (imgReference != null) {
                final ImageData imageData = imgReference.get();
                if (imageData != null) {
                    return imageData;
                } else {
                    softCache.remove(key);
                }
            }
        }
        return null;
    }

    public void clear() {
        try {
            if (hardCache != null) {
                synchronized (hardCache) {
                    clearAll = true;
                    hardCache.evictAll();
                }
            }
            if (softCache != null) {
                synchronized (softCache) {
                    if (softCache.isEmpty()) {
                        return;
                    }
                    softCache.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
