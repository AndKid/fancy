package com.andkid.fancy.newloader;

/**
 * Created by liuyan on 2017/7/6.
 */

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.andkid.fancy.newloader.implement.ImageData;
import com.andkid.fancy.newloader.implement.Network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyan on 2017/7/5.
 */

public class FanliImageManager {
    private static final String TAG = "FANLI_IMAGE";
    private static final String DEFAULT_CACHE_DIR = "fanli_image";
    private static FanliImageManager instance;
    private RequestQueue mRequestQueue;
    private ImageCache mMemoryCache;

    private final Map<String, Map<Request, ImageListener>> mWaitingRequests = new HashMap<String, Map<Request, ImageListener>>();

    /**
     * 只在主线程调用，所以不做同步
     **/
    public static FanliImageManager getInstance(Context context) {
        if (instance == null) {
            instance = new FanliImageManager(context);
        }
        return instance;
    }

    private FanliImageManager(Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        mRequestQueue.start();
        mMemoryCache = new MemoryCache(context);
    }

    public void stop() {
        mRequestQueue.stop();
        instance = null;
    }

    Request displayImage(ImageView imageView, String url, ImageRequestConfig config, ImageListener imageListener) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Log.d(TAG, "displayImage url = " + url);
        throwIfNotOnMainThread();
        if (config == null) {
            config = new ImageRequestConfig();
        }

        String memoryCacheKey = getMemoryCacheKey(url, config);
        String requestCacheKey = getRequestCacheKey(url, config);
        Request<ImageData> newRequest = new FanliImageRequest(url, config);

        if (imageListener == null) {
            imageListener = getImageListener(imageView, config, newRequest);
        }
        imageListener.onStart();

        if (config.canGetFromMemory) {
            ImageData imageData = mMemoryCache.get(memoryCacheKey);
            if (imageData != null) {
                Log.d(TAG, "git it from memory suc");
                newRequest.finishLog("done-git-from-memory");
                imageListener.onResponse(imageData, true);
                return null;
            }
        }

