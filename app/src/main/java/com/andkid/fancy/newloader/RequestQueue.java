package com.andkid.fancy.newloader;

import android.os.Handler;
import android.os.Looper;

import com.andkid.fancy.newloader.implement.Cache;
import com.andkid.fancy.newloader.implement.Network;
import com.andkid.fancy.newloader.implement.ResponseDelivery;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestQueue {

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    private final Cache mCache;

    private final Network mNetwork;

    private final ResponseDelivery mDelivery;

    private NetworkDispatcher[] mDispatchers;

    private CacheDispatcher mCacheDispatcher;

    private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue<Request<?>>(64, new Comparator<Request<?>>() {
        @Override
        public int compare(Request r1, Request r2) {
            return r1.compareTo(r2);
        }
    });

    private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<Request<?>>(64, new Comparator<Request<?>>() {
        @Override
        public int compare(Request r1, Request r2) {
            return r1.compareTo(r2);
        }
    });

    public RequestQueue(Cache cache, Network network, int threadPoolSize,
                        ResponseDelivery delivery) {
        mCache = cache;
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[threadPoolSize];
        mDelivery = delivery;
    }

    public RequestQueue(Cache cache, Network network, int threadPoolSize) {
        this(cache, network, threadPoolSize,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    public RequestQueue(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public void start() {
        stop();
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
        mCacheDispatcher.start();

        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork,
                    mCache, mDelivery);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    public void stop() {
        if (mCacheDispatcher != null) {
            mCacheDispatcher.quit();
        }
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    public Cache getCache() {
        return mCache;
    }


    public <T> Request<T> add(Request<T> request) {
        request.setSequence(getSequenceNumber());
        request.addMarker("add-to-queue url = " + request.getUrl());

        if (!request.getRequestConfig().canGetFromDisk) {
            mNetworkQueue.add(request);
            return request;
        }

        mCacheQueue.add(request);
        return request;

    }

}
