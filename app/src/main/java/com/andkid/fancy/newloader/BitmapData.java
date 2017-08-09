package com.andkid.fancy.newloader;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.widget.ImageView;

//import com.andkid.fancy.loader.implement.LeftBottomRoundedDrawable;
//import com.andkid.fancy.loader.implement.RoundedDrawable;
//import com.andkid.fancy.loader.implement.TopRightRoundedDrawable;
//import com.andkid.fancy.loader.implement.TopRoundedDrawable;
import com.andkid.fancy.newloader.implement.ImageData;
//import com.fanli.android.lib.R;

/**
 * Created by liuyan on 2017/7/10.
 */

public class BitmapData implements ImageData {
    private Bitmap mBitmap;

    public BitmapData(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    @Override
    public int computeSize() {
        if (mBitmap == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return mBitmap.getByteCount();
        }
        return mBitmap.getRowBytes() * mBitmap.getHeight();
    }

    @Override
    public boolean isEmpty() {
        return mBitmap == null;
    }

    @Override
    public void displayContent(ImageView imageView, ImageRequestConfig config, boolean isImmediate) {

        if (config != null && config.renderType == ImageRequestConfig.RENDER_TRANSACTION && !isImmediate) {
            displayFade(imageView, config);
        } else {
            displayNormal(imageView, config);
        }
    }

    private void displayFade(ImageView imageView, ImageRequestConfig config) {
        Drawable drawables[] = new Drawable[2];
        if (mBitmap != null) {
            if (config.radius <= 0) {
                drawables[1] = new BitmapDrawable(mBitmap);
            } else {
                drawables[1] = getDrawableByroundType(config.roundType, config.radius);
            }
        } else {
            if (config.hideWhiteDrawable) {
                return;
            }
            // 不能直接缓存whiteDrawable,因为其他ImageView设置该Drawable时会修改其高宽，从而导致其他控件中的Drawable也会改变
//            drawables[1] = imageView.getContext().getResources().getDrawable(R.drawable.drawable_white);
        }

        Drawable placeHolder = imageView.getDrawable();
        if (placeHolder == null) {
            drawables[0] = new ColorDrawable(imageView.getContext().getResources().getColor(android.R.color.transparent));
        } else {
            drawables[0] = placeHolder;
        }
        TransitionDrawable crossFade = new TransitionDrawable(drawables);
        crossFade.setCrossFadeEnabled(true);
        imageView.setImageDrawable(crossFade);
        crossFade.startTransition(500);
    }

    private void displayNormal(ImageView imageView, ImageRequestConfig config) {
        if (mBitmap != null) {
            if (config == null || config.radius <= 0) {
                imageView.setImageBitmap(mBitmap);
            } else {
                imageView.setImageDrawable(getDrawableByroundType(config.roundType, config.radius));
            }
        } else {
            if (config != null && config.hideWhiteDrawable) {
                return;
            }
            // 不能直接缓存whiteDrawable,因为其他ImageView设置该Drawable时会修改其高宽，从而导致其他控件中的Drawable也会改变
//            imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.drawable_white));
        }
    }

    private Drawable getDrawableByroundType(int roundType, int radius) {

        Drawable drawable = null;
//        switch (roundType) {
//            case ImageRequestConfig.ROUND_TYPE_FULL:
//                drawable = new RoundedDrawable(mBitmap, radius);
//                break;
//            case ImageRequestConfig.ROUND_TYPE_LEFT_RIGHT:
//                drawable = new TopRoundedDrawable(mBitmap, radius);
//                break;
//            case ImageRequestConfig.ROUND_TYPE_LEFT_BOTTOM:
//                drawable = new LeftBottomRoundedDrawable(mBitmap, radius);
//                break;
//            case ImageRequestConfig.ROUND_TYPE_TOP_RIGHT:
//                drawable = new TopRightRoundedDrawable(mBitmap, radius);
//                break;
//        }
        return drawable;
    }
}
