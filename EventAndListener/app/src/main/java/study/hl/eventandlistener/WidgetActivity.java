package study.hl.eventandlistener;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WidgetActivity extends AppCompatActivity{
    private WebView WebImg;
    private WebView WebText;
    private WebView WebClick;
    private final String mineType = "text/html";
    private final String encoding = "utf-8";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        initView();
        loadWebPages();
        WebClick.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    //加载网页
    private void loadWebPages() {
        WebImg.loadDataWithBaseURL(null, "<a href='http://www.njnu.edu.cn'><img src='file:///android_res/drawable/yes.jpg'/></a>", mineType, encoding, null);
        WebText.loadDataWithBaseURL(null, "<a href='http://computer.njnu.edu.cn'>计算机科学与技术学院</a>", mineType, encoding, null);
        WebClick.getSettings().setJavaScriptEnabled(true);
        WebClick.loadUrl("http://www.hxedu.com.cn");
    }

    private void initView(){
        WebImg = findViewById(R.id.webImg);
        WebText = findViewById(R.id.webText);
        WebClick = findViewById(R.id.webClick);
    }

    //载入网页
    public void onOKClick(View view) {
        WebClick.loadDataWithBaseURL(null, "<a href='http://www.hxedu.com.cn/hxedu/fg/book/bookinfo.html?code=G0232700'><img src='file:///android_res/drawable/crystal.png'/></a>", mineType, encoding, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && WebClick.canGoBack()){
            WebClick.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    //下一页，页面跳转
    public void onNextPage(View view) {
        Intent it = new Intent(WidgetActivity.this, ImageSwitcherActivity.class);
        startActivity(it);
        finish();
    }
}
