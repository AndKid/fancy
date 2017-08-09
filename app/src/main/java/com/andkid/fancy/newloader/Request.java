package com.andkid.fancy.newloader;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.andkid.fancy.newloader.implement.Cache;
import com.andkid.fancy.newloader.implement.RetryPolicy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public abstract class Request<T> implements Comparable<Request<T>> {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    private final FanliImageLog.MarkerLog mEventLog = FanliImageLog.MarkerLog.ENABLED ? new FanliImageLog.MarkerLog() : null;
    private final String mUrl;
    private final int mDefaultTrafficStatsTag;
    private Integer mSequence;
    private boolean mCanceled = false;
    private boolean mShouldRetryServerErrors = false;
    private RetryPolicy mRetryPolicy;
    private Cache.Entry mCacheEntry = null;
    private Object mTag;
    private final int mMethod;
    protected ImageRequestConfig.Priority mPriority;
    protected ImageRequestConfig mConfig;

    private ArrayList<RequestOnFinishListener<T>> mFinishListeners = new ArrayList<RequestOnFinishListener<T>>();


    public interface RequestOnFinishListener<T> {
        /**
         * 当一个request被完成，回调给需要的地方
         **/
        void onRequestFinish(Request<T> request, boolean isCancelFinish, Response<T> response);
    }

    public Request(int method, String url) {
        this.mMethod = method;
        mUrl = url;
        setRetryPolicy(new DefaultRetryPolicy());
        mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(url);
    }

    public void addFinishListener(RequestOnFinishListener listener) {
        mFinishListeners.add(listener);
    }

    public Request<?> setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public Object getTag() {
        return mTag;
    }

    public int getMethod() {
        return mMethod;
    }

    public ImageRequestConfig getRequestConfig() {
        return mConfig;
    }

    public int getTrafficStatsTag() {
        return mDefaultTrafficStatsTag;
    }

    private static int findDefaultTrafficStatsTag(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String host = uri.getHost();
                if (host != null) {
                    return host.hashCode();
                }
            }
        }
        return 0;
    }

    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
    }

    public void addMarker(String tag) {
        if (FanliImageLog.MarkerLog.ENABLED) {
            mEventLog.add(tag, Thread.currentThread().getId());
        }
    }

    public void finishByCancel(String tag) {
        this.finish(true, null, tag);
    }

    public void finishByComplete(@NonNull Response response) {
        this.finish(false, response, "done");
    }

    private void finish(final boolean isCancelFinish, final Response response, final String tag) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            doFinish(isCancelFinish, response, tag);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    doFinish(isCancelFinish, response, tag);
                }
            });
        }
    }

    private void doFinish(boolean isCancelFinish, Response response, String tag) {
        for (RequestOnFinishListener listener : mFinishListeners) {
            listener.onRequestFinish(Request.this, isCancelFinish, response);
        }
        if (FanliImageLog.MarkerLog.ENABLED) {
            finishLog(tag);
        }
    }

    public void finishLog(String tag) {
        final long threadId = Thread.currentThread().getId();
        mEventLog.add(tag, threadId);
        mEventLog.finish(this.toString());
    }

    public final Request<?> setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getCacheKey() {
        return getUrl();
    }

    public Request<?> setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    public void cancel() {
        mCanceled = true;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public Map<String, String> getHeaders() throws FanliImageError {
        return Collections.emptyMap();
    }

    @Deprecated
    protected Map<String, String> getPostParams() throws FanliImageError {
        return getParams();
    }

    @Deprecated
    protected String getPostParamsEncoding() {
        return getParamsEncoding();
    }

    @Deprecated
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    @Deprecated
    public byte[] getPostBody() throws FanliImageError {
        Map<String, String> postParams = getPostParams();
        if (postParams != null && postParams.size() > 0) {
            return encodeParameters(postParams, getPostParamsEncoding());
        }
        return null;
    }

    protected Map<String, String> getParams() throws FanliImageError {
        return null;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public byte[] getBody() throws FanliImageError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public final Request<?> setShouldRetryServerErrors(boolean shouldRetryServerErrors) {
        mShouldRetryServerErrors = shouldRetryServerErrors;
        return this;
    }

    public final boolean shouldRetryServerErrors() {
        return mShouldRetryServerErrors;
    }

    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public ImageRequestConfig.Priority getPriority() {
        return mPriority == null ? ImageRequestConfig.Priority.LOW : mPriority;
    }

    public void setPriority(ImageRequestConfig.Priority priority) {
        mPriority = priority;
    }

    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }

    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    protected FanliImageError parseNetworkError(FanliImageError fanliImageError) {
        return fanliImageError;
    }

    @Override
    public int compareTo(Request<T> other) {
        ImageRequestConfig.Priority left = this.getPriority();
        ImageRequestConfig.Priority right = other.getPriority();

        //先用优先级排序，优先级一样的情况下，后进先出
        return left == right ?
                other.mSequence - this.mSequence :
                right.ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag + " "
                + getPriority() + " " + mSequence;
    }
}
