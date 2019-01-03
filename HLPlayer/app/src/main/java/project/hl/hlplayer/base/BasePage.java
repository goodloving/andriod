package project.hl.hlplayer.base;

import android.content.Context;
import android.view.View;

public abstract class BasePage {
    //上下文
    public final Context context;
    public View rootView;
    public boolean isInitData;

    public BasePage(Context context){
        this.context = context;
        rootView = initView();
    }

    /**
     * 强制其继承类实现initView方法，达到实现特定的效果
     * @return
     */
    public abstract View initView();

    /**
     * 子页面需要初始化数据的时候重写，绑定数据
     */
    public void  initData(){

    }

}
