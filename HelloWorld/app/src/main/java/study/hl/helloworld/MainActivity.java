package study.hl.helloworld;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //edit/text
    private TextView myTextView;
    private EditText myEditView;
    private TextView textView2B;
    private Button myButton;
    private ImageButton myImageButton;

    private void findViews(){
        //edit/text例子
        myTextView = findViewById(R.id.textView);
        myEditView = findViewById(R.id.editText);
        myTextView.setText("@string/hl");
        myEditView.setText("haolian");
        //button/imagebutton视图
        textView2B.findViewById(R.id.textView2Button);
        myButton.findViewById(R.id.button1);
        myImageButton.findViewById(R.id.imgButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        //button监听事件
        Button.OnClickListener bListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button1:
                        myTextView.setText("Button按钮");
                        myButton.setText("确定");
                        myImageButton.setImageResource(R.mipmap.yes);
                        return;
                    case R.id.imgButton:
                        myTextView.setText("imageButton按钮");
                        myButton.setText("OK");
                        myImageButton.setImageResource(R.mipmap.crystal);
                        return;
                }
            }
        };
        myButton.setOnClickListener(bListener);
        myImageButton.setOnClickListener(bListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
