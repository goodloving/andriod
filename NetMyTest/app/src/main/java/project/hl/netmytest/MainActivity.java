package project.hl.netmytest;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btn_click(View view) {
        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2019/01/02/mp4/190102174213856159.mp4"),"video/*");
        intent.setDataAndType(Uri.parse("http://94.191.89.177:8080/hbsp.mp4"),"video/*");
        startActivity(intent);
    }
}
