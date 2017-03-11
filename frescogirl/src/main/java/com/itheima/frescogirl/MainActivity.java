package com.itheima.frescogirl;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.my_image_view)
    SimpleDraweeView mMyImageView;
    //SimpleDraweeView的宽高必须写死或者最大，不能自适应
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this); //必须在setContentView之前初始化
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Uri uri = Uri.parse("http://dynamic-image.yesky.com/740x-/uploadImages/2015/163/50/690V3VHW0P77.jpg");
        mMyImageView.setImageURI(uri);
        //DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        mMyImageView.setController(controller);
    }
}
