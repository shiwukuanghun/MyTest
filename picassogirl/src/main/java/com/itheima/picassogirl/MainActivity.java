package com.itheima.picassogirl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.listview)
    ListView mListview;
    private Gson mGson = new Gson();
    private List<ResultBean.ResultsBean> mListData;
    private ImageAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    private void initData() {
        mListData = new ArrayList<>();
        mAdapter = new ImageAdapter();
        mListview.setAdapter(mAdapter);
        sendAyncRequest();
    }

    private void initListener() {
        mListview.setOnScrollListener(this);
    }

    private void sendAyncRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";
        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ResultBean resultBean = mGson.fromJson(result, ResultBean.class);
                mListData.addAll(resultBean.getResults());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE) {
            if(mListview.getLastVisiblePosition() == mListData.size()-1 && !isLoading) {
                loadMoreImage();
            }
        }
    }

    private void loadMoreImage() {
        isLoading = true;
        OkHttpClient okHttpClient = new OkHttpClient();
        int nextIndex = mListData.size()/10 + 1;
        String url = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/" + nextIndex;
        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ResultBean resultBean = mGson.fromJson(result, ResultBean.class);
                mListData.addAll(resultBean.getResults());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                isLoading = false;
            }
        });
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.image_item_view, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            ResultBean.ResultsBean resultsBean = mListData.get(position);
            holder.mTextView.setText(resultsBean.getPublishedAt());
            String url = resultsBean.getUrl();
            //centerCrop()必须和resize()一起用，要不就都只用resize（）
            Picasso.with(MainActivity.this).load(url).resize(300, 300).into(holder.mImageView);

            return convertView;
        }
    }

    class ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        ViewHolder(View v) {
            mImageView = (ImageView) v.findViewById(R.id.iv_image);
            mTextView = (TextView) v.findViewById(R.id.tv_publishtime);
        }

    }

}
