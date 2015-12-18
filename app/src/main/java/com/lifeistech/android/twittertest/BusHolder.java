package com.lifeistech.android.twittertest;

import com.squareup.otto.Bus;

/**
 * Created by MINAMI on 2015/12/11.
 */
public class BusHolder {
    private static Bus bus;

    public static void init() {
        bus = new Bus();
    }

    public static Bus getBus() {
        synchronized (bus) {
            return bus;
        }
    }

    public static void post(Object object) {
        synchronized (bus) {
            bus.post(object);
        }
    }

    public static void register(Object object) {
        synchronized (bus) {
            bus.register(object);
        }
    }

    public static void unregister(Object object) {
        synchronized (bus) {
            bus.unregister(object);
        }
    }
}
