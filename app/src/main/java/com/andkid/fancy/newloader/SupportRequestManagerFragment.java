package com.andkid.fancy.newloader;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.andkid.fancy.newloader.implement.RequestManager;

/**
 * Created by liuyan on 2017/7/11.
 */

public class SupportRequestManagerFragment extends Fragment {

    private RequestManager mRequestManager;

    public RequestManager getRequestManager() {
        return mRequestManager;
    }

    public void setRequestManager(RequestManager requestManager) {
        this.mRequestManager = requestManager;
    }

    @Override
    public void onDestroy() {
        Log.d("fanli_image", "SupportRequestManagerFragment onDestroy()");
        super.onDestroy();
        mRequestManager.onDestroy();
    }
}
