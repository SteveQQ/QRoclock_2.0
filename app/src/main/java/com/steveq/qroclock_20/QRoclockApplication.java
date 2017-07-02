package com.steveq.qroclock_20;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Adam on 2017-07-03.
 */

public class QRoclockApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                        .setDefaultFontPath("fonts/Quicksand-Regular.ttf")
                                        .build()
        );
    }
}
