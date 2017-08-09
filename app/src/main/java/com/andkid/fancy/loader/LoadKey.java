package com.andkid.fancy.loader;

/**
 * Created by yuguan.chen on 2017/7/18.
 */

public class LoadKey {

    private String url;
    private int width;
    private int height;

    public LoadKey(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    //TODO
    //读取图片资源时，根据参数inSampleSize读合适的图片大小到内存
    //(考虑一下多处使用，但是需要长宽不一样，当长宽不够时尝试再次load进内存，只load一个长宽最小公倍数大小的内存，并舍弃之前的)
    /*@Override
    public boolean equals(Object obj) {
        if (obj instanceof LoadKey) {
            LoadKey other = (LoadKey) obj;
            return url.equals(other.url) &&
                    width == other.width &&
                    height == other.height;
        }
        return false;
    }*/

}
