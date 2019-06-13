package com.zm.cuz.whatstheweather;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.zm.cuz.whatstheweather.db.City;
import com.zm.cuz.whatstheweather.db.DBHelper;
import com.zm.cuz.whatstheweather.util.PermissionUtils;

import org.litepal.LitePal;

import java.util.Timer;
import java.util.TimerTask;


public class LoadingActivity extends FragmentActivity {
	
    private Timer timer;
    
    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(LoadingActivity.this, WeatherActivity.class);
                    startActivity(intent);
                    timer.cancel();
                    finish();

                    break;
            }
            super.handleMessage(msg);
        }
    };
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        long start = System.currentTimeMillis();
        permission();
        // 初始化数据库
        LitePal.getDatabase();
        int city_count=LitePal.count(City.class);
        if(city_count==0) {
            DBHelper.createCity();
            Log.e("db", "create db city");
        }

        long end = System.currentTimeMillis();
        Log.e("inittime", end - start + "");

        timer = new Timer(true);
        timer.schedule(task,2000);
    }
    private void permission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permissionsNeedCheck = PermissionUtils.checkPermission(this, permissions);
        if(permissionsNeedCheck != null){
            PermissionUtils.grantPermission(this, permissionsNeedCheck, PermissionUtils.REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode){
            case PermissionUtils.REQUEST_PERMISSIONS:
                if(PermissionUtils.isGrantedAllPermissions(permissions, grantResults)){
                    Toast.makeText(this, "你允许了全部授权", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "你拒绝了部分权限，可能造成程序运行不正常", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

}


