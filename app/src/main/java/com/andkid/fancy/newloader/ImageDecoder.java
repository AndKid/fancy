package com.andkid.fancy.newloader;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.andkid.fancy.newloader.implement.ImageData;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;

public class ImageDecoder {

    private final static int MAX_SIZE = 520 * 520;//图片内存大于1M
    public static final int TYPE_HEADER_LENGTH = 4;
    public static final String HEADER_GIF = "47494638";
    public static final String[] HEADER_BITMAP_ARRAYS = {"FFD8FF", "89504E47", "49492A00", "424D"};

    public enum Type {
        BITMAP, GIF, JSON, UNKNOW
    }

    public static ImageData decodeFromBytes(byte[] data, ImageRequestConfig config) {

        switch (getImageType(data)) {
            case GIF:
                return new GifData(createGifDrawable(data));
            default:
                return new BitmapData(createBitmap(data, config));

        }
    }

    private static GifDrawable createGifDrawable(byte[] data) {
        GifDrawable drawable = null;
        try {
            int scale = getScale(getOptions(data));
            GifDrawableBuilder builder = new GifDrawableBuilder();
            builder.from(data);
            builder.sampleSize(scale);
            drawable = builder.build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    private static BitmapFactory.Options getOptions(byte[] data) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        return opts;
    }

    private static int getScale(BitmapFactory.Options opts) {
        int scale = 1;
        final int orginSize = opts.outHeight * opts.outWidth;
        if (orginSize > MAX_SIZE) { //只有大于阈值才会缩放
            int rate = orginSize / MAX_SIZE;
            if (rate < 4)
                scale = 2; //小于四倍的时候缩放1/4
            else
                scale = 3; //否则缩放1/6
        }
        return scale;
    }


    private static Bitmap createBitmap(byte[] data, ImageRequestConfig config) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        if (config.maxWidth == 0 && config.maxHeight == 0) {
            decodeOptions.inPreferredConfig = config.bitmapConfig;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(config.maxWidth, config.maxHeight, actualWidth, actualHeight, config.scaleType);
            int desiredHeight = getResizedDimension(config.maxHeight, config.maxWidth, actualHeight, actualWidth, config.scaleType);

            decodeOptions.inJustDecodeBounds = false;

            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary, ImageView.ScaleType scaleType) {

        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    /**
     * 根据文件内容获取图片类型
     */
    public static Type getImageType(byte[] data) {
        String headerMagic = bytesToHexString(data, TYPE_HEADER_LENGTH);
        if (headerMagic == null)
            return Type.UNKNOW;
        if (headerMagic.startsWith(HEADER_GIF)) {
            return Type.GIF;
        } else {
            for (String headerBitmap : HEADER_BITMAP_ARRAYS) {
                if (headerMagic.startsWith(headerBitmap)) {
                    return Type.BITMAP;
                }
            }
        }
        return Type.UNKNOW;
    }

    // 将字节转换成十六进制
    @SuppressLint("DefaultLocale")
    private static String bytesToHexString(byte[] src, int size) {
        StringBuilder builder = new StringBuilder();
        if (src == null || size <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < size; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
