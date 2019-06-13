package com.zm.cuz.whatstheweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zm.cuz.whatstheweather.gson.Future;
import com.zm.cuz.whatstheweather.gson.Response;
import com.zm.cuz.whatstheweather.gson.Sk;
import com.zm.cuz.whatstheweather.gson.Today;
import com.zm.cuz.whatstheweather.util.Photo;
import com.zm.cuz.whatstheweather.util.MyNetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WeatherActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private LinearLayout nowLayout;
    private Button navButton,rfButton;
    private TextView titleCity;

    private TextView degreeText;
    private TextView weatherInfoText;
    private ImageView mImageView;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView WashText;
    private TextView sportText;
    private ImageView bingPicImg;
    private TextView mTempText;

    private Response mResponse;
    private Today mToday;
    private Sk mSk;
    private String city_id;
    private List<Future> mFutureList = new ArrayList<>();

    private double latitude = 0.0;
    private double longitude = 0.0;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, FutureActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();

        try {
            getData_gps();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if(city_id ==null) getData_gps();
                    else {
                        getData_city(city_id);
                    }
                    swipeRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, CitylistActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        rfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    city_id =null;
                    getData_gps();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        nowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, FutureActivity.class);
                intent.putExtra("city_id",mToday.getCity());
                startActivity(intent);
            }
        });

        Glide.with(this).load(Photo.getImageApi(0)).into(bingPicImg);

    }

    void initView(){

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        // 初始化各控件
        nowLayout = (LinearLayout) findViewById(R.id.now_layout);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mImageView = (ImageView) findViewById(R.id.weather_image);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        WashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        navButton = (Button) findViewById(R.id.nav_button);
        rfButton = (Button) findViewById(R.id.rf_button);
        mTempText = (TextView) findViewById(R.id.temperature_text);
    }

    //动态申请权限的测试方法
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},1);
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }//检查申请权限

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(getApplicationContext(),latitude+","+longitude,Toast.LENGTH_SHORT).show();
                return;
            }else Toast.makeText(getApplicationContext(),"GPS定位失败",Toast.LENGTH_SHORT).show();
        }
        LocationListener locationListener = new LocationListener() {

            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {

            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {

            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    Log.e("Map", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                }
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,60000, 0,locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
            latitude = location.getLatitude(); //经度
            longitude = location.getLongitude(); //纬度
            Toast.makeText(getApplicationContext(),"网络定位成功："+latitude+","+longitude,Toast.LENGTH_SHORT).show();
            return;
        }else Toast.makeText(getApplicationContext(),"网络定位失败",Toast.LENGTH_SHORT).show();
    }

    //gps访问天气网络接口
    public void getData_gps() throws JSONException {
        if (longitude == 0 && latitude == 0){
            getLocation();//获取经纬度
        }
        MyNetUtil.get("geo?format=2&key=f1cb0837ce6ff8297cbcf75aef5b0e9b&lon="+ longitude +"&lat="+ latitude, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.e("onSuccess",response.toString());
                mResponse = new Gson().fromJson(response.toString(), new TypeToken<Response>(){}.getType());
                mToday = mResponse.getResult().getToday();
                mSk = mResponse.getResult().getSk();
                showWeatherInfo();

                mFutureList.clear();
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONArray f_json = result.getJSONArray("future");
                    Log.e("future", f_json.toString());
                    for(int i =0;i <= f_json.length();i++){
                        Future temp = new Future();
                        JSONObject tempJSON = (JSONObject) f_json.get(i);
                        temp.setTemperature(tempJSON.getString("temperature"));
                        temp.setWeather(tempJSON.getString("weather"));
                        JSONObject weatherJSON = (JSONObject) tempJSON.getJSONObject("weather_id");
                        Future.Weather_id weather_id = new Future.Weather_id();
                        weather_id.setFa(weatherJSON.getString("fa"));
                        weather_id.setFb(weatherJSON.getString("fb"));
                        temp.setWeather_id(weather_id);
                        temp.setWeek(tempJSON.getString("week"));
                        //Log.e("Today",tempJSON.getString("week"));
                        temp.setDate(tempJSON.getString("date"));
                        mFutureList.add(temp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //      zjw:f1cb0837ce6ff8297cbcf75aef5b0e9b
    //alien_key:ca41d59b7d82b4ea5cb5e0c4cf754394
    //城市名访问天气接口
    public void getData_city(String cityName) throws JSONException{
        MyNetUtil.get("index?format=2&cityname=" + cityName + "&key=f1cb0837ce6ff8297cbcf75aef5b0e9b",null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("onSuccess",response.toString());
                mResponse = new Gson().fromJson(response.toString(), new TypeToken<Response>(){}.getType());
                mToday = mResponse.getResult().getToday();
                mSk = mResponse.getResult().getSk();
                showWeatherInfo();

                mFutureList.clear();
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONArray f_json = result.getJSONArray("future");
                    Log.e("future", f_json.toString());
                    for(int i =0;i <= f_json.length();i++){
                        Future temp = new Future();
                        JSONObject tempJSON = (JSONObject) f_json.get(i);
                        temp.setTemperature(tempJSON.getString("temperature"));
                        temp.setWeather(tempJSON.getString("weather"));
                        JSONObject weatherJSON = (JSONObject) tempJSON.getJSONObject("weather_id");
                        Future.Weather_id weather_id = new Future.Weather_id();
                        weather_id.setFa(weatherJSON.getString("fa"));
                        weather_id.setFb(weatherJSON.getString("fb"));
                        temp.setWeather_id(weather_id);
                        temp.setWeek(tempJSON.getString("week"));
                        //Log.e("Today",tempJSON.getString("week"));
                        temp.setDate(tempJSON.getString("date"));
                        mFutureList.add(temp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {
            String result = data.getStringExtra("result");
            try {
                city_id = toURLEncoded(result);
                getData_city(city_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d("toURLEncoded error:",paramString);
            return "";
        }
        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e("toURLEncoded error:", paramString , localException);
        }

        return "";
    }

    private void showWeatherInfo() {
        String cityName = mToday.getCity();
        String updateTime ="更新时间："+ mSk.getTime();//mToday.getDate_y()+
        String degree = mSk.getTemp()+ "℃";
        //String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        //titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        mTempText.setText(mToday.getTemperature());
        mImageView.setImageResource( this.getResources().getIdentifier(Photo.drawableID(mSk.getTime(),mToday.getWeather_id().getFa()),"drawable",  this.getPackageName()));
        String humidity = "空气湿度："+mSk.getHumidity();
        weatherInfoText.setText(updateTime);

        if (mSk.getWind_direction()!= null) {
            aqiText.setText(mSk.getWind_direction());
            pm25Text.setText(mSk.getWind_strength());
        }
        String comfort = "舒适度：" + mToday.getDressing_index();
        String Wash = "洗衣指数：" + mToday.getWash_index();
        String sport = "穿衣指南：" + mToday.getDressing_advice();
        comfortText.setText(comfort);
        WashText.setText(Wash);
        sportText.setText(sport);
    }
}
