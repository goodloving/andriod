package project.hl.hlplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import project.hl.hlplayer.base.BasePage;

public class MyPager extends BasePage {
    //该页面的控件
    private TextView textView;
    /**
     * 重写构造函数
     * @param context
     */
    public MyPager(Context context) {
        super(context);
    }

    /**
     * 继承父类强制执行的方法，初始化该页面的控件
     * @return
     */
    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    /**
     * 重写父类初始化数据的方法
     */
    @Override
    public void initData() {
        super.initData();
        textView.setText("个人中心");
    }
}
