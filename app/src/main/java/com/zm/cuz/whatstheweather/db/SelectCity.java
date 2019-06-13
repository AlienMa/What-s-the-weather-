package com.zm.cuz.whatstheweather.db;

import org.litepal.crud.LitePalSupport;

public class SelectCity extends LitePalSupport {
    String my_elect_id;
    String my_name;

    public String getMy_elect_id() {
        return my_elect_id;
    }

    public void setMy_elect_id(String my_elect_id) {
        this.my_elect_id = my_elect_id;
    }

    public String getMy_name() {
        return my_name;
    }

    public void setMy_name(String my_name) {
        this.my_name = my_name;
    }
}
