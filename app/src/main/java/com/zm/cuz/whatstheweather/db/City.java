package com.zm.cuz.whatstheweather.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private String city_id;
    private String province;
    private String District;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }
}
