package com.andkid.fancy.request;

import android.widget.ImageView;

import com.andkid.fancy.resource.Resource;
import com.andkid.fancy.target.ImageViewTargetFactory;
import com.andkid.fancy.target.Target;

/**
 * Created by yuguan.chen on 2017/7/12.
 */

public class RequestBuilder<ResourceType> {

    private Request request = new Request();
    private RequestManager requestManager;
    private Class<ResourceType> resourceType;

    public RequestBuilder(RequestManager requestManager, Class<ResourceType> resourceType) {
        this.requestManager = requestManager;
        this.resourceType = resourceType;
    }

    public RequestBuilder<ResourceType> from(String url) {
        request.url = url;
        return this;
    }

    //TODO 扩展以下接口
//    private RequestBuilder<ResourceType> from(File file) {
//        return this;
//    }
//
//    private RequestBuilder<ResourceType> from(Integer resourceId) {
//        return this;
//    }
//
//    private RequestBuilder<ResourceType> from(Uri uri) {
//        return this;
//    }
//
//    private RequestBuilder<ResourceType> from(Object model) {
//        return this;
//    }

    /**
     * 磁盘缓存策略
     * @return
     */
    public RequestBuilder<ResourceType> diskCacheStrategy() {
        return this;
    }

    /**
     * 不使用memory cache
     * @return
     */
    public RequestBuilder<ResourceType> skipMemoryCache() {
        request.skipMemoryCache = true;
        return this;
    }

    /**
     * 默认图
     * @return
     */
    public RequestBuilder<ResourceType> placeholder(int resId) {
        request.placeholder = resId;
        return this;
    }

    /**
     * 淡入
     * @return
     */
    public RequestBuilder<ResourceType> crossFade() {
        request.crossFade = true;
        return this;
    }

    /**
     * 圆角
     * @return
     */
    public RequestBuilder<ResourceType> circular(int ratio) {
        request.ratio = ratio;
        return this;
    }

    /**
     * 尺寸
     * @return
     */
    public RequestBuilder<ResourceType> size(int width, int height) {
        request.width = width;
        request.height = height;
        return this;
    }

    /**
     * 尺寸适应view大小
     * @return
     */
    public RequestBuilder<ResourceType> fitBounds() {
        request.fitBounds = true;
        return this;
    }

    /**
     * 数据获取结果监听
     * @return
     */
    public RequestBuilder<ResourceType> listener(RequestListener<ResourceType> listener) {
        request.listener = listener;
        return this;
    }

    /**
     * 开始加载数据
     * @return
     */
    public void load() {
        requestManager.runRequest(request);
    }

    /**
     * 开始加载数据，成功加载后设置给目标
     * @return
     */
    public void into(Target<?> target) {
        request.setTarget(target);
        requestManager.runRequest(request);
    }

    /**
     * 开始加载数据，成功加载后设置给目标
     * @return
     */
    public void into(ImageView imageView) {
        requestManager.clear(imageView);
        into(ImageViewTargetFactory.buildTarget(imageView, resourceType));
    }

}
