package project.hl.hlplayer.pager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import project.hl.hlplayer.R;
import project.hl.hlplayer.base.BasePage;

public class LocalMusicPager extends BasePage {
    //该页面的控件
    private TextView textView;
    /**
     * 重写构造函数
     * @param context
     */
    public LocalMusicPager(Context context) {
        super(context);
    }

    /**
     * 继承父类强制执行的方法，初始化该页面的控件
     * @return
     */
    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(textView.getResources().getColor(R.color.colorBlack));
        textView.setText("本地音乐");
        textView.setTextSize(16);
        return textView;
    }

    /**
     * 重写父类初始化数据的方法
     */
    @Override
    public void initData() {
        super.initData();
    }
}
