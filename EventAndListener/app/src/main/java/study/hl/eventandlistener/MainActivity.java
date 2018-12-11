package study.hl.eventandlistener;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {
    private EditText myET;
    private TextView myTV;
    private CheckBox myCB;

    private TextView touchView;
    private TextView historyView;
    private TextView eventView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //加载界面控件
        //initViews();

        //studyKeyEvent();

        //studyTouckEvent();

    }

    //初始化界面
    private void initViews(){
        myET = findViewById(R.id.entry);
        myCB = findViewById(R.id.cb1);
        myTV = findViewById(R.id.tv1);

        touchView = findViewById(R.id.tv2);
        historyView = findViewById(R.id.tv3);
        eventView = findViewById(R.id.tv4);

    }

    //按键事件学习
    private void studyKeyEvent(){
        myET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int metaState = event.getMetaState();
                int unicodeChar = event.getUnicodeChar();
                String msg = "";
                msg += "按键动作：" + String.valueOf(event.getAction()) + "\n";
                msg += "按键代码：" + String.valueOf(keyCode) + "\n";
                msg += "按键字符：" + (char)unicodeChar + "\n";
                msg += "UNICODE：" + String.valueOf(unicodeChar) + "\n";
                msg += "重复次数：" + String.valueOf(event.getRepeatCount()) + "\n";
                msg += "功能键状态：" + String.valueOf(metaState) + "\n";
                msg += "硬件编码：" + String.valueOf(event.getScanCode()) + "\n";
                msg += "按键标志：" + String.valueOf(event.getFlags()) + "\n";
                myTV.setText(msg);
                if(myCB.isChecked()) return true;
                return false;
            }
        });
    }

    //触摸事件学习
    private void studyTouckEvent(){
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        display("触击屏幕", event);
                        break;
                    case MotionEvent.ACTION_UP:
                        processHistory(event);
                        display("离开屏幕", event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        display("屏幕上移动", event);
                        break;
                }
                return true;
            }
        });
    }
    private void display(String eventType, MotionEvent event){
        int x = (int)event.getX();
        int y = (int)event.getY();
        int rawX = (int)event.getRawX();
        int rawY = (int)event.getRawY();
        float pressure = event.getPressure();
        float size = event.getSize();

        String msg = "";
        msg += "事件类型：" + eventType + "\n";
        msg += "相对坐标：(" + String.valueOf(x) + "," + String.valueOf(y) + ")\n";
        msg += "绝对坐标：(" + String.valueOf(rawX) + "," + String.valueOf(rawY) + ")\n";
        msg += "触点压力：" + String.valueOf(pressure)+ "\n";
        msg += "触点尺寸：" + String.valueOf(size)+ "\n";
        eventView.setText(msg);
    }
    private void processHistory(MotionEvent event){
        int historySize = event.getHistorySize();
        for(int i=0;i<historySize;i++){
            long time = event.getHistoricalEventTime(i);
            float x = event.getHistoricalX(i);
            float y = event.getHistoricalY(i);
            float pressure = event.getHistoricalPressure(i);
            float size = event.getHistoricalSize(i);
        }
        historyView.setText("历史数据量：" + historySize);
    }

    //button跳转到下一页
    private void onNextClick() {
        Button btn = findViewById(R.id.buttonNext);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, WidgetActivity.class);
                startActivity(it);
            }
        });

    }

    public void onNext(View view) {
        Intent it = new Intent(MainActivity.this, WidgetActivity.class);
        startActivity(it);
        finish();
    }
}
