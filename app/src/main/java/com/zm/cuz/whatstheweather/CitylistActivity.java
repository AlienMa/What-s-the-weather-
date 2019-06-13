package com.zm.cuz.whatstheweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zm.cuz.whatstheweather.db.DBHelper;


public class CitylistActivity extends Activity implements OnItemClickListener {

    public EditText searchEditText;
    public TextView searchClearButton;

    private ListView city_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citylist);
        initView();
    }

    private void initView() {
        searchEditText = (EditText) findViewById(R.id.title_bar_search_edit);
        searchClearButton = (TextView) findViewById(R.id.title_bar_search_clear_button);// 清空搜索内容按钮
        searchClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });
        city_lv  =(ListView) findViewById(R.id.listView_city);// 显示城市列表
        city_lv.setOnItemClickListener(this);

        try {
            DataUpdate();//获取全2594个城市
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //通过view获取其内部的组件，进而进行操作
        String text = (String) ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
//        //大多数情况下，position和id相同，并且都从0开始
//        String showText = "点击第" + position + "项，文本内容为：" + text + "，ID为：" + id;
//        Toast.makeText(this, showText, Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.putExtra("result", text);
        setResult(1, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent();
        setResult(2, i);
        finish();
    }

    //获取城市列表
    //http://v.juhe.cn/weather/citys?key=f1cb0837ce6ff8297cbcf75aef5b0e9b
    public void DataUpdate(){

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(CitylistActivity.this,
                    android.R.layout.simple_list_item_1, DBHelper.getCityList());//使用系统已经实现好的xml文件simple_list_item_1
            city_lv.setAdapter(adapter);

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(s);
                    city_lv.setAdapter(adapter);
                }
            });

    }

}
