package com.andkid.fancy.newloader;


import com.andkid.fancy.newloader.implement.ImageData;

/**
 * Created by liuyan on 2017/7/6.
 */

public class FanliImageRequest extends Request<ImageData> {

    public static final int DEFAULT_IMAGE_TIMEOUT_MS = 1000;
    public static final int DEFAULT_IMAGE_MAX_RETRIES = 2;
    public static final float DEFAULT_IMAGE_BACKOFF_MULT = 2f;

    private static final Object sDecodeLock = new Object();

    public FanliImageRequest(String url, ImageRequestConfig config) {
        super(Method.GET, url);
        this.mConfig = config;
        this.mPriority = config.priority;
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_IMAGE_TIMEOUT_MS, DEFAULT_IMAGE_MAX_RETRIES, DEFAULT_IMAGE_BACKOFF_MULT));
    }

    @Override
    protected Response<ImageData> parseNetworkResponse(NetworkResponse response) {
        synchronized (sDecodeLock) {
            ImageData imageData;
            try {
                imageData = ImageDecoder.decodeFromBytes(response.data, mConfig);
                if (imageData.isEmpty()) {
                    return Response.error(new FanliImageError(response, FanliImageError.Type.PARSE_ERROR));
                } else {
                    return Response.success(imageData, HttpHeaderParser.parseCacheHeaders(response));
                }
            } catch (OutOfMemoryError e) {
                return Response.error(new FanliImageError(e, FanliImageError.Type.PARSE_ERROR));
            }
        }
    }

}
