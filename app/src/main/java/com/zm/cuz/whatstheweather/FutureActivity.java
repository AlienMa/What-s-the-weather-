package com.zm.cuz.whatstheweather;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stone.pile.libs.PileLayout;
import com.zm.cuz.whatstheweather.gson.Future;
import com.zm.cuz.whatstheweather.gson.Response;
import com.zm.cuz.whatstheweather.util.Photo;
import com.zm.cuz.whatstheweather.util.MyNetUtil;

import org.json.*;

import com.loopj.android.http.*;
import com.zm.cuz.whatstheweather.widget.FadeTransitionImageView;
import com.zm.cuz.whatstheweather.widget.HorizontalTransitionLayout;
import com.zm.cuz.whatstheweather.widget.VerticalTransitionLayout;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class FutureActivity extends AppCompatActivity {

    private View positionView;
    private PileLayout pileLayout;

    private int lastDisplay = -1;

    private ObjectAnimator transitionAnimator;
    private float transitionValue;

    private HorizontalTransitionLayout cityView, temperatureView;
    private VerticalTransitionLayout WeatherView, timeView;
    private FadeTransitionImageView bottomView;
    private Animator.AnimatorListener animatorListener;
    private TextView descriptionView;
    private ImageView weather_image;

    private Response mResponse;
    private String city_id;
    private List<Future> mFutureList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);

        initView();
        Intent intent = getIntent();
        city_id = intent.getStringExtra("city_id");
        try {
            initDataList();//初始化数据
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setTransitionAnimator();
    }

    private void setTransitionAnimator(){
        //PileLayout绑定Adapter
        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e("onAnimationStart: ","onAnimationStart" );
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cityView.onAnimationEnd();
                temperatureView.onAnimationEnd();
                WeatherView.onAnimationEnd();
                bottomView.onAnimationEnd();
                timeView.onAnimationEnd();
                Log.e("onAnimationEnd: ", "onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("onAnimationRepeat: ","onAnimationRepeat: " );
            }
        };

        pileLayout.setAdapter(new PileLayout.Adapter() {
            @Override
            public int getLayoutId() {
                return R.layout.item_layout;
            }

            @Override
            public void bindView(View view, int position) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
                    view.setTag(viewHolder);
                }
                Glide
                        .with(FutureActivity.this)
                        .load(Photo.getImageApi(position))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.imageView);
            }

            @Override
            public int getItemCount() {
                return  mFutureList.size();
            }

            @Override
            public void displaying(int position) {
                descriptionView.setText( mFutureList.get(position).getWind());
                //weather_image.setImageResource( getApplication().getResources().getIdentifier(Photo.drawableID("12:00", mFutureList.get(position).getWeather_id().getFa()),"drawable",  getApplication().getPackageName()));
                if (lastDisplay <0) {
                    initSecene(position);
                    lastDisplay = 0;
                } else if (lastDisplay != position) {
                    transitionSecene(position);
                    lastDisplay = position;
                }
            }

            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
//                //跳转选择界面
//                Intent intent = new Intent(FutureActivity.this, CitylistActivity.class);
//                startActivityForResult(intent, 1);
            }
        });
    }

    void initView(){
        positionView = findViewById(R.id.positionView);
        cityView = (HorizontalTransitionLayout)findViewById(R.id.cityView);
        temperatureView = (HorizontalTransitionLayout) findViewById(R.id.temperatureView);
        pileLayout = (PileLayout) findViewById(R.id.pileLayout);
        WeatherView = (VerticalTransitionLayout) findViewById(R.id.WeatherView);
        weather_image = (ImageView) findViewById(R.id.weather_image);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        timeView = (VerticalTransitionLayout) findViewById(R.id.timeView);
        bottomView = (FadeTransitionImageView) findViewById(R.id.bottomImageView);
    }

    private void initSecene(int position) {
        cityView.firstInit(mFutureList.get(position).getWeek());
        temperatureView.firstInit( mFutureList.get(position).getTemperature());
        WeatherView.firstInit( mFutureList.get(position).getWeather());
        bottomView.firstInit(Photo.getImageApi(position));
        timeView.firstInit(city_id+" - "+  mFutureList.get(position).getDate());
    }

    private void transitionSecene(int position) {
        if (transitionAnimator != null) {
            transitionAnimator.cancel();
        }
        cityView.saveNextPosition(position, mFutureList.get(position).getWeek());
        temperatureView.saveNextPosition(position, mFutureList.get(position).getTemperature());
        WeatherView.saveNextPosition(position, mFutureList.get(position).getWeather());
        bottomView.saveNextPosition(position, Photo.getImageApi(position));
        timeView.saveNextPosition(position, city_id+" - "+  mFutureList.get(position).getDate());

        transitionAnimator = ObjectAnimator.ofFloat(this, "transitionValue", 0.0f, 1.0f);
        transitionAnimator.setDuration(300);
        transitionAnimator.start();
        transitionAnimator.addListener(animatorListener);
    }

    private void initDataList() throws JSONException {
        getSevenData_city(city_id);
    }
    /**
     * 属性动画
     */
    public void setTransitionValue(float transitionValue) {
        this.transitionValue = transitionValue;
        cityView.duringAnimation(transitionValue);
        temperatureView.duringAnimation(transitionValue);
        WeatherView.duringAnimation(transitionValue);
        bottomView.duringAnimation(transitionValue);
        timeView.duringAnimation(transitionValue);
    }

    public float getTransitionValue() {
        return transitionValue;
    }

    class ViewHolder {
        ImageView imageView;
    }

    //      zjw:f1cb0837ce6ff8297cbcf75aef5b0e9b
    //alien_key:ca41d59b7d82b4ea5cb5e0c4cf754394
    //城市名访问天气接口
    //http://v.juhe.cn/weather/index?cityname="+cityName+"&key=***a7558b2e0bedaa19673f74a6809ce

    public void getSevenData_city(String cityName) throws JSONException{
        MyNetUtil.get("index?format=2&cityname=" + cityName + "&key=54230d4901a6f8acfa9e93830894f4ec",null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("onSuccess",response.toString());
                mResponse = new Gson().fromJson(response.toString(), new TypeToken<Response>(){}.getType());
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONArray f_json = result.getJSONArray("future");
                    mFutureList.clear();
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
                        temp.setWind(tempJSON.getString("wind"));
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

}
