package com.itheima.heimagirl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.list_view)
    ListView mListView;
    private Gson mGson = new Gson();
    private List<ResultBean.ResultsBean> mListData = new ArrayList<>();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mListView.setAdapter(mBaseAdapter);
        //sendSyncRequest();
        sendAyncRequest();
        initListener();
    }

    private void initListener() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当ListView是一个idle,判断是否滑动到底部
                if(scrollState == SCROLL_STATE_IDLE) {
                    //还要判断是否正在加载数据，如果正在加载数据也不要发送网络请求
                    if(mListView.getLastVisiblePosition() == mListData.size()-1 && !isLoading) {
                        //滑动到了底部，加载更多数据
                        loadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void loadMore() {
        isLoading = true;
        OkHttpClient okHttpClient = new OkHttpClient();
        int nextIndex = mListData.size()/10 + 1;
        String url = "http://gank.io/api/data/福利/10/" + nextIndex;
        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ResultBean resultBean = mGson.fromJson(result, ResultBean.class);
//                Log.d(TAG, "onResponse:" + resultBean.getResults().get(1).getUrl());
                //将网络数据加到数据集合
                mListData.addAll(resultBean.getResults());
                Log.d(TAG, "mListData.size()===" + mListData.size());
                //在主线程刷新ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //通知adapter刷新列表
                        mBaseAdapter.notifyDataSetChanged();
                    }
                });
                isLoading = false;
            }
        });
    }

    private void sendAyncRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建网络请求
        String url = "http://gank.io/api/data/福利/10/1";
        Request request = new Request.Builder().get().url(url).build();
        //异步请求不需要等到结果返回，就执行后面的代码，okhttp会在子线程执行网络请求，返回结果
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ResultBean resultBean = mGson.fromJson(result, ResultBean.class);
//                Log.d(TAG, "onResponse:" + resultBean.getResults().get(1).getUrl());
                //将网络数据加到数据集合
                mListData.addAll(resultBean.getResults());
                Log.d(TAG, "mListData.size()===" + mListData.size());
                //在主线程刷新ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //通知adapter刷新列表
                        mBaseAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     * 发送同步请求
     */
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

    private BaseAdapter mBaseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.view_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //绑定视图
            ResultBean.ResultsBean resultBean = mListData.get(position);//拿到对应位置的数据
            //更新发布时间
            holder.mTextView.setText(resultBean.getPublishedAt());
            //刷新图片
            String url = resultBean.getUrl();
            //centerCrop()等比例放大图片填充控件
            //Glide.with(MainActivity.this).load(url).centerCrop().into(holder.mImageView);
            Glide.with(MainActivity.this).load(url).bitmapTransform(new CropCircleTransformation(MainActivity.this)).into(holder.mImageView);
            return convertView;
        }
    };

    public class ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        public ViewHolder(View root) {
            mImageView = (ImageView) root.findViewById(R.id.image);
            mTextView = (TextView) root.findViewById(R.id.publish_time);
        }
    }

}
