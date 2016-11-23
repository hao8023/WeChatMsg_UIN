package cn.truistic.enmicromsg.app;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * EnApplication
 */
public class EnApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
    }


}
