package com.andkid.fancy.target;

import android.widget.ImageView;

import com.andkid.fancy.resource.BitmapResource;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class BitmapImageViewTarget extends ImageViewTarget<BitmapResource> {

    private ImageView imageView;
    private BitmapResource bitmapResource;

    public BitmapImageViewTarget(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void setPlaceHolder(int resId) {
        imageView.setImageResource(resId);
    }

    @Override
    public void setResource(BitmapResource resource) {
        bitmapResource = resource;
        imageView.setImageBitmap(resource.get());
    }

    @Override
    public BitmapResource getResource() {
        return bitmapResource;
    }

    @Override
    public void clear() {

    }

}