        if (mWaitingRequests.containsKey(requestCacheKey)) {
            Log.d(TAG, "contained in mWaitingRequests url = " + newRequest.getUrl());
            mWaitingRequests.get(requestCacheKey).put(newRequest, imageListener);
            newRequest.addMarker("be-represented-by-same");
        } else {
            Log.d(TAG, "do not contained in mWaitingRequests url = " + newRequest.getUrl());
            Map<Request, ImageListener> map = new HashMap<Request, ImageListener>();
            map.put(newRequest, imageListener);
            mWaitingRequests.put(requestCacheKey, map);
            setFinishlListener(newRequest);
            mRequestQueue.add(newRequest);
        }
        return newRequest;
    }

    private void setFinishlListener(Request<ImageData> request) {
        request.addFinishListener(new Request.RequestOnFinishListener<ImageData>() {
            /**
             * 主线程回调,cancel了取消了执行的任务一定要走onErrorResponse
             * **/
            @Override
            public void onRequestFinish(Request<ImageData> request, boolean isCancelFinish, Response<ImageData> response) {
                if (isCancelFinish) {
                    handleCancelFinish(request);
                } else {
                    handleCompletedFinish(request, response);
                }
            }
        });
    }

    private void handleCompletedFinish(Request<ImageData> comRequest, Response<ImageData> response) {
        String requestCacheKey = getRequestCacheKey(comRequest.getUrl(), comRequest.getRequestConfig());
        Map<Request, ImageListener> map = mWaitingRequests.remove(requestCacheKey);

        if  (response.isSuccess()) {
            String memoryCacheKey = getMemoryCacheKey(comRequest.getUrl(), comRequest.getRequestConfig());
            mMemoryCache.put(memoryCacheKey, response.result);
            for (Map.Entry<Request, ImageListener> entry : map.entrySet()) {
                Request request = entry.getKey();
                if (request != comRequest) {
                    request.finishLog("done-represented-suc");
                }
                ImageListener imageListener = entry.getValue();
                imageListener.onResponse(response.result, false);
            }
        } else {
            for (Map.Entry<Request, ImageListener> entry : map.entrySet()) {
                Request request = entry.getKey();
                if (request != comRequest) {
                    request.finishLog("done-represented-error");
                }
                ImageListener imageListener = entry.getValue();
                imageListener.onErrorResponse(response.error);
            }
        }
    }

    private void handleCancelFinish(Request<ImageData> request) {
        String requestCacheKey = getRequestCacheKey(request.getUrl(), request.getRequestConfig());
        Map<Request, ImageListener> map = mWaitingRequests.get(requestCacheKey);
        if (map == null) {
            return;
        }
        ImageListener imageListener = map.remove(request); //移除被cancel的当前request
        imageListener.onErrorResponse(new FanliImageError("request canceled error", FanliImageError.Type.CANCEL_ERROR));

        if (map.isEmpty()) {
            mWaitingRequests.remove(requestCacheKey);
            return;
        }
        Request otherRequest = null;
        for (Request r : map.keySet()) {
            if (!r.isCanceled()) {
                otherRequest = r;
                break;
            }
        }
        if (otherRequest != null) {
            setFinishlListener(otherRequest); //为新出发的request添加listener
            mRequestQueue.add(otherRequest);
        }
    }

    public ImageListener getImageListener(final ImageView view, final ImageRequestConfig config, final Request<ImageData> newRequest) {
        return new ImageListener() {

            @Override
            public void onStart() {
                if (view == null) { //如果不传imageview也没关系
                    return;
                }
                cancelOldRequest(view);

                view.setTag(newRequest);
                if (view.getDrawable() != null) {
                    view.setImageDrawable(null);
                }
                if (config.defaultImageResId != 0) {
                    view.setImageResource(config.defaultImageResId);
                }
            }

            @Override
            public void onErrorResponse(FanliImageError error) {
                if (view == null) {
                    return;
                }
                if (view.getTag() != newRequest) {
                    return; //这个imageview，设置了新的图片
                }
                if (config.errorImageResId != 0) {
                    view.setImageResource(config.errorImageResId);
                }
                view.setTag(null);
            }

            @Override
            public void onResponse(ImageData imageData, boolean isImmediate) {
                if (view == null) {
                    return;
                }
                if (view.getTag() != newRequest) {
                    return; //这个imageview，设置了新的图片
                }
                if (imageData != null) {
                    imageData.displayContent(view, config, isImmediate);
                }
                view.setTag(null);
            }
        };
    }

    /**
     * cancel掉这个imageview上的之前的请求
     **/
    private void cancelOldRequest(ImageView view) {
        Request oldRequest = (Request) view.getTag();
        if (oldRequest != null) {
            Log.d(TAG, "cancelOldRequest URL = " + oldRequest.toString());
            oldRequest.addMarker("imageview-new-request_cancel");
            oldRequest.cancel();
        }
    }

    public void removeCanceledRequests(ArrayList<Request<ImageData>> mRequests) {
        for (Request request : mRequests) {
            request.cancel();
            request.setPriority(ImageRequestConfig.Priority.IMMEDIATE); //设置为最高优先级，在有新请求添加到RequestQueue的时候会触发排序，放在最前面最快释放request引用

            /**一些重用的请求并没有进入mRequestQueue，而是在mWaitingRequests中等待，删除mWaitingRequests对它的引用**/
            String requestCacheKey = getRequestCacheKey(request.getUrl(), request.getRequestConfig());
            Map<Request, ImageListener> map = mWaitingRequests.get(requestCacheKey);
            if (map != null) {
                ImageListener imageListener = map.remove(request);
                if (imageListener != null) {
                    imageListener.onErrorResponse(new FanliImageError("request canceled error", FanliImageError.Type.CANCEL_ERROR));
                }
                if (map.isEmpty()) {
                    mWaitingRequests.remove(requestCacheKey);
                }
            }
        }
    }

    /**
     * MemoryCache 不关心canGetFromRemote和canGetFromDisk
     */
    private static String getMemoryCacheKey(String url, ImageRequestConfig config) {
        return new StringBuilder(url.length() + 12).append("#W").append(config.maxWidth)
                .append("#H").append(config.maxHeight).append("#S").append(config.scaleType.ordinal()).append(url)
                .toString();
    }

    /**
     * 考虑anGetFromRemote和canGetFromDisk，如果2个request这两个属性不同，可能最后拿到结果的方式不同
     * 所以anGetFromRemote和canGetFromDisk都相同才做去重
     **/
    private static String getRequestCacheKey(String url, ImageRequestConfig config) {
        return new StringBuilder(url.length() + 12).append("#W").append(config.maxWidth)
                .append("#H").append(config.maxHeight).append("#S").append(config.scaleType.ordinal())
                .append("#D").append(config.canGetFromDisk).append("#R").append(config.canGetFromRemote).append(url)
                .toString();
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be from the main thread.");
        }
    }

    public interface ImageCache {
        ImageData get(String url);

        void put(String url, ImageData bitmap);
    }

    public interface ImageListener {

        void onStart();

        void onResponse(ImageData imageData, boolean isImmediate);

        void onErrorResponse(FanliImageError error);
    }

}

