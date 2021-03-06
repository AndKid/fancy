package com.andkid.fancy.request;

import com.andkid.fancy.load.DataSource;
import com.andkid.fancy.load.ResourceCallback;
import com.andkid.fancy.resource.Resource;
import com.andkid.fancy.target.NoTarget;
import com.andkid.fancy.target.Target;

/**
 * Created by yuguan.chen on 2017/7/12.
 */

public class Request<R> implements ResourceCallback {

    private enum Status {
        /**
         * Created but not yet running.
         */
        PENDING,
        /**
         * In the process of fetching media.
         */
        RUNNING,
        /**
         * Waiting for a callback given to the Target to be called to determine target dimensions.
         */
        WAITING_FOR_SIZE,
        /**
         * Finished loading media successfully.
         */
        COMPLETE,
        /**
         * Failed to load media, may be restarted.
         */
        FAILED,
        /**
         * Cancelled by the user, may not be restarted.
         */
        CANCELED,
        /**
         * Cleared by the user with a placeholder set, may not be restarted.
         */
        CLEARED,
        /**
         * Temporarily paused by the system, may be restarted.
         */
        PAUSED,
    }

    public static NoTarget NO_TARGET = new NoTarget();

    private Status status;
    private Target<Resource<R>> target = NO_TARGET;
    public String url;
    public RequestListener listener;
    public boolean crossFade;
    public boolean fitBounds;
    public boolean skipMemoryCache;
    public boolean skipDiskCache;
    public int placeholder;
    public int ratio;
    public int width;
    public int height;
    public int priority;
    public int order;
    public static int ORDER_COUNTER;
    //TODO cache

    public Request() {
        order = ++ORDER_COUNTER;
        status = Status.PENDING;
    }

    public void begin() {
        status = Status.RUNNING;
    }

    public void pause() {
        status = Status.PAUSED;
    }

    public void cancel() {
        status = Status.CANCELED;
    }

    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    public void clear() {
        cancel();
        target.clear();
        listener = null;
        status = Status.CLEARED;
    }

    @Override
    public void onLoadStart() {
        target.setPlaceHolder(placeholder);
    }

    @Override
    public void onResourceReady(Resource<?> resource, DataSource dataSource) {
        listener.onResourceReady(resource);
        if (resource !=null) {
            target.setResource((Resource<R>) resource);
        }
    }

    @Override
    public void onLoadFailed() {

    }

    public void setTarget(Target<Resource<R>> target) {
        if (target == null) {
            this.target = NO_TARGET;
        } else {
            this.target = target;
        }
    }

    public Target<?> getTarget() {
        return target;
    }

    public String getCacheKey() {
        return url;
    }

}
