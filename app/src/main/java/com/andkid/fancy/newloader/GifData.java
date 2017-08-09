package com.andkid.fancy.newloader;

import android.util.Log;
import android.widget.ImageView;

import com.andkid.fancy.newloader.implement.ImageData;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by liuyan on 2017/7/10.
 */

public class GifData implements ImageData {
    private GifDrawable mGifDrawable;

    public GifData(GifDrawable mGifDrawable) {
        this.mGifDrawable = mGifDrawable;
    }

    @Override
    public int computeSize() {
        if (mGifDrawable == null) {
            return 0;
        }
        return (int) (mGifDrawable.getAllocationByteCount());
    }

    @Override
    public boolean isEmpty() {
        return mGifDrawable == null;
    }

    @Override
    public void displayContent(ImageView imageView, ImageRequestConfig config, boolean isImmediate) {
        Log.d("fanli_image", "GifData displayContent___");
        imageView.setImageDrawable(mGifDrawable);
    }
}
