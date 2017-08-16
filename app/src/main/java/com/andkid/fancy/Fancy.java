package com.andkid.fancy;

import android.content.Context;

import com.andkid.fancy.request.RequestManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuguan.chen on 2017/7/14.
 */

public class Fancy {
    private static volatile Fancy fancy;
    private FancyContext fancyContext;
    private Map<Context, RequestManager> managers = new HashMap<>();

    private Fancy(Context context) {
        fancyContext = new FancyContext(context);
    }

    public static Fancy get(Context context) {
        if (fancy == null) {
            synchronized (Fancy.class) {
                if (fancy == null) {
                    fancy = new Fancy(context);
                }
            }
        }
        return fancy;
    }

    public static RequestManager with(Context context) {
        RequestManager manager = getRequestManager(context);
        if (manager == null) {
            manager = new RequestManager();
            addRequestManager(context, manager);
        }
        return manager;
    }

    public static RequestManager getRequestManager(Context context) {
        return Fancy.get(context).managers.get(context);
    }

    private static void addRequestManager(Context context, RequestManager requestManager) {
        Fancy.get(context).managers.put(context, requestManager);
    }

}

