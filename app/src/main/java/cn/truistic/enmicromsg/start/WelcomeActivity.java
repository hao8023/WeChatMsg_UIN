package cn.truistic.enmicromsg.start;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import cn.truistic.enmicromsg.R;
import cn.truistic.enmicromsg.base.BaseActivity;
import cn.truistic.enmicromsg.common.util.SharedPerfUtil;
import cn.truistic.enmicromsg.main.model.Result;
import cn.truistic.enmicromsg.main.ui.MainActivity;

/**
 * 欢迎界面，本页面校验 密钥是否正确，校验是否正确的登陆过
 */
public class WelcomeActivity extends BaseActivity {

    private Button btn_start;
    private EditText et_secret;
    private RequestQueue queue;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = NoHttp.newRequestQueue();
        //获取设备唯一标识码
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        Logger.d("唯一标识码：  "+ imei);
        setContentView(R.layout.activity_welcome);
        initView();
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.welcome_btn_start);
        et_secret = (EditText) findViewById(R.id.et_secret);
        btn_start.setOnClickListener(new View.OnClickListener() {

            private String secret;
            private Request<String> request;

            @Override
            public void onClick(View v) {

                secret = et_secret.getText().toString().trim();
                Logger.d("密钥：  "+ secret);
                request = NoHttp.createStringRequest("http://192.168.2.99:8080/index.php?s=/Wx/Secret/inspect", RequestMethod.POST);
                request.add("secret", secret);
                request.add("imei",imei);
                queue.add(0, request, new OnResponseListener<String>() {

                    private Result result;
                    private String json;

                    @Override
                    public void onStart(int what) {
                        Logger.d("授权联网开始");
                    }

                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        Logger.d("授权联网成功");
                        json = response.get();
                        Gson gson = new Gson();
                        result = gson.fromJson(json, Result.class);
                        if (result.result == 2) { //校验密钥正确，imei码 和 密钥 匹配
                            Logger.d("插入成功"+2);
                            Toast.makeText(WelcomeActivity.this,"校验成功",Toast.LENGTH_SHORT).show();
                             SharedPerfUtil.saveSecret(WelcomeActivity.this, secret);
                            SharedPerfUtil.saveIsFirstStart(WelcomeActivity.this, false);
                            Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        }else if ( result.result == 3) { //

                        }else if ( result.result == 1) { //绑定不一致
                            Toast.makeText(WelcomeActivity.this,"绑定不一致",Toast.LENGTH_SHORT).show();
                        }else if ( result.result == 0 ) { //密钥不正确
                            Toast.makeText(WelcomeActivity.this,"请联系服务商购买使用版权",Toast.LENGTH_SHORT).show();

                        }else if (result.result == 4){ //校验密钥正确，imei码 和 密钥 匹配
                            Logger.d("匹配成功"+4);
                            Toast.makeText(WelcomeActivity.this,"匹配成功",Toast.LENGTH_SHORT).show();

                            SharedPerfUtil.saveSecret(WelcomeActivity.this, secret);
                            SharedPerfUtil.saveIsFirstStart(WelcomeActivity.this, false);
                            Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        }


                    }

                    @Override
                    public void onFailed(int what, Response<String> response) {
                        Logger.d("授权联网失败");

                    }

                    @Override
                    public void onFinish(int what) {
                        Logger.d("授权联网结束");

                    }
                });

            }
        });
    }

}
