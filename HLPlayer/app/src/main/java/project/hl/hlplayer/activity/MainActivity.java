package project.hl.hlplayer.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.base.BasePage;
import project.hl.hlplayer.pager.FindPager;
import project.hl.hlplayer.pager.LivePager;
import project.hl.hlplayer.pager.LocalMusicPager;
import project.hl.hlplayer.pager.LocalVedioPager;
import project.hl.hlplayer.pager.MusicPager;
import project.hl.hlplayer.pager.MyPager;
import project.hl.hlplayer.pager.NetMusicPager;
import project.hl.hlplayer.pager.NetVedioPager;
import project.hl.hlplayer.pager.VedioPager;

public class MainActivity extends AppCompatActivity {
    //实例化控件
    private RadioGroup rg_botton_tag;
    private RadioGroup rg_tb;
    //将各个页面存入的集合
    private static ArrayList<BasePage> basePages;
    //选中的fragement的位置
    private static Integer position;
    private TextView tv_tb;
    private View include_layout;

    private RadioButton local;
    private RadioButton net;

    private RadioButton rb_music;
    private RadioButton rb_vedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();                 //调用初始化界面的方法
    }

    /*
        在初始化界面控件
     */
    public void initView(){
        rg_botton_tag = findViewById(R.id.rg_botton_tag);
        rg_tb = findViewById(R.id.rg_tb);
        tv_tb = findViewById(R.id.tv_tb_tag);
        include_layout = findViewById(R.id.include_layout);

        rb_music = findViewById(R.id.rb_music);
        rb_vedio = findViewById(R.id.rb_vedio);

        local = findViewById(R.id.rb_tb_local);
        net = findViewById(R.id.rb_tb_net);

        basePages = new ArrayList<>();
        basePages.add(new LocalMusicPager(this));
        basePages.add(new NetMusicPager(this));
        basePages.add(new LocalVedioPager(this));
        basePages.add(new NetVedioPager(this));
        basePages.add(new FindPager(this));
        basePages.add(new LivePager(this));
        basePages.add(new MyPager(this));

        /**
         *设置radiobutton的监听来改变界面
         */
        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {                 //选中不同的 radiobutton 时定位到不同的 position
                    case R.id.rb_music:
                        tv_tb.setText(R.string.tag_music);
                        include_layout.setVisibility(View.VISIBLE);
                        if(local.isChecked()){
                            position = 0;
                        }else {
                            position = 1;
                        }
                        break;
                    case R.id.rb_vedio:
                        tv_tb.setText(R.string.tag_vedio);
                        include_layout.setVisibility(View.VISIBLE);
                        if(local.isChecked()){
                            position = 2;
                        }else {
                            position = 3;
                        }
                        break;
                    case R.id.rb_find:
                        position = 4;
                        //tv_tb.setText(R.string.tag_find);
                        include_layout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_live:
                        position = 5;
                        //tv_tb.setText(R.string.tag_live);
                        include_layout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_mycenter:
                        position = 6;
                        //tv_tb.setText(R.string.tag_mycenter);
                        include_layout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_tb_local:
                        if (rb_music.isChecked()) {
                            position = 0;
                        }
                        if(rb_vedio.isChecked()){
                            position = 2;
                        }
                        break;
                    case R.id.rb_tb_net:
                        if (rb_music.isChecked()) {
                            position = 1;
                        }
                        if(rb_vedio.isChecked()){
                            position = 3;
                        }
                        break;
                }
                setFragment();         //将页面设置到Fragment中
            }
        };
        rg_botton_tag.setOnCheckedChangeListener(listener);
        rg_tb.setOnCheckedChangeListener(listener);
        rg_botton_tag.check(R.id.rb_music);             //设置默认选中音乐的radiobutton
        rg_tb.check(R.id.rb_tb_local);

    }

    /**
     * 将页面设置到Fragment中
     */
    private void setFragment() {
        //1.得到FragmentMangeer
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content, new myOnCreateView());
        //4.提交事务
        ft.commit();
    }

    /**
     * 定义内部类进行传参,原来的api修改了，改成只能进static的重写方法
     */
    public static class  myOnCreateView  extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            BasePage basePage = getBasePager();
            if(basePage!=null){
                return basePage.rootView;
            }
            return null;
        }

        /**
         * 根据位置得到相应的pager
         * @return
         */
        private BasePage getBasePager() {
            BasePage basePager = basePages.get(position);
            if(basePager != null && !basePager.isInitData){
                basePager.initData();
                basePager.isInitData = true;
            }
            return basePager;
        }
    }

}

