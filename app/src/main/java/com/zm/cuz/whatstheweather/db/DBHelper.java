package com.zm.cuz.whatstheweather.db;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zm.cuz.whatstheweather.gson.Response;
import com.zm.cuz.whatstheweather.gson.Today;
import com.zm.cuz.whatstheweather.util.MyNetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DBHelper {
    private Response mResponse;
    private Today mToday;

    //xj:54230d4901a6f8acfa9e93830894f4ec
    public static void createCity(){//创建City数据库
        MyNetUtil.get("citys?key=f1cb0837ce6ff8297cbcf75aef5b0e9b",null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("onSuccess",response.toString());
                try {
                    JSONArray result = response.getJSONArray("result");
                    for (int i = 0; i <result.length(); i++) {
                        JSONObject jsonCity =(JSONObject) result.get(i);
                        City city = new City();
                        city.setCity_id(jsonCity.getString("id"));
                        city.setProvince(jsonCity.getString("province"));
                        city.setDistrict(jsonCity.getString("district"));
                        boolean temp = city.save();
                        if (!temp)Log.e("onSuccess: ","存入失败" );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String[] getCityList(){
        List<City> allCity = LitePal.findAll(City.class);
        String[] sw = new String[allCity.size()];
        for(int i=0;i<allCity.size();i++){
            sw[i]=allCity.get(i).getDistrict();
        }
        return sw;
    }

    public static List<Weatherdb> getAllWeatherList() {//查询所有天气
        List<Weatherdb> allWeatherdb = LitePal.findAll(Weatherdb.class);
        return allWeatherdb;
    }

//    public void NewWeatherList() {//更新Weather数据
//        List<SelectCity> allSelect = LitePal.findAll(SelectCity.class);
//        for(int i=0;i<allSelect.size();i++){
//            String city_id = allSelect.get(i).
//            MyNetUtil.get("index?format=2&cityname=" + city_id + "&key=ca41d59b7d82b4ea5cb5e0c4cf754394",null,new JsonHttpResponseHandler(){
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    Log.e("onSuccess",response.toString());
//                    mResponse = new Gson().fromJson(response.toString(), new TypeToken<Response>(){}.getType());
//                    mToday = mResponse.getResult().getToday();
//                    Weatherdb weatherdb = new Weatherdb();
//                    weatherdb.setCity(mToday.getCity());
//                    weatherdb.setTemperature(mToday.getTemperature());
//                    weatherdb.setWeather(mToday.getWeather());
//                    weatherdb.setWeather_id(mToday.getWeather_id().getFa());
//                    weatherdb.setWind(mToday.getWind());
//                    weatherdb.setWeek(mToday.getWeek());
//                    weatherdb.setCity(mToday.getCity());
//                    weatherdb.setDate_y(mToday.getDate_y());
//                    weatherdb.setDressing_index(mToday.getDressing_index());
//                    weatherdb.setDressing_advice(mToday.getDressing_advice());
//                    weatherdb.setUv_index(mToday.getUv_index());
//                    weatherdb.setComfort_index(mToday.getComfort_index());
//                    weatherdb.setWash_index(mToday.getWash_index());
//                    weatherdb.setTravel_index(mToday.getTravel_index());
//                    weatherdb.setExercise_index(mToday.getExercise_index());
//                    weatherdb.setDrying_index(mToday.getDrying_index());
//                    weatherdb.save();
//                }
//            });
//        }
//    }

    public static Weatherdb getWeather(String city_id){
        List<Weatherdb> weatherdb = LitePal.where("city_id = ?",city_id).find(Weatherdb.class);
        Log.e("getWeather: ", weatherdb.get(0).getWeather());
        return weatherdb.get(0);
    }

    public static String getWetherId(String city_id){
        List<Weatherdb> weatherdb = LitePal.where("city_id = ?",city_id).find(Weatherdb.class);
        Log.e("getWetherId: ", weatherdb.get(0).getWeather_id());
        return weatherdb.get(0).getWeather_id();
    }

//    public static void updateSelect(String city_id, String city_name){
//        int num = LitePal.where("city_id = ?",city_id).count(SelectCity.class);
//        if(num==0){//如果数据不存在
//            SelectCity select = new SelectCity();
//            select.setSelect_id(city_id);
//            select.setName(city_name);
//            select.save();
//        }
//    }
}
