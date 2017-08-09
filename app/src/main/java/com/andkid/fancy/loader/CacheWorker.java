package com.andkid.fancy.loader;

import android.os.Process;

import com.andkid.fancy.newloader.implement.Cache;
import com.andkid.fancy.newloader.implement.ResponseDelivery;
import com.andkid.fancy.request.Request;

import java.util.concurrent.BlockingQueue;

public class CacheWorker extends Thread {


    private final BlockingQueue<Request> mCacheQueue;

    private final BlockingQueue<Request> mNetworkQueue;

    private final Cache mCache;

//    private final ResponseDelivery mDelivery;

    private volatile boolean mQuit = false;

    public CacheWorker(
            BlockingQueue<Request> cacheQueue, BlockingQueue<Request> networkQueue,
            Cache cache, ResponseDelivery delivery) {
        mCacheQueue = cacheQueue;
        mNetworkQueue = networkQueue;
        mCache = cache;
//        mDelivery = delivery;
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mCache.initialize();

        while (true) {
            try {
                final Request request = mCacheQueue.take();
//                request.addMarker("cache-queue-take");

                if (request.isCanceled()) {
//                    request.finishByCancel("done-cache-discard-cancelled");
                    continue;
                }

                Cache.Entry entry = mCache.get(request.getCacheKey());
                if (entry == null) {
                    //如果本地缓存没有
//                    request.addMarker("cache-miss");
                    delivery2Network(request);
                    continue;
                }

                if (entry.isExpired()) {
                    //如果已经过期失效，重新请求获取
//                    request.addMarker("cache-hit-expired");
//                    request.setCacheEntry(entry);
                    delivery2Network(request);
                    continue;
                }

                if (entry.refreshNeeded()) {
//                    request.addMarker("cache-hit-refresh-needed");
//                    request.setCacheEntry(entry);
                    delivery2Network(request);
                    continue;
                }

//                request.addMarker("cache-hit");
//                Response<?> response = request.parseNetworkResponse(new NetworkResponse(entry.data, entry.responseHeaders));
//                request.addMarker("cache-hit-parsed");
//                mDelivery.postResponse(request, response);

            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }
        }
    }

    private void delivery2Network(Request request) throws InterruptedException {
//        if (request.getRequestConfig().canGetFromRemote) {
//            mNetworkQueue.put(request);
//        } else {
//            mDelivery.postError(request, new FanliImageError("can not get from remote__", FanliImageError.Type.NONE));
//        }
        //TODO Strategy
        mNetworkQueue.put(request);
    }
}
