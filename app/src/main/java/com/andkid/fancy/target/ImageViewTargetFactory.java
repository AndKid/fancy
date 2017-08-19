package com.andkid.fancy.target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by yuguan.chen on 2017/7/17.
 */

public class ImageViewTargetFactory {

    public static <ResourceType> Target<?> buildTarget(ImageView view, Class<ResourceType> resourceClass) {
        if (Bitmap.class.equals(resourceClass)) {
            return new BitmapImageViewTarget(view);
        } else if (Drawable.class.isAssignableFrom(resourceClass)) {
            return new DrawableImageViewTarget(view);
        } else {
            throw new IllegalArgumentException(
                    "Unhandled class: " + resourceClass + ", try .as*(Class).transcode(ResourceTranscoder)");
        }
    }

}
