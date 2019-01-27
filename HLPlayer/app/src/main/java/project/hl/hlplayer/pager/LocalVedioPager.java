package project.hl.hlplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.activity.MyVideoPalyer;
import project.hl.hlplayer.adapter.MyVedioPagerAdapter;
import project.hl.hlplayer.base.BasePage;
import project.hl.hlplayer.domain.MediaItem;

public class LocalVedioPager extends BasePage {
    //该页面的控件
    private ListView lv_vedio;
    private TextView tv_novedio;
    private ProgressBar pb_loading;
    //装数据的集合
    ArrayList<MediaItem> mediaItems;

    //自定义MyVedioPagerAdapter
    private MyVedioPagerAdapter myVedioPagerAdapter;

    /**
     * 重写构造函数
     * @param context
     */
    public LocalVedioPager(Context context) {
        super(context);
    }

    /**
     * 继承父类强制执行的方法，初始化该页面的控件
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager,null);
        lv_vedio = view.findViewById(R.id.lv_vedio);
        tv_novedio = view.findViewById(R.id.tv_novedio);
        pb_loading = view.findViewById(R.id.pb_loading);

        lv_vedio.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    /**
     * 重写父类初始化数据的方法
     */
    @Override
    public void initData() {
        super.initData();
        getVedioFromLocal();
    }

    //用来接收遍历完了后的handler发送过来的信息（只要有信息都会触发）
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //判断数组中是否有东西
            if(mediaItems != null & mediaItems.size()>0){
                //有数据，将数据装入适配器
                myVedioPagerAdapter = new MyVedioPagerAdapter(context, mediaItems);
                lv_vedio.setAdapter(myVedioPagerAdapter);
                //数据加载了将novedio文本隐藏
                tv_novedio.setVisibility(View.GONE);
            }
            else{
                tv_novedio.setVisibility(View.VISIBLE);
            }
            //最后都要隐藏进度控件
            pb_loading.setVisibility(View.GONE);
        }
    };

    /**
     * 通过centextprivider获取本地的视频文件信息
     * 耗时操作，需要在子线程中进行
     */
    private void getVedioFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                //通过context获取到内容解析者
                ContentResolver resolver = context.getContentResolver();
                //获取到外部存储的uri
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                //获取到要得到的信息的字段
                String[] objc = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.ARTIST,
                };
                //获取查询到的游标
                Cursor cursor = resolver.query(uri,objc,null,null,null);
                //判断游标是否为空
                if(cursor != null){
                    while (cursor.moveToNext()){
                        //初始化一个可以存储数据的mediaitem
                        MediaItem mediaItem = new MediaItem();
                        //将存储每一个文件的信息的mediaitem放入数组中
                        mediaItems.add(mediaItem);
                        //将每一个文件的信息存入mediaitem中
                        mediaItem.setName(cursor.getString(0));
                        mediaItem.setTime(cursor.getLong(1));
                        mediaItem.setSize(cursor.getLong(2));
                        mediaItem.setDataPath(cursor.getString(3));
                        mediaItem.setArtist(cursor.getString(4));
                    }
                    //遍历完了关闭cursor
                    cursor.close();
                }
                //想handler发送消息，我遍历完了
                handler.sendEmptyMessage(10);

            }
        }.start();
    }

    /**
     * listview每个子项的点击事件
     */
    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //在数组中获取当前的视频信息
            MediaItem mediaItem = mediaItems.get(position);
            /**
             * 方法一：调用系统中所有的播放器，供用户选择播放-----隐式意图
            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(mediaItem.getDataPath()),"video/*");
            context.startActivity(intent);
             */
            /**
             * 方法二：自己创建视频播放器播放视频-------显示意图
             * 自己新建一个视频播放器的class---MyVideoPlayer
             Intent intent = new Intent(context, MyVideoPalyer.class);
             intent.setDataAndType(Uri.parse("https://135zyv6.xw0371.com/2018/12/26/cDmSCUoLDBKd8BAM/playlist.m3u8"),"video/*");
             context.startActivity(intent);
             */

            /**
             * 方法三：传递播放列表，需要对列表进行序列化才能在进程中传递,先让mediaitem继承接口serializable
             */
            Intent intent = new Intent(context, MyVideoPalyer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }


}
