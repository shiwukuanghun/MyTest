package com.itheima.mytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itheima.androidlib.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private  void method() {
        Utils.test();
    }

}
