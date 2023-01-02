package com.ryd.banner.banner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ryd.banner.R;
import com.ryd.banner.viewpager2.ViewPager2Activity;
import java.util.ArrayList;
import java.util.List;


//https://blog.csdn.net/weixin_43695721/article/details/123991424
// https://blog.csdn.net/GYongJia/article/details/89645378?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1-89645378-blog-123991424.pc_relevant_3mothn_strategy_and_data_recovery&spm=1001.2101.3001.4242.2&utm_relevant_index=4

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BannerViewPager banner= (BannerViewPager) findViewById(R.id.banner);
        List<Integer> imageUrl=new ArrayList<>();
        imageUrl.add(R.drawable.water); // 当用户在第一页时，向右拖拽左滑时新增一个最后一页，让用户以为滑到了最后一页
        imageUrl.add(R.drawable.bubble);// 1 用户看到的第一页
        imageUrl.add(R.drawable.grass);// 2 用户看到的第二页
        imageUrl.add(R.drawable.water);// 3 用户看到的第三页
        imageUrl.add(R.drawable.bubble);// 当用户在第三页时，向左拖拽右滑时新增一个第一页，让用户以为滑到了第一页
        banner.setData(imageUrl);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewPager2Activity.class));
            }
        });

    }


}