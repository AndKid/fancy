package com.andkid.fancy.loader;

import android.content.Context;

import com.andkid.fancy.newloader.BasicNetwork;
import com.andkid.fancy.newloader.DiskBasedCache;
import com.andkid.fancy.newloader.HurlStack;
import com.andkid.fancy.newloader.implement.Network;
import com.andkid.fancy.request.Request;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuguan.chen on 2017/7/18.
 */

public class Loader implements RequestUnit.JobListener {

    private static final String DEFAULT_CACHE_DIR = "fanli_image";
    private Map<LoadKey, RequestUnit> jobs;
    private RequestDispatcher dispatcher;

    public Loader(Context context) {
        jobs = new HashMap<>();
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        Network network = new BasicNetwork(new HurlStack());
        dispatcher = new RequestDispatcher(new DiskBasedCache(cacheDir), network);
    }

    public void load(Request request) {
//        LoadKey loadKey = new LoadKey(request.url, request.width, request.height);
//        RequestUnit current = jobs.get(loadKey);
//        if (current != null) {
//            current.addCallback(request, request.priority, request.order);
//            return;
//        }
//        current = new RequestUnit(request, loadKey, request.url, request.width, request.height, request.priority, request.order);
//        dispatcher.add(current);
//        jobs.put(loadKey, current);
    }

    @Override
    public void onJobFinish(LoadKey loadKey) {
        jobs.remove(loadKey);
    }

}
