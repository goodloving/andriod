package study.hl.eventandlistener;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ImageSwitcherActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory {
    private ImageSwitcher myImgSw;
    private int[] image = {R.mipmap.guide00, R.mipmap.guide01, R.mipmap.guide02, R.mipmap.splash};
    int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageswitcher);
        initView();
    }

    //加载界面i
    private void initView(){
        myImgSw = findViewById(R.id.imageSwitcher);
        myImgSw.setFactory(this);
        myImgSw.setImageResource(image[0]);
        index = 0;
    }

    //相片切换函数
    public void onPicture(View view) {
        switch (view.getId()){
            case R.id.btnForword:
                index --;
                if(index < 0){
                    index = image.length - 1;
                    Toast.makeText(this, "最后一张", Toast.LENGTH_SHORT);
                }
                myImgSw.setImageResource(image[index]);
                break;
            case R.id.btnNext:
                index ++;
                if(index >= image.length){
                    index = 0;
                    Toast.makeText(this, "第一张", Toast.LENGTH_SHORT);
                }
                myImgSw.setImageResource(image[index]);
                break;
        }
    }

    @Override
    public View makeView() {
        ImageView iv1 = new ImageView(this);
        iv1.setBackgroundColor(0x00000000);
        iv1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv1.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        return iv1;
    }
}
