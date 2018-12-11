package study.hl.demotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        ImageView splashView = findViewById(R.id.splashView);
        splashView.postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpToGuideActivity();
            }
        }, 1500);
    }

    private void jumpToGuideActivity(){
        Intent it = new Intent(this, GuideActivity.class);
        startActivity(it);
        finish();
    }
}
