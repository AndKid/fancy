package com.andkid.fancy.load;

import com.andkid.fancy.request.Request;
import com.andkid.fancy.resource.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by AndKid on 2017/8/20 0020.
 */

public class Loader implements LoadUnit.LoadUnitListener {

    private Map<LoadKey, LoadUnit> unitMap = new HashMap<>();
    private ExecutorService remoteExecutor;
    private ExecutorService diskCacheExecutor;


    public Loader(ExecutorService remoteExecutor, ExecutorService diskCacheExecutor) {
        this.remoteExecutor = remoteExecutor;
        this.diskCacheExecutor = diskCacheExecutor;
    }

    public void load(Request request) {
        LoadKey loadKey = new LoadKey(request.url, request.width, request.height);

        //memory
        Resource<?> resource = loadFromCache(loadKey, request.skipMemoryCache);
        if (resource != null) {
            request.onResourceReady(resource, DataSource.MEMORY);
            return;
        }

        LoadUnit loadUnit = unitMap.get(loadKey);
        if (loadUnit != null) {
            loadUnit.addCallback(request);
            return;
        }

        loadUnit = new LoadUnit(loadKey, request, this);
        LoadTask loadTask = new LoadTask();
        unitMap.put(loadKey, loadUnit);
        if (request.skipDiskCache) {
            remoteExecutor.execute(loadTask);
        } else {
            diskCacheExecutor.execute(loadTask);
        }
    }

    private Resource<?> loadFromCache(LoadKey loadKey, boolean skipMemoryCache) {
        if (skipMemoryCache)
            return null;
        return null;
    }

    @Override
    public void onLoadComplete(LoadKey key) {
        unitMap.remove(key);
    }

}
