package com.andkid.fancy.newloader;

import android.content.Context;
import android.widget.ImageView;

import com.andkid.fancy.newloader.implement.ImageData;
import com.andkid.fancy.newloader.implement.RequestManager;

import java.util.ArrayList;

/**
 * Created by liuyan on 2017/7/11.
 */

public class RecycleRequestManager implements RequestManager {

    private ArrayList<Request<ImageData>> mRequests = new ArrayList<Request<ImageData>>();
    private Context mContext;

    public RecycleRequestManager(Context context) {
        this.mContext = context;
    }

    @Override
    public void displayImage(ImageView imageView, String url) {
        this.displayImage(imageView, url, null, null);
    }

    @Override
    public void displayImage(ImageView imageView, String url, ImageRequestConfig config) {
        this.displayImage(imageView, url, config, null);
    }

    @Override
    public void displayImage(ImageView imageView, String url, FanliImageManager.ImageListener listener) {
        this.displayImage(imageView, url, null, listener);
    }

    @Override
    public void displayImage(ImageView imageView, String url, ImageRequestConfig config, FanliImageManager.ImageListener imageListener) {
        Request<ImageData> newRequest = FanliImageManager.getInstance(mContext).displayImage(imageView, url, config, imageListener);
        if (newRequest != null) {
            mRequests.add(newRequest);
            newRequest.addFinishListener(new Request.RequestOnFinishListener<ImageData>() {
                @Override
                public void onRequestFinish(Request<ImageData> request, boolean isCancelFinish, Response<ImageData> response) {
                    mRequests.remove(request); //无论是否是cancel导致的finish，都移除
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        FanliImageManager.getInstance(mContext).removeCanceledRequests(mRequests);
        mRequests.clear();
    }
}
