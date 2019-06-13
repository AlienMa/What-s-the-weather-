/**
  * Copyright 2019 bejson.com 
  */
package com.zm.cuz.whatstheweather.gson;

import java.util.List;

/**
 * Auto-generated: 2019-05-25 17:55:45
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Result {

    private Sk sk;
    private Today today;
    private List<Future> mFuture;

    public Sk getSk() {
        return sk;
    }

    public void setSk(Sk sk) {
        this.sk = sk;
    }

    public Today getToday() {
        return today;
    }

    public void setToday(Today today) {
        this.today = today;
    }

    public List<Future> getFuture() {
        return mFuture;
    }

    public void setFuture(List<Future> future) {
        this.mFuture = future;
    }
}