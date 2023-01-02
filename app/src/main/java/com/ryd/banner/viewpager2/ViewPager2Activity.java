package com.ryd.banner.viewpager2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import com.ryd.banner.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Activity extends AppCompatActivity {

    private List<ViewModel> mViewModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2);

        initData();

        ViewPager2 horizontalViewPager2 = findViewById(R.id.horizontal_view_pager2);
        HorizontalViewPager2Adapter horizontalViewPager2Adapter = new HorizontalViewPager2Adapter(mViewModels);
        horizontalViewPager2.setAdapter(horizontalViewPager2Adapter);


        ViewPager2 verticalViewPager2 = findViewById(R.id.vertical_view_pager2);
        verticalViewPager2.setAdapter(horizontalViewPager2Adapter);

    }

    private void initData() {
        for (int i = 0; i < 3; i++) {
            ViewModel viewModel1 = new ViewModel("气泡",R.drawable.bubble);
            mViewModels.add(viewModel1);

            ViewModel viewModel2 = new ViewModel("青草",R.drawable.grass);
            mViewModels.add(viewModel2);

            ViewModel viewModel3 = new ViewModel("水",R.drawable.water);
            mViewModels.add(viewModel3);
        }
    }


}