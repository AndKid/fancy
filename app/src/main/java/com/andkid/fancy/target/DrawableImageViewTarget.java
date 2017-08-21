package com.andkid.fancy.target;

import android.widget.ImageView;

import com.andkid.fancy.resource.DrawableResource;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class DrawableImageViewTarget extends ImageViewTarget<DrawableResource<?>> {

    private ImageView imageView;
    private DrawableResource<?> drawableResource;

    public DrawableImageViewTarget(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void setPlaceHolder(int resId) {
        imageView.setImageResource(resId);
    }

    @Override
    public void setResource(DrawableResource<?> resource) {
        drawableResource = resource;
        imageView.setImageDrawable(resource.get());
    }

    @Override
    public DrawableResource<?> getResource() {
        return null;
    }

    @Override
    public void clear() {
        imageView = null;
        drawableResource.release();
        drawableResource = null;
    }

}
