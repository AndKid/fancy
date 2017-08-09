package com.andkid.fancy.newloader;

/**
 * Created by liuyan on 2017/7/6.
 */

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 请求的配置
 **/
public class ImageRequestConfig {

    public static final int RENDER_NORMAL = 0;
    public static final int RENDER_TRANSACTION = 1;

    public static final int ROUND_TYPE_FULL = 0;
    public static final int ROUND_TYPE_LEFT_RIGHT = 5;
    public static final int ROUND_TYPE_LEFT_BOTTOM = 9;
    public static final int ROUND_TYPE_TOP_RIGHT = 10;

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    int maxHeight = 0;
    int maxWidth = 0;
    int defaultImageResId = 0;
    int errorImageResId = 0;
    boolean canGetFromDisk = true;
    boolean canGetFromRemote = true;
    boolean canGetFromMemory = true;
    boolean needSave = true;

    Priority priority = Priority.NORMAL;
    ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;
    Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;

    int renderType;
    int radius;
    int roundType;
    boolean hideWhiteDrawable;//未传入默认图时会展示白色图片，此参数控制是否隐藏白色图片

    public ImageRequestConfig setCanGetFromDisk(boolean canGetFromDisk) {
        this.canGetFromDisk = canGetFromDisk;
        return this;
    }

    public ImageRequestConfig setCanGetFromRemote(boolean canGetFromRemote) {
        this.canGetFromRemote = canGetFromRemote;
        return this;
    }

    public ImageRequestConfig setCanGetFromMemory(boolean canGetFromMemory) {
        this.canGetFromMemory = canGetFromMemory;
        return this;
    }

    public ImageRequestConfig setNeedSave(boolean needSave) {
        this.needSave = needSave;
        return this;
    }

    public ImageRequestConfig setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    public ImageRequestConfig setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
        return this;
    }

    public ImageRequestConfig setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public ImageRequestConfig setMaxWidth(int maxWidth) {
        this.maxWidth = maxHeight;
        return this;
    }

    public ImageRequestConfig setDefaultImageResId(int defaultImageResId) {
        this.defaultImageResId = defaultImageResId;
        return this;
    }

    public ImageRequestConfig setErrorImageResId(int errorImageResId) {
        this.errorImageResId = errorImageResId;
        return this;
    }

    public ImageRequestConfig setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public ImageRequestConfig setRenderType(int renderType) {
        this.renderType = renderType;
        return this;
    }

    public ImageRequestConfig setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public ImageRequestConfig setRoundType(int roundType) {
        this.roundType = roundType;
        return this;
    }

    public ImageRequestConfig setHideWhiteDrawable(boolean hideWhiteDrawable) {
        this.hideWhiteDrawable = hideWhiteDrawable;
        return this;
    }
}
