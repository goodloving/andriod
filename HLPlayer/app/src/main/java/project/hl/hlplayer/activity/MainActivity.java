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
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.base.BasePage;
import project.hl.hlplayer.pager.FindPager;
import project.hl.hlplayer.pager.LivePager;
import project.hl.hlplayer.pager.MusicPager;
import project.hl.hlplayer.pager.MyPager;
import project.hl.hlplayer.pager.VedioPager;

public class MainActivity extends AppCompatActivity {
    //实例化控件
    private RadioGroup rg_botton_tag;
    //将各个页面存入的集合
    private static ArrayList<BasePage> basePages;
    //选中的fragement的位置
    private static Integer position;


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
        basePages = new ArrayList<>();
        basePages.add(new MusicPager(this));
        basePages.add(new VedioPager(this));
        basePages.add(new FindPager(this));
        basePages.add(new LivePager(this));
        basePages.add(new MyPager(this));

        /**
         *设置radiobutton的监听来改变界面
         */
        rg_botton_tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){                 //选中不同的 radiobutton 时定位到不同的 position
                    default:
                        position = 0;
                        break;
                    case R.id.rb_vedio:
                        position = 1;
                        break;
                    case R.id.rb_find:
                        position = 2;
                        break;
                    case R.id.rb_live:
                        position = 3;
                        break;
                    case R.id.rb_mycenter:
                        position = 4;
                        break;
                }
                setFragment();         //将页面设置到Fragment中
            }
        });
        rg_botton_tag.check(R.id.rb_music);             //设置默认选中音乐的radiobutton

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

