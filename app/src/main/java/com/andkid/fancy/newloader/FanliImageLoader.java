package com.andkid.fancy.newloader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.andkid.fancy.newloader.implement.RequestManager;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by liuyan on 2017/7/11.
 */

public class FanliImageLoader {

    private static final String TAG = "FanliImageLoader";
    private static final String FRAGMENT_TAG = "com.fanli.iamge.loader";

    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;

    /**
     * 如果在调用with方法的时候传入的不是Fragment和Activity，不做自动cancel
     **/
    private static RequestManager mApplicationManager;

    /**
     * RequestManagerFragment的初始化添加是异步的，可能在最开始添加fragment的时候几个请求通过fm.findFragmentByTag(FRAGMENT_TAG)
     * 得到的都是null,导致同一页面生成多个fragment
     * 添加这个来保存正在渲染的fragment，避免上述问题
     **/
    final static Map<FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap<FragmentManager, RequestManagerFragment>();

    final static Map<android.support.v4.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments =
            new HashMap<android.support.v4.app.FragmentManager, SupportRequestManagerFragment>();

    static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ID_REMOVE_FRAGMENT_MANAGER:
                    FragmentManager fm = (FragmentManager) msg.obj;
                    pendingRequestManagerFragments.remove(fm);
                    break;
                case ID_REMOVE_SUPPORT_FRAGMENT_MANAGER:
                    android.support.v4.app.FragmentManager v4fm = (android.support.v4.app.FragmentManager) msg.obj;
                    pendingSupportRequestManagerFragments.remove(v4fm);
                    break;
                default:
                    break;
            }
        }
    };

    public static RequestManager with(Activity activity) {
        assertMainThread();
        assertNotDestroyed(activity);
        FragmentManager fm = activity.getFragmentManager();
        return fragmentGet(activity, fm, null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static RequestManager with(Fragment fragment) {
        assertMainThread();
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return with(fragment.getActivity().getApplicationContext());
        }
        FragmentManager fm = fragment.getChildFragmentManager();
        return fragmentGet(fragment.getActivity(), fm, fragment);
    }

    public static RequestManager with(android.support.v4.app.Fragment v4_fragment) {
        assertMainThread();
        if (v4_fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
        }
        android.support.v4.app.FragmentManager fm = v4_fragment.getChildFragmentManager();
        return supportFragmentGet(v4_fragment.getActivity(), fm, v4_fragment);

    }

    public static RequestManager with(Context context) {
        assertMainThread();
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (!(context instanceof Application)) {
            if (context instanceof android.support.v4.app.FragmentActivity) {
                return with((android.support.v4.app.FragmentActivity) context);
            } else if (context instanceof Activity) {
                Log.d(TAG, "RequestManager with Activity");
                return with((Activity) context);
            } else if (context instanceof ContextWrapper) {
                return with(((ContextWrapper) context).getBaseContext());
            }
        }
        Log.d(TAG, "RequestManager with Application");
        return getApplicationManager(context);
    }

    private static RequestManager getApplicationManager(Context context) {
        if (mApplicationManager == null) {
            mApplicationManager = new DefaultRequestManager(context);
        }
        return mApplicationManager;
    }

    private static RequestManager fragmentGet(Context context, FragmentManager fm, Fragment parentHint) {
        RequestManagerFragment current = getRequestManagerFragment(fm, parentHint);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            requestManager = new RecycleRequestManager(context.getApplicationContext());
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

    private static RequestManagerFragment getRequestManagerFragment(final FragmentManager fm, Fragment parentHint) {
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = pendingRequestManagerFragments.get(fm);
            Log.d(TAG, "get from pendingRequestManagerFragments");
            if (current == null) {
                Log.d(TAG, "not in pending, create one");
                current = new RequestManagerFragment();
                pendingRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                Message.obtain(mHandler, ID_REMOVE_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        } else {
            Log.d(TAG, "already in FragmentManager");
        }
        return current;
    }

    private static RequestManager supportFragmentGet(Context context, android.support.v4.app.FragmentManager fm, android.support.v4.app.Fragment
            parentHint) {
        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm, parentHint);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            requestManager = new RecycleRequestManager(context.getApplicationContext());
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

    private static SupportRequestManagerFragment getSupportRequestManagerFragment(android.support.v4.app.FragmentManager fm, android.support.v4.app
            .Fragment parentHint) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = pendingSupportRequestManagerFragments.get(fm);
            if (current == null) {
                current = new SupportRequestManagerFragment();
                pendingSupportRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                Message.obtain(mHandler, ID_REMOVE_SUPPORT_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        }
        return current;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    private static void assertMainThread() {
        if (!isOnMainThread()) {
            throw new IllegalArgumentException("You must start load in main thread");
        }
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
