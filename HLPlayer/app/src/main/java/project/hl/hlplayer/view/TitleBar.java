package project.hl.hlplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import project.hl.hlplayer.R;

/**
 * 自定义标题栏
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View iv_tb_search;
    private Context context;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 当布局文件加载完成的时候回调该方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例
        iv_tb_search = getChildAt(2);
        //设置点击事件
        iv_tb_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_tb_search:
                Toast.makeText(context,"搜索",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
