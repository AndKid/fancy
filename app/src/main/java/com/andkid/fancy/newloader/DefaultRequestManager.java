package com.andkid.fancy.newloader;

import android.content.Context;
import android.widget.ImageView;

import com.andkid.fancy.newloader.implement.RequestManager;

/**
 * Created by liuyan on 2017/7/11.
 */

public class DefaultRequestManager implements RequestManager {

    private Context mContext;

    public DefaultRequestManager(Context context) {
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
        FanliImageManager.getInstance(mContext).displayImage(imageView, url, config, imageListener);
    }

    @Override
    public void onDestroy() {
        //do nothing
    }
}
