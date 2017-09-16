package com.example.ioc_api;

/**
 * Created by zwl on 17-9-16.
 */

public interface ViewInject<T> {
    /**
     * inject method
     * @param t object for inject
     * @param o object with inject material
     */
    void inject(T t, Object o);
}
