package com.andkid.fancy.load;

/**
 * Created by AndKid on 2017/8/21 0021.
 */

public class LoadKey {

    private String url;
    private int width;
    private int height;
    private int hashCode;

    public LoadKey(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LoadKey) {
            LoadKey other = (LoadKey) o;
            return url.equals(other.url)
                    && height == other.height
                    && width == other.width;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = url.hashCode();
            hashCode = 31 * hashCode + width;
            hashCode = 31 * hashCode + height;
        }
        return hashCode;
    }

}
