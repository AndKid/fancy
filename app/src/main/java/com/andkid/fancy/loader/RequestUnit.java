package com.andkid.fancy.loader;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.andkid.fancy.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuguan.chen on 2017/7/18.
 */

public class RequestUnit implements LoadCallback, Comparable<RequestUnit> {

    public enum DataSource {
        MEMORY,
        DISK,
        REMOTE
    }

    public interface JobListener {
        void onJobFinish(LoadKey loadKey);
    }

    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;
    private static final Handler MAIN_THREAD_HANDLER =
            new Handler(Looper.getMainLooper(), new MainThreadCallback());
    private LoadKey loadKey;
    private List<ResourceCallback> cbs = new ArrayList<>(2);
    private String url;
    private int width;
    private int height;
    private int priority;
    private long order;

    private Resource resource;
    private DataSource dataSource;

    public RequestUnit(ResourceCallback callback, LoadKey loadKey, String url, int width, int height, int priority, long order) {
        cbs.add(callback);
        this.loadKey = loadKey;
        this.url = url;
        this.width = width;
        this.height = height;
        this.priority = priority;
        this.order = order;
    }

    @Override
    public int compareTo(@NonNull RequestUnit o) {
        int result = this.priority - o.priority;
        if (result != 0) {
            return result;
        } else if (this.order == o.order) {
            return 0;
        } else {
            return (this.order > o.order) ? -1 : 1;
        }
    }

    public void addCallback(ResourceCallback callback, int priority, long order) {
        if (this.priority > priority)
            this.priority = priority;
        if (this.order < order)
            this.order = order;
        cbs.add(callback);
    }

    @Override
    public void onResourceReady(Resource resource, DataSource dataSource) {
        this.resource = resource;
        this.dataSource = dataSource;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_COMPLETE, this).sendToTarget();
    }

    @Override
    public void onLoadFailed() {
        MAIN_THREAD_HANDLER.obtainMessage(MSG_EXCEPTION, this).sendToTarget();
    }

    public void handleResultOnMainThread() {
        notifyResourceReady(resource, dataSource);
    }

    public void handleExceptionOnMainThread() {
        notifyLoadFailed();
    }

    public void notifyResourceReady(Resource resource, DataSource dataSource) {
        for (ResourceCallback callback : cbs) {
            callback.onResourceReady(resource, dataSource);
        }
    }

    public void notifyLoadFailed() {
        for (ResourceCallback callback : cbs) {
            callback.onLoadFailed();
        }
    }

    private static class MainThreadCallback implements Handler.Callback {

        MainThreadCallback() { }

        @Override
        public boolean handleMessage(Message message) {
            RequestUnit job = (RequestUnit) message.obj;
            switch (message.what) {
                case MSG_COMPLETE:
                    job.handleResultOnMainThread();
                    break;
                case MSG_EXCEPTION:
                    job.handleExceptionOnMainThread();
                    break;
                default:
                    throw new IllegalStateException("Unrecognized message: " + message.what);
            }
            return true;
        }
    }
}
