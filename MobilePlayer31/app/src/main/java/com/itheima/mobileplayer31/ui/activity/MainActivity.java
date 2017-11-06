package com.itheima.mobileplayer31.ui.activity;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.adapter.MainAdadpter;
import com.itheima.mobileplayer31.ui.fragment.AudioFragment;
import com.itheima.mobileplayer31.ui.fragment.VideoFramgent;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private MainAdadpter adadpter;
    private View main_mark;
    private TextView main_video;
    private TextView main_audio;
    private int green;
    private int halfWhite;

    @Override
    protected void initData() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new VideoFramgent());
        fragments.add(new AudioFragment());
        adadpter.updateFragments(fragments);

        //初始化指示器宽度
        initMarkWidth();


        green = getResources().getColor(R.color.green);
        halfWhite = getResources().getColor(R.color.halfwhite);

        handleTitle(0);
    }

    private void initMarkWidth() {
        //获取屏幕宽度
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int windowW = point.x;
        main_mark.getLayoutParams().width = windowW/2;
        //刷新
        main_mark.requestLayout();
    }

    @Override
    protected void initListener() {
        adadpter = new MainAdadpter(getSupportFragmentManager());
        viewPager.setAdapter(adadpter);
        main_video.setOnClickListener(this);
        main_audio.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        main_mark = findViewById(R.id.main_mark);
        main_video = (TextView) findViewById(R.id.main_video);
        main_audio = (TextView) findViewById(R.id.main_audio);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processClick(View v) {
        //标题状态改变
        //指示器滑动
        //viewpager选中状态改变
        switch (v.getId()){
            case R.id.main_video:
                viewPager.setCurrentItem(0);
                break;
            case R.id.main_audio:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    /**
     * viewpager滑动
     * @param position 滑动前的选中条目
     * @param positionOffset 滑动偏移百分比
     * @param positionOffsetPixels  滑动像素
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        偏移x=偏移百分比*指示器宽度
        float offsetX = positionOffset*main_mark.getWidth();//需要view执行完onmeasure和onlayout方法之后
//        开始x=position*指示器宽度
        float startX = position*main_mark.getWidth();
//        指示器x=开始x+偏移x
        float finalX = startX+offsetX;

//        ViewCompat.animate()
        ViewCompat.setTranslationX(main_mark,finalX);
    }

    /**
     * 页面选中状态改变
     * @param position  改变后的页面position
     */
    @Override
    public void onPageSelected(int position) {
        handleTitle(position);
    }
    //处理viewpager页面选中状态改变 标题处理
    private void handleTitle(int position) {
        if(position==0){
            main_video.setTextColor(green);
            main_audio.setTextColor(halfWhite);

//            ViewCompat.getScaleX();
            ViewCompat.animate(main_video).scaleX(1.2f).scaleY(1.2f);
            ViewCompat.animate(main_audio).scaleX(1.0f).scaleY(1.0f);
        }else if(position==1) {
            main_video.setTextColor(halfWhite);
            main_audio.setTextColor(green);
            ViewCompat.animate(main_audio).scaleX(1.2f).scaleY(1.2f);
            ViewCompat.animate(main_video).scaleX(1.0f).scaleY(1.0f);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
