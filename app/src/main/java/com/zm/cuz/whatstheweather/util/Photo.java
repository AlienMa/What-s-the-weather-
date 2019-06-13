package com.zm.cuz.whatstheweather.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo {
    public final static String[] imageUrls = new String[]{
            "http://img.hb.aicdn.com/10dd7b6eb9ca02a55e915a068924058e72f7b3353a40d-ZkO3ko_fw658",
            "http://img.hb.aicdn.com/a3a995b26bd7d58ccc164eafc6ab902601984728a3101-S2H0lQ_fw658",
            "http://pic4.nipic.com/20091124/3789537_153149003980_2.jpg",
            "http://img.hb.aicdn.com/4ba573e93c6fe178db6730ba05f0176466056dbe14905-ly0Z43_fw658",
            "http://img.hb.aicdn.com/4bc60d00aa3184f1f98e418df6fb6abc447dc814226ef-ZtS8hB_fw658",
    };
    public static String getImageApi(int position){
        position = position%5;
        return imageUrls[position];
    }
    public static String  drawableID(String time,String num){
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm");
        try {
            Date flag1 = sf.parse("18:00");
            Date flag2 = sf.parse("6:00");
            Date now = sf.parse(time);
            if(flag1.getTime()<now.getTime()||flag2.getTime()>now.getTime()){
                Log.e( "drawableID: ","fb_success" );
                return "fb_"+num;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "fa_"+num;
    }
}
