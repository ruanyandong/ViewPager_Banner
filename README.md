# ViewPager_Banner
## 背景
最近做需求要实现一个轮播图，最后通过Handler+ViewPager实现了需求，所以把实现的过程总结一下，方便以后学习参考，以下是轮播图的效果：

## 实现思路
 - 定时轮播
利用Handler+ViewPager，Handler发送定时消息切换ViewPager的页面

 - 无限轮播效果
![在这里插入图片描述](https://img-blog.csdnimg.cn/b550e68b40ba407b85f729abc0ab094e.png)

如果我们只是在自动轮播到最后一页 然后进行判断让切换到第一页 这样是可以实现轮播的效果，但是 有两个问题
切换从最后一页切换到第一页的时候有一个很明显的回滚效果 不是我们想要的
当我们手动滑动的时候 在第一页和最后一页的时候 无法继续左右滑动 因为已经没有下一页了

在第一页前面插入一个最后一页，在最后一页后面插入一个第一页，这样用户在第一页继续向右滑动时，便可滑动看到最后一页，同理，用户在最后一页向左滑动时便可滑动到第一页。

用户虽然只看到三张图片，实际上却是五张，当在view4的时候自动切换到view5时，进行判断让到切换到view2，这样造成的感觉就是最后一张下来是第一张，当在view2的时候切换到view1时，进行判断让切换到view4。

我们利用viewpage自带的方法切换界面立即切换没有滚动效果 当图片一样的时候是看不出图片变化的

```java
setCurrentItem(int item, boolean smoothScroll)
第二个参数设置false 界面切换的时候无滚动效果 默认true
```
下面是代码实现
layout_banner.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    xmlns:tools="http://schemas.android.com/tools">

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/banner_view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="30dp"
        android:layout_gravity="bottom"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:background="#33000000">
        <!-- 标题-->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@android:color/white"
            android:layout_marginStart="10dp"
            android:layout_gravity="start"
            android:id="@+id/banner_title"
            tools:text="title"/>
        <!-- 小圆点-->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/banner_indicator"
            android:orientation="horizontal"
            android:gravity="end"
            android:layout_marginEnd="10dp"
            android:padding="10dp">
        </LinearLayout>
    </LinearLayout>

</FrameLayout>
```
BannerViewPager.java
```java
package com.ryd.banner.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ryd.banner.R;
import com.ryd.banner.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 12/29/22 4:23 PM
 * @desc : com.ryd.banner
 */
public class BannerViewPager extends FrameLayout {

    private ViewPager viewPager;
    private TextView tvTitle;
    private LinearLayout indicatorGroup;
    private BannerAdapter adapter;
    private List<String> titles;//标题集合
    private List imageUrls;//图片数据
    private List<View> views;//轮播图显示
    private ImageView[] tips;//保存显示的小圆点
    private int count;//保存imageUrls的总数
    private int bannerTime = 2500;//轮播图的间隔时间
    private int currentItem = 0;//轮播图的当前选中项
    private long releaseTime = 0;//保存触发时手动滑动的时间 进行判断防止滑动之后立即轮播
    private final int START = 10;
    private final int STOP = 20;
    private Context context;
    private Handler handler;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            if (now - releaseTime > bannerTime - 500) {
                handler.sendEmptyMessage(START);
            } else {
                handler.sendEmptyMessage(STOP);
            }
        }
    };


    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        titles = new ArrayList<>();
        titles.add("标题1");
        titles.add("标题2");
        titles.add("标题3");
        imageUrls = new ArrayList();
        views = new ArrayList<>();
        init(context, attrs);
    }


    private void init(final Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_banner, this);
        viewPager = (ViewPager) view.findViewById(R.id.banner_view_pager);
        tvTitle = (TextView) view.findViewById(R.id.banner_title);
        indicatorGroup = (LinearLayout) view.findViewById(R.id.banner_indicator);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case START:
                        viewPager.setCurrentItem(currentItem + 1);
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, bannerTime);
                        break;
                    case STOP:
                        releaseTime = 0;
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, bannerTime);
                        break;
                }
            }
        };
    }

    /**
     * 初始化数据 以及拿到数据后的各种设置
     * 可以是网络地址 也可是项目图片数据
     *
     * @param imageUrls
     */
    public void setData(List<?> imageUrls) {
        this.imageUrls.clear();
        this.count = imageUrls.size();

        //this.imageUrls.add(imageUrls.get(count-1));
        this.imageUrls.addAll(imageUrls);
        //this.imageUrls.add(imageUrls.get(0));

        initIndicator();
        getShowView();
        setUI();
    }

    /**
     * 设置标题
     *
     * @param titles
     */
    public void setTitles(List<String> titles) {
        this.titles.clear();
        this.titles.addAll(titles);
    }

    /**
     * 设置小圆点指示器
     */
    private void initIndicator() {
        tips = new ImageView[count-2];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutParams.height = Utils.dip2px(context,10);
        layoutParams.width = Utils.dip2px(context,10);
        layoutParams.leftMargin = Utils.dip2px(context,5);// 设置点点点view的左边距
        layoutParams.rightMargin = Utils.dip2px(context,5);// 设置点点点view的右边距
        for (int i = 0; i < count-2; i++) {
            ImageView imageView = new ImageView(context);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.shape_circle_red);
            } else {
                imageView.setBackgroundResource(R.drawable.shape_circle_white);
            }

            tips[i] = imageView;
            indicatorGroup.addView(imageView, layoutParams);
        }
    }

    /**
     * 获取显示图片view
     */
    private void getShowView() {
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (imageUrls.get(i) instanceof String) {

            } else {
                imageView.setImageResource((Integer) imageUrls.get(i));
            }
            views.add(imageView);
        }
    }

    /**
     * 设置UI
     */
    private void setUI() {
        adapter = new BannerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(onPageChangeLis);
        viewPager.setCurrentItem(1);
        handler.postDelayed(runnable, bannerTime);
    }


    // 第一页到第二页
    //  onPageScrollStateChanged: state 1
    //  onPageScrolled: position 0 positionOffset 0.031481482 positionOffsetPixels 34
    //  onPageScrolled: position 0 positionOffset 0.107407406 positionOffsetPixels 116
    //  onPageScrolled: position 0 positionOffset 0.18888889 positionOffsetPixels 204
    //  onPageScrolled: position 0 positionOffset 0.20462963 positionOffsetPixels 221
    //  onPageScrollStateChanged: state 2
    //  onPageSelected: position 1
    //  onPageScrolled: position 0 positionOffset 0.28148147 positionOffsetPixels 304
    //  onPageScrolled: position 0 positionOffset 0.44351852 positionOffsetPixels 479
    //  onPageScrolled: position 0 positionOffset 0.5685185 positionOffsetPixels 614
    //  onPageScrolled: position 0 positionOffset 0.675 positionOffsetPixels 729
    //  onPageScrolled: position 0 positionOffset 0.7592593 positionOffsetPixels 820
    //  onPageScrolled: position 0 positionOffset 0.82222223 positionOffsetPixels 888
    //  onPageScrolled: position 0 positionOffset 0.87314814 positionOffsetPixels 943
    //  onPageScrolled: position 0 positionOffset 0.912037 positionOffsetPixels 985
    //  onPageScrolled: position 0 positionOffset 0.9388889 positionOffsetPixels 1014
    //  onPageScrolled: position 0 positionOffset 0.96018517 positionOffsetPixels 1037
    //  onPageScrolled: position 0 positionOffset 0.975 positionOffsetPixels 1053
    //  onPageScrolled: position 0 positionOffset 0.98425925 positionOffsetPixels 1063
    //  onPageScrolled: position 0 positionOffset 0.9916667 positionOffsetPixels 1071
    //  onPageScrolled: position 0 positionOffset 0.9953704 positionOffsetPixels 1075
    //  onPageScrolled: position 0 positionOffset 0.99814814 positionOffsetPixels 1078
    //  onPageScrolled: position 0 positionOffset 0.9990741 positionOffsetPixels 1079
    //  onPageScrolled: position 1 positionOffset 0.0 positionOffsetPixels 0
    //  onPageScrollStateChanged: state 0

    // 第二页到第三页
    // onPageScrollStateChanged: state 1
    // onPageScrolled: position 1 positionOffset 0.01111114 positionOffsetPixels 12
    // onPageScrolled: position 1 positionOffset 0.06666672 positionOffsetPixels 72
    // onPageScrolled: position 1 positionOffset 0.1240741 positionOffsetPixels 134
    // onPageScrolled: position 1 positionOffset 0.19259262 positionOffsetPixels 208
    // onPageScrolled: position 1 positionOffset 0.2527778 positionOffsetPixels 273
    // onPageScrolled: position 1 positionOffset 0.27592587 positionOffsetPixels 297
    // onPageScrollStateChanged: state 2
    // onPageSelected: position 2
    // onPageScrolled: position 1 positionOffset 0.30277777 positionOffsetPixels 327
    // onPageScrolled: position 1 positionOffset 0.4074074 positionOffsetPixels 440
    // onPageScrolled: position 1 positionOffset 0.4990741 positionOffsetPixels 539
    // onPageScrolled: position 1 positionOffset 0.57407403 positionOffsetPixels 619
    // onPageScrolled: position 1 positionOffset 0.64444447 positionOffsetPixels 696
    // onPageScrolled: position 1 positionOffset 0.70092595 positionOffsetPixels 757
    // onPageScrolled: position 1 positionOffset 0.7537037 positionOffsetPixels 814
    // onPageScrolled: position 1 positionOffset 0.79814816 positionOffsetPixels 862
    // onPageScrolled: position 1 positionOffset 0.8342593 positionOffsetPixels 901
    // onPageScrolled: position 1 positionOffset 0.8666667 positionOffsetPixels 936
    // onPageScrolled: position 1 positionOffset 0.89351857 positionOffsetPixels 965
    // onPageScrolled: position 1 positionOffset 0.91481483 positionOffsetPixels 988
    // onPageScrolled: position 1 positionOffset 0.9342593 positionOffsetPixels 1009
    // onPageScrolled: position 1 positionOffset 0.94907403 positionOffsetPixels 1025
    // onPageScrolled: position 1 positionOffset 0.96111107 positionOffsetPixels 1038
    // onPageScrolled: position 1 positionOffset 0.9703704 positionOffsetPixels 1048
    // onPageScrolled: position 1 positionOffset 0.97870374 positionOffsetPixels 1057
    // onPageScrolled: position 1 positionOffset 0.98425925 positionOffsetPixels 1063
    // onPageScrolled: position 1 positionOffset 0.98888886 positionOffsetPixels 1068
    // onPageScrolled: position 1 positionOffset 0.9925926 positionOffsetPixels 1072
    // onPageScrolled: position 1 positionOffset 0.9944445 positionOffsetPixels 1074
    // onPageScrolled: position 1 positionOffset 0.9962963 positionOffsetPixels 1076
    // onPageScrolled: position 1 positionOffset 0.9981482 positionOffsetPixels 1078
    // onPageScrolled: position 1 positionOffset 0.9990741 positionOffsetPixels 1079
    // onPageScrolled: position 2 positionOffset 0.0 positionOffsetPixels 0
    // onPageScrollStateChanged: state 0

    // 第三页到第二页
    // onPageScrollStateChanged: state 1
    // onPageScrolled: position 1 positionOffset 0.95277774 positionOffsetPixels 1029
    // onPageScrolled: position 1 positionOffset 0.88796294 positionOffsetPixels 959
    // onPageScrolled: position 1 positionOffset 0.83611107 positionOffsetPixels 902
    // onPageScrolled: position 1 positionOffset 0.82592595 positionOffsetPixels 892
    // onPageScrollStateChanged: state 2
    // onPageSelected: position 1
    // onPageScrolled: position 1 positionOffset 0.7851852 positionOffsetPixels 848
    // onPageScrolled: position 1 positionOffset 0.66388893 positionOffsetPixels 717
    // onPageScrolled: position 1 positionOffset 0.55277777 positionOffsetPixels 597
    // onPageScrolled: position 1 positionOffset 0.45648146 positionOffsetPixels 492
    // onPageScrolled: position 1 positionOffset 0.3787037 positionOffsetPixels 409
    // onPageScrolled: position 1 positionOffset 0.30833328 positionOffsetPixels 332
    // onPageScrolled: position 1 positionOffset 0.2490741 positionOffsetPixels 269
    // onPageScrolled: position 1 positionOffset 0.20092595 positionOffsetPixels 217
    // onPageScrolled: position 1 positionOffset 0.1592592 positionOffsetPixels 171
    // onPageScrolled: position 1 positionOffset 0.1240741 positionOffsetPixels 134
    // onPageScrolled: position 1 positionOffset 0.09722221 positionOffsetPixels 104
    // onPageScrolled: position 1 positionOffset 0.07407403 positionOffsetPixels 79
    // onPageScrolled: position 1 positionOffset 0.055555582 positionOffsetPixels 60
    // onPageScrolled: position 1 positionOffset 0.041666627 positionOffsetPixels 44
    // onPageScrolled: position 1 positionOffset 0.030555606 positionOffsetPixels 33
    // onPageScrolled: position 1 positionOffset 0.021296263 positionOffsetPixels 22
    // onPageScrolled: position 1 positionOffset 0.014814854 positionOffsetPixels 16
    // onPageScrolled: position 1 positionOffset 0.010185242 positionOffsetPixels 11
    // onPageScrolled: position 1 positionOffset 0.0064815283 positionOffsetPixels 7
    // onPageScrolled: position 1 positionOffset 0.004629612 positionOffsetPixels 4
    // onPageScrolled: position 1 positionOffset 0.0027778149 positionOffsetPixels 3
    // onPageScrolled: position 1 positionOffset 9.2589855E-4 positionOffsetPixels 0
    // onPageScrolled: position 1 positionOffset 0.0 positionOffsetPixels 0
    // onPageScrollStateChanged: state 0

    // 第二页到第一页
    // onPageScrollStateChanged: state 1
    // onPageScrolled: position 0 positionOffset 0.9592593 positionOffsetPixels 1036
    // onPageScrolled: position 0 positionOffset 0.8666667 positionOffsetPixels 936
    // onPageScrolled: position 0 positionOffset 0.75185186 positionOffsetPixels 812
    // onPageScrolled: position 0 positionOffset 0.7185185 positionOffsetPixels 776
    // onPageScrollStateChanged: state 2
    // onPageSelected: position 0
    // onPageScrolled: position 0 positionOffset 0.65185183 positionOffsetPixels 704
    // onPageScrolled: position 0 positionOffset 0.46203703 positionOffsetPixels 499
    // onPageScrolled: position 0 positionOffset 0.31851852 positionOffsetPixels 344
    // onPageScrolled: position 0 positionOffset 0.21851853 positionOffsetPixels 236
    // onPageScrolled: position 0 positionOffset 0.14166667 positionOffsetPixels 153
    // onPageScrolled: position 0 positionOffset 0.08796296 positionOffsetPixels 95
    // onPageScrolled: position 0 positionOffset 0.053703703 positionOffsetPixels 58
    // onPageScrolled: position 0 positionOffset 0.030555556 positionOffsetPixels 33
    // onPageScrolled: position 0 positionOffset 0.015740741 positionOffsetPixels 17
    // onPageScrolled: position 0 positionOffset 0.0074074073 positionOffsetPixels 8
    // onPageScrolled: position 0 positionOffset 0.0027777778 positionOffsetPixels 3
    // onPageScrolled: position 0 positionOffset 9.259259E-4 positionOffsetPixels 1
    // onPageScrolled: position 0 positionOffset 0.0 positionOffsetPixels 0
    // onPageScrollStateChanged: state 0
    /**
     * viewPage改变监听
     * 用于响应所选页面
     */
    private ViewPager.OnPageChangeListener onPageChangeLis = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.d("ruanyandong", "onPageScrolled: position "+position+" positionOffset "+positionOffset+" positionOffsetPixels "+positionOffsetPixels);
            // 第一页到第二页  position 0->1  positionOffset 0->1->0  positionOffsetPixels 0->划过一页的距离->0
            // 第二页到第三页  position 1->2  positionOffset 0->1->0  positionOffsetPixels 0->划过一页的距离->0
            // 第三页到第二页  position 一直是1  positionOffset 1->0  positionOffsetPixels 一页的距离->0
            // 第二页到第一页  position 一直是0  positionOffset 1->0  positionOffsetPixels 一页的距离->0
        }

        @Override
        public void onPageSelected(int position) {
            Log.d("ruanyandong", "onPageSelected: position "+position);
            // 每滑动一次都只打印一次方法，position打印最终选定页面的下标

            //计算当前页的下标，用于指示器改变
            int max = views.size() - 1;
            int temp = position;
            currentItem = position;
            if (position == 0) { // 当滑动到下标为0时，其实对于用户来说看到的是最后一个，所以需要将下标替换成倒数第二个
                currentItem = max - 1;
            } else if (position == max) {// 当滑动到最后一个时，其实对于用户来说看到的是第一个，所以需要将下标替换成正数第二个
                currentItem = 1;
            }
            temp = currentItem - 1;
            setIndicatorAndTitle(temp);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            currentItem = viewPager.getCurrentItem();
            Log.d("ruanyandong", "onPageScrollStateChanged: state "+state+" currentItem "+currentItem);
            // 0 静止  1 正在拖拽   2 松手后自动滑动
            // 每次拖动的状态变化都是 1->2->0  每次拖动一页，都会调用3次方法，依次打印1->2->0
            switch (state) {
                case 0: // 静止状态
                    //Log.e("aaa","=====静止状态======");
                    if (currentItem == 0) {
                        viewPager.setCurrentItem(count-2, false);
                    } else if (currentItem == count - 1) {
                        viewPager.setCurrentItem(1, false);
                    }
                    break;
                case 1: // 拖拽状态
//    Log.e("aaa","=======手动拖拽滑动时调用====");
                    releaseTime = System.currentTimeMillis();
                    if (currentItem == count - 1) {
                        viewPager.setCurrentItem(1, false);
                    } else if (currentItem == 0) {
                        viewPager.setCurrentItem(count-2, false);
                    }
                    break;
                case 2: // 松手自动滑动状态
//    Log.e("aaa","=======自动滑动时调用====");
                    break;
            }
        }
    };


    /**
     * 设置指示器和标题切换
     *
     * @param position
     */
    private void setIndicatorAndTitle(int position) {
        tvTitle.setText(titles.get(position));

        for (int i = 0; i < tips.length; i++) {
            if (i == position) {
                tips[i].setBackgroundResource(R.drawable.shape_circle_red);
            } else {
                tips[i].setBackgroundResource(R.drawable.shape_circle_white);
            }
        }
    }

    /**
     * 适配器
     */
    class BannerAdapter extends PagerAdapter {

        /**
         * 返回可用的视图数量
         * @return
         */
        @Override
        public int getCount() {
            return views.size();
        }


        /**
         * 判断当前View是否是来自于object
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        /**
         * 创建初始化View，如果总共有三个View，刚开始会创建好第一二两个，当从第一个滑动到第二个时，会创建第三个view
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        /**
         * 销毁view，当滑动到第三个时会销毁第一个，当滑动到第一个时会销毁第三个
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }


    }

}

```

layout_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".banner.MainActivity">

    <com.ryd.banner.banner.BannerViewPager
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="200dp"/>

</FrameLayout>
```
MainActivity.java
```java
package com.ryd.banner.banner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ryd.banner.R;
import com.ryd.banner.viewpager2.ViewPager2Activity;
import java.util.ArrayList;
import java.util.List;

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

    }


}
```





