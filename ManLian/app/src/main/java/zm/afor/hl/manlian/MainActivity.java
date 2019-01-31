package zm.afor.hl.manlian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView ivFirst;
    private ImageView ivSecond;
    private ImageView ivThrid;
    private ImageView ivFourth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews() {
        ivFirst = findViewById( R.id.iv_first );
        ivSecond = findViewById( R.id.iv_second );
        ivThrid = findViewById( R.id.iv_thrid );
        ivFourth = findViewById( R.id.iv_fourth );

        //设置监听
        ivFirst.setOnClickListener(this);
        ivSecond.setOnClickListener(this);
        ivThrid.setOnClickListener(this);
        ivFourth.setOnClickListener(this);
    }

    /**
     * 监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == ivFirst){
            Intent intent = new Intent(this,MemoryDayActivity.class);
            startActivity(intent);
        }else if(v == ivSecond){

        }
        else if(v == ivThrid){

        }
        else if(v == ivFourth){

        }
    }
}
