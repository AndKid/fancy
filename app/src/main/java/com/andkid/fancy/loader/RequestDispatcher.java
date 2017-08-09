package com.andkid.fancy.loader;

import android.os.Handler;
import android.os.Looper;

import com.andkid.fancy.newloader.ExecutorDelivery;
import com.andkid.fancy.newloader.implement.Cache;
import com.andkid.fancy.newloader.implement.Network;
import com.andkid.fancy.newloader.implement.ResponseDelivery;
import com.andkid.fancy.request.Request;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestDispatcher {

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    private final Cache mCache;

    private final Network mNetwork;

    private final ResponseDelivery mDelivery;

    private NetworkWorker[] mDispatchers;

    private CacheWorker mCacheDispatcher;

    private final PriorityBlockingQueue<Request> mCacheQueue = new PriorityBlockingQueue<>(64);

    private final PriorityBlockingQueue<Request> mNetworkQueue = new PriorityBlockingQueue<>(64);

    public RequestDispatcher(Cache cache, Network network, int threadPoolSize,
                             ResponseDelivery delivery) {
        mCache = cache;
        mNetwork = network;
        mDispatchers = new NetworkWorker[threadPoolSize];
        mDelivery = delivery;
    }

    public RequestDispatcher(Cache cache, Network network, int threadPoolSize) {
        this(cache, network, threadPoolSize,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    public RequestDispatcher(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public void start() {
        stop();
        mCacheDispatcher = new CacheWorker(mCacheQueue, mNetworkQueue, mCache, mDelivery);
        mCacheDispatcher.start();

        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkWorker networkDispatcher = new NetworkWorker(mNetworkQueue, mNetwork,
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

    public Cache getCache() {
        return mCache;
    }

    public void add(Request request) {
        //TODO Strategy
        mCacheQueue.add(request);
    }

}
