package com.andkid.fancy.newloader.implement;

import android.widget.ImageView;

import com.andkid.fancy.newloader.ImageRequestConfig;

/**
 * Created by liuyan on 2017/7/10.
 */

public interface ImageData {

    /**
     * 计算对象大小
     *
     * @return
     */
    int computeSize();

    boolean isEmpty();

    void displayContent(ImageView imageView, ImageRequestConfig config, boolean isImmediate);
}
