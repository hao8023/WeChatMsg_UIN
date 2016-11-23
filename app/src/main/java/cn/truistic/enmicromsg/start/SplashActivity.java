package cn.truistic.enmicromsg.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cn.truistic.enmicromsg.R;
import cn.truistic.enmicromsg.base.BaseActivity;
import cn.truistic.enmicromsg.common.util.SharedPerfUtil;
import cn.truistic.enmicromsg.main.ui.MainActivity;

/**
 * 启动界面
 */
public class SplashActivity extends BaseActivity {

    private final static long DELAY_TIME = 1000;
    private boolean isFirstStart; // 是否第一次启动应用
    private String secret;//密钥
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isFirstStart = SharedPerfUtil.getIsFirstStart(this);
        secret = SharedPerfUtil.getSecret(this);
        delay();
    }

    /**
     * 延迟启动
     */
    private void delay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstStart) {
                    Intent welcomeIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(welcomeIntent);
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                finish();
            }
        }, DELAY_TIME);
    }

}
