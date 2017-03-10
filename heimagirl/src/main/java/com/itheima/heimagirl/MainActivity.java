package com.itheima.heimagirl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        sendSyncRequest();
    }

    private void sendSyncRequest() {

        new Thread(new Runnable() {//网络请求是耗时操作要在子线程进行
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                /* 创建网络请求 */
                String url = "http://gank.io/api/data/福利/10/1";
                Request request = new Request.Builder().get().url(url).build();
                try {
                    //同步请求
                    Response response = okHttpClient.newCall(request).execute();
                    //同步请求就是网络请求返回之后，才能走后边的代码
                    //string()方法只能调用一次
                   // String result = response.body().string();
                    Log.d(TAG, "sendRequest:" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
