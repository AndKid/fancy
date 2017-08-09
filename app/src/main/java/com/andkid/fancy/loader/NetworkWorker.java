
package com.andkid.fancy.loader;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;

import com.andkid.fancy.newloader.FanliImageError;
import com.andkid.fancy.newloader.implement.Cache;
import com.andkid.fancy.newloader.implement.Network;
import com.andkid.fancy.newloader.implement.ResponseDelivery;
import com.andkid.fancy.request.Request;

import java.util.concurrent.BlockingQueue;

public class NetworkWorker extends Thread {
    private final BlockingQueue<Request> mQueue;
    private final Network mNetwork;
    private final Cache mCache;
    private final ResponseDelivery mDelivery;
    private volatile boolean mQuit = false;

    public NetworkWorker(BlockingQueue<Request> queue,
                         Network network, Cache cache,
                         ResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void addTrafficStatsTag(Request request) {
        // Tag the request (if API >= 14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            Request request;
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
//                request.addMarker("network-queue-take");

                if (request.isCanceled()) {
//                    request.finishByCancel("done-network-discard-cancelled");
                    continue;
                }

                addTrafficStatsTag(request);

//                NetworkResponse networkResponse = mNetwork.performRequest(request);
//                request.addMarker("network-http-complete");

//                Response<?> response = request.parseNetworkResponse(networkResponse);
//                request.addMarker("network-parse-complete");

                // Write to cache if applicable.
                // TODO: Only update cache metadata instead of entire record for 304s.
//                if (request.getRequestConfig().needSave && response.cacheEntry != null) {
//                    mCache.put(request.getCacheKey(), response.cacheEntry);
//                    request.addMarker("network-cache-written");
//                }

//                mDelivery.postResponse(request, response);
//            } catch (FanliImageError fanliImageError) {
//                fanliImageError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
//                parseAndDeliverNetworkError(request, fanliImageError);
            } catch (Exception e) {
//                FanliImageLog.e(e, "Unhandled exception %s", e.toString());
//                FanliImageError fanliImageError = new FanliImageError(e, FanliImageError.Type.NONE);
//                fanliImageError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
//                mDelivery.postError(request, fanliImageError);
            }
        }
    }

    private void parseAndDeliverNetworkError(Request request, FanliImageError error) {
//        error = request.parseNetworkError(error);
//        mDelivery.postError(request, error);
    }
}
