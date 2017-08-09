package com.andkid.fancy.request;

/**
 * Created by yuguan.chen on 2017/7/12.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.andkid.fancy.FancyContext;
import com.andkid.fancy.newloader.LifeCycleCallback;
import com.andkid.fancy.util.Util;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 通过关联一个activity/fragment的生命周期来管理其中的请求
 */
public class RequestManager implements LifeCycleCallback {

    private final Set<Request> requests =
            Collections.newSetFromMap(new WeakHashMap<Request, Boolean>());
    private volatile boolean isPaused;

    public void addRequest(Request request) {
        requests.add(request);
    }

    public void runRequest(Request request) {
        FancyContext.requestDispatcher.add(request);
        addRequest(request);
        request.begin();
    }

    public void removeRequest(Request request) {
        if (request == null)
            return;
        request.clear();
        requests.remove(request);
    }

    public void resume() {
        for (Request request : Util.getSnapshot(requests)) {
//            request.resume();
        }
        isPaused = false;
    }

    public void pause() {
        isPaused = true;
        for (Request request : Util.getSnapshot(requests)) {
            request.pause();
        }
    }

    public void clearAll() {
        for (Request request : Util.getSnapshot(requests)) {
            request.clear();
            removeRequest(request);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public RequestBuilder<Drawable> from(String url) {
        return asDrawable().from(url);
    }

    public RequestBuilder<Drawable> asDrawable() {
        return as(Drawable.class);
    }

    public RequestBuilder<Bitmap> asBitmap() {
        return as(Bitmap.class);
    }

    public RequestBuilder<GifDrawable> asGif() {
        return as(GifDrawable.class);
    }

    public RequestBuilder<File> asFile() {
        return as(File.class);
    }

    public <ResourceType> RequestBuilder<ResourceType> as(Class<ResourceType> resourceClass) {
        return new RequestBuilder<>(this, resourceClass);
    }

    @Override
    public void onStart() {
        if (isPaused) {
            resume();
        }
    }

    @Override
    public void onStop() {
        if (!isPaused) {
            pause();
        }
    }

    @Override
    public void onDestroy() {
        clearAll();
    }

}
