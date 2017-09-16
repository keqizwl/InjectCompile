package com.example.ioc_api;

import android.app.Activity;
import android.view.View;

/**
 * Created by zwl on 17-9-16.
 */

public class ViewInjector {

    //use like that we generate one viewinject class with one activity because we want save time by not using runtime reflect
    public static void bind(Activity activity){
        //1.find viewinject class for activity class
        String viewInjectClassName = activity.getClass().getName().concat("$$ViewInject");
        try {
            Class injectClass = Class.forName(viewInjectClassName);
            //2. create inject object
            ViewInject viewInject = (ViewInject) injectClass.newInstance();
            //3. inject
            viewInject.inject(activity, activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    public static void bind(Activity activity, View v){
        String viewInjectClassName = activity.getClass().getName().concat("$$ViewInject");
        try {
            Class injectClass = Class.forName(viewInjectClassName);
            ViewInject viewInject = (ViewInject) injectClass.newInstance();
            viewInject.inject(activity, activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
