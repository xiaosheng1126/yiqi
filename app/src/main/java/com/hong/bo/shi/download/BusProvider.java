package com.hong.bo.shi.download;

import com.squareup.otto.Bus;

/**
 * Created by juude on 16/3/24.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
