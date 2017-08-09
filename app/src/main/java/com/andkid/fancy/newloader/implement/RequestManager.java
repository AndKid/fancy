package com.andkid.fancy.newloader.implement;

import android.widget.ImageView;

import com.andkid.fancy.newloader.FanliImageManager;
import com.andkid.fancy.newloader.ImageRequestConfig;

/**
 * Created by liuyan on 2017/7/11.
 */

public interface RequestManager {

    void displayImage(ImageView imageView, String url);

    void displayImage(ImageView imageView, String url, ImageRequestConfig config);

    void displayImage(ImageView imageView, String url, FanliImageManager.ImageListener listener);

    void displayImage(ImageView imageView, String url, ImageRequestConfig config, FanliImageManager.ImageListener imageListener);

    void onDestroy();
}
