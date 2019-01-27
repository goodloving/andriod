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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.security.MessageDigest;
import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.activity.MyMusicPlayer;
import project.hl.hlplayer.activity.MyVideoPalyer;
import project.hl.hlplayer.adapter.MyMusicPagerAdapter;
import project.hl.hlplayer.base.BasePage;
import project.hl.hlplayer.domain.MediaItem;

public class LocalMusicPager extends BasePage {
    //该页面的控件
    private TextView tv_noMusic;
    private ListView lv_muscic;
    private ProgressBar pb_music;

    //存放音乐列表的集合
    ArrayList<MediaItem> mediaItems;
    //音乐适配器
    private MyMusicPagerAdapter myMusicPagerAdapter;

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
        View view = View.inflate(context,R.layout.music_pager,null);
        tv_noMusic = view.findViewById(R.id.tv_nomusic);
        lv_muscic = view.findViewById(R.id.lv_music);
        pb_music = view.findViewById(R.id.pb_music_loading);

        lv_muscic.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    /**
     * 重写父类初始化数据的方法
     */
    @Override
    public void initData() {
        super.initData();
        getMusicFromLocal();
    }

    /**
     * 从手机获取音乐列表，在子线程中执行
     */
    private void getMusicFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                //类容解析者
                ContentResolver resolver = context.getContentResolver();
                //得到本地音乐的地址
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                //要获取的信息
                String[] objc = {
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST,
                };
                //获取查询到的游标
                Cursor cursor = resolver.query(uri,objc,null,null,null);
                if(cursor!=null){
                    while (cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();
                        mediaItem.setName(cursor.getString(0));
                        mediaItem.setTime(cursor.getLong(1));
                        mediaItem.setSize(cursor.getLong(2));
                        mediaItem.setDataPath(cursor.getString(3));
                        mediaItem.setArtist(cursor.getString(4));
                        mediaItems.add(mediaItem);
                    }
                    //关闭游标
                    cursor.close();
                }
                //向handler发送消息执行遍历完了的下一步
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //接到消息，判断音乐列表是否为空
            if(mediaItems!=null&&mediaItems.size()>0){
                //有数据，将列表装进适配器
                myMusicPagerAdapter = new MyMusicPagerAdapter(context,mediaItems);
                lv_muscic.setAdapter(myMusicPagerAdapter);
                tv_noMusic.setVisibility(View.GONE);
            }else {
                tv_noMusic.setVisibility(View.VISIBLE);
            }
            pb_music.setVisibility(View.GONE);
        }
    };

    /**
     * 将歌曲地址传递到播放界面
     */
    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, MyMusicPlayer.class);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}
