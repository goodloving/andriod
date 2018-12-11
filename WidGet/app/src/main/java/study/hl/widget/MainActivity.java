package study.hl.widget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView myTextView;
    private Button myButton;
    private ImageButton myImageButton;

    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private String msg;

    private Spinner sp1;

    private TextView tv1;
    private ListView lv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //加载界面
        initViews();
        //button和imagebutton学习
        studyButtons();
        //checkbox学习
        studyCheckBox();
        //listview学习
        studyListView();

    }

    //初始化界面
    private void initViews(){
        myTextView = findViewById(R.id.textView1);
        myButton = findViewById(R.id.button1);
        myImageButton = findViewById(R.id.imageButton1);

        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);

        //学习spinner
        sp1 = findViewById(R.id.spinner1);
        List<String> list = new ArrayList<>();
        list.add("学士");
        list.add("硕士");
        list.add("博士");
        list.add("博士后");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);

        //添加listview数据
        listViewBingAdapter();

    }

    //button和imagebutton控件study
    private void studyButtons(){
        Button.OnClickListener bListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button1:
                        myTextView.setText("button按钮");
                        myButton.setText("确定");
                        myImageButton.setImageResource(R.mipmap.ic_launcher);
                        return;
                    case R.id.imageButton1:
                        myTextView.setText("imagebutton按钮");
                        myButton.setText("OK");
                        myImageButton.setImageResource(R.mipmap.ic_launcher_round);
                        return;
                }
            }
        };
        myButton.setOnClickListener(bListener);
        myImageButton.setOnClickListener(bListener);
    }

    //checkBox控件学习-------用法同radiobutton（radioGroup）
    private void studyCheckBox(){
        myTextView.setText("湖北省市：");
        CheckBox.OnClickListener cListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cb1:
                        if(cb1.isChecked()){
                            msg += cb1.getText().toString() + " ";
                            myTextView.setText(msg);
                        }
                        return;
                    case R.id.cb2:
                        if(cb2.isChecked()){
                            msg += cb2.getText().toString() + " ";
                            myTextView.setText(msg);
                        }
                        return;
                    case R.id.cb3:
                        if(cb3.isChecked()){
                            msg += cb3.getText().toString() + " ";
                            myTextView.setText(msg);
                        }
                        return;
                }
            }
        };
        cb1.setOnClickListener(cListener);
        cb2.setOnClickListener(cListener);
        cb3.setOnClickListener(cListener);
    }

    //listview数据和适配器绑定
    private void listViewBingAdapter(){
        tv1 = findViewById(R.id.textView2);
        lv1 = findViewById(R.id.listView1);
        List<String> list = new ArrayList<>();
        list.add("学士");
        list.add("硕士");
        list.add("博士");
        list.add("博士后");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        lv1.setAdapter(adapter);
    }
    //listview控件监听
    private void studyListView(){
        AdapterView.OnItemClickListener vListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = "";
                if(position==0) msg="学士";
                if(position==1) msg="硕士";
                if(position==2) msg="博士";
                if(position==3) msg="博士后";
                tv1.setText(msg);
            }
        };
        lv1.setOnItemClickListener(vListener);
    }

}