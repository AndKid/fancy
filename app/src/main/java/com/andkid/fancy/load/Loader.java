package com.andkid.fancy.load;

import com.andkid.fancy.request.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndKid on 2017/8/20 0020.
 */

public class Loader {

    private List<LoadUnit> units = new ArrayList<>();

    public Loader() {
    }

    public void load(Request request) {
        //memory
        //load
        //TODO 同样的url不同的宽高对应不同的unit，对应不同的disk_cache_fetcher，但是要同一个network_fetcher
    }

}
