package com.andkid.fancy.newloader;

import android.app.Fragment;
import android.util.Log;

import com.andkid.fancy.newloader.implement.RequestManager;

/**
 * Created by liuyan on 2017/7/11.
 * <p>
 * 用于根据fragment生命周期，自动取消图片请求
 */

public class RequestManagerFragment extends Fragment {

    private RequestManager mRequestManager;

    public RequestManager getRequestManager() {
        return mRequestManager;
    }

    public void setRequestManager(RequestManager requestManager) {
        this.mRequestManager = requestManager;
    }

    @Override
    public void onDestroy() {
        Log.d("fanli_image", "RequestManagerFragment onDestroy()");
        super.onDestroy();
        mRequestManager.onDestroy();
    }
}
