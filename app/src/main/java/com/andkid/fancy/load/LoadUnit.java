package com.andkid.fancy.load;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.andkid.fancy.request.Request;
import com.andkid.fancy.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndKid on 2017/8/20 0020.
 */

public class LoadUnit implements LoadTask.LoadCallback {

    private static final Handler MAIN_THREAD_HANDLER =
            new Handler(Looper.getMainLooper(), new MainThreadCallback());
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_FAILED = 2;

    private Resource<?> resource;
    private DataSource dataSource;
    private LoadUnitListener listener;
    private List<ResourceCallback> cbs = new ArrayList<>(2);

    interface LoadUnitListener {
        void onLoadComplete();
    }

    public LoadUnit(Request request, LoadUnitListener listener, ResourceCallback cb) {
        this.listener = listener;
        cbs.add(cb);
    }

    public void addCallback(ResourceCallback cb) {
        cbs.add(cb);
    }

    @Override
    public void onLoadSuccess(Resource resource, DataSource dataSource) {
        this.resource = resource;
        this.dataSource = dataSource;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_SUCCESS, this).sendToTarget();
    }

    @Override
    public void onLoadFailed() {
        MAIN_THREAD_HANDLER.obtainMessage(MSG_FAILED, this).sendToTarget();
    }

    private void handleSuccessOnMainThread() {
        listener.onLoadComplete();
        for (ResourceCallback cb : cbs) {
            cb.onResourceReady(resource, dataSource);
        }
    }

    private void handleFailureOnMainThread() {
        listener.onLoadComplete();
        for (ResourceCallback cb : cbs) {
            cb.onLoadFailed();
        }
    }

    private static class MainThreadCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message message) {
            LoadUnit unit = (LoadUnit) message.obj;
            switch (message.what) {
                case MSG_SUCCESS:
                    unit.handleSuccessOnMainThread();
                    break;
                case MSG_FAILED:
                    unit.handleFailureOnMainThread();
                    break;
                default:
                    throw new IllegalStateException("Unrecognized message: " + message.what);
            }
            return true;
        }
    }

}
