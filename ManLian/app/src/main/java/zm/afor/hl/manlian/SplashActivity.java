package zm.afor.hl.manlian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SplashActivity extends Activity{
    //存放图片的数组
    private final int[] resId = {R.drawable.huhu1, R.drawable.huhu2, R.drawable.huhu3, R.drawable.huhu4};
    private ViewPager vp_splash;
    private int currentPosition;
    private float startx;
    private float endx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findView();
    }

    /**
     * 控件实例化
     */
    public void findView() {
        vp_splash = findViewById(R.id.vp_splash);
        vp_splash.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return resId.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView iv = new ImageView(SplashActivity.this);
                iv.setBackgroundResource(resId[position]);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(iv);
                return iv;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });

        //设置监听，最后一很张滑动进入主页面
        vp_splash.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPosition = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        vp_splash.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startx = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        endx = event.getX();
                        break;
                }
                if(currentPosition == resId.length-1){
                    if (endx-startx<0){
                        if(Math.abs(endx-startx) >= 50){
                            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
                return  false;
            }
        });
    }
}
