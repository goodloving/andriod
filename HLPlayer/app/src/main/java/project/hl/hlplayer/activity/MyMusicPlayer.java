package project.hl.hlplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import okhttp3.internal.Util;
import project.hl.hlplayer.IMusicPlayerService;
import project.hl.hlplayer.R;
import project.hl.hlplayer.domain.MediaItem;
import project.hl.hlplayer.service.MusicPlayerService;
import project.hl.hlplayer.utils.Utils;
import project.hl.hlplayer.view.RoundImageView;

public class MyMusicPlayer extends AppCompatActivity implements View.OnClickListener {
    //进度跟新
    private static final int PROGRESS = 1;
    private TextView tvMusicPlayerName;
    private TextView tvMusicPlayerArtist;
    private RoundImageView rivMusicPlayerIcon;
    private TextView tvMusicPrelyc;
    private TextView tvMusicCurrlyc;
    private TextView tvMusicNextlyc;
    private TextView tvMusicCurrenttime;
    private SeekBar seekbarMusic;
    private TextView tvMusicTotaltime;
    private Button btnMusicModel;
    private Button btnMusicPre;
    private Button btnMusicPause;
    private Button btnMusicNext;
    private Button btnMusicList;
    private int position;
    private IMusicPlayerService service;

    private Utils utils;
    //实现连接
    private ServiceConnection connection = new ServiceConnection() {
        /**
         * 当连接成功的时候回调该方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if(service!=null){
                try {
                    if(!notification){
                        service.openMusic(position);
                    } else{
                        showPlayerView();
                        checkPlayModel();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * 当断开连接的时候回调该方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if(service!=null){
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    private MyReceiver receiver;
    private boolean notification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        utils = new Utils();
        //初始化数据
        setData();
        //初始化控件
        findViews();
        //获得数据
        getData();
        //绑定和开启服务
        bindAndStartService();
        
    }

    /**
     * 初始化数据
     */
    private void setData() {
        //接受广播后要执行的动作
        receiver = new MyReceiver();
        //添加动作
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENMUSIC);
        //注册广播
        registerReceiver(receiver,intentFilter);

    }

    /**
     * 绑定和开启服务
     */
    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("hlplayer_OPENMUSIC");
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        //启动一次就不再启动了------单例
        startService(intent);
    }

    /**
     * 获得传递过来的数据
     */
    private void getData() {
        notification = getIntent().getBooleanExtra("Notification",false);
        if(!notification){
            position = getIntent().getIntExtra("position",0);
        }
    }

    /**
     * 控件初始化
     */
    private void findViews() {
        tvMusicPlayerName = (TextView)findViewById( R.id.tv_music_player_name );
        tvMusicPlayerArtist = (TextView)findViewById( R.id.tv_music_player_artist );
        rivMusicPlayerIcon = (RoundImageView)findViewById( R.id.riv_music_player_icon );
        tvMusicPrelyc = (TextView)findViewById( R.id.tv_music_prelyc );
        tvMusicCurrlyc = (TextView)findViewById( R.id.tv_music_currlyc );
        tvMusicNextlyc = (TextView)findViewById( R.id.tv_music_nextlyc );
        tvMusicCurrenttime = (TextView)findViewById( R.id.tv_music_currenttime );
        seekbarMusic = (SeekBar)findViewById( R.id.seekbar_music );
        tvMusicTotaltime = (TextView)findViewById( R.id.tv_music_totaltime );
        btnMusicModel = (Button)findViewById( R.id.btn_music_model );
        btnMusicPre = (Button)findViewById( R.id.btn_music_pre );
        btnMusicPause = (Button)findViewById( R.id.btn_music_pause );
        btnMusicNext = (Button)findViewById( R.id.btn_music_next );
        btnMusicList = (Button)findViewById( R.id.btn_music_list );

        btnMusicModel.setOnClickListener( this );
        btnMusicPre.setOnClickListener( this );
        btnMusicPause.setOnClickListener( this );
        btnMusicNext.setOnClickListener( this );
        btnMusicList.setOnClickListener( this );

        //设置音频的拖动
        seekbarMusic.setOnSeekBarChangeListener(new myOnSeekBarChangeListener());
    }


    /**
     * 控件监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        if ( v == btnMusicModel ) {
            setPlayModel();
        } else if ( v == btnMusicPre ) {
            if(service!=null){
                try {
                    service.preMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnMusicPause ) {
            if(service!=null){
                try {
                    if(service.isPlaying()){
                        //暂停操作，按钮设置为播放
                        service.pause();
                        btnMusicPause.setBackgroundResource(R.drawable.video_start_pressed);
                    }else{
                        //播放操作，按钮设置为暂停
                        service.start();
                        btnMusicPause.setBackgroundResource(R.drawable.video_paused_presse);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnMusicNext ) {
            if(service!=null){
                try {
                    service.nextMusic();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // Handle clicks for btnMusicNext
        } else if ( v == btnMusicList ) {
            // Handle clicks for btnMusicList
        }
    }

    /**
     * 设置播放模式
     */
    private void setPlayModel() {
        try {
            int playModel = service.getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                playModel = MusicPlayerService.ALL_PLAY;
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                playModel = MusicPlayerService.SINGLE_PLAY;
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                playModel = MusicPlayerService.RANDOM_PLAY;
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                playModel = MusicPlayerService.ORDER_PLAY;
            } else {
                playModel = MusicPlayerService.ORDER_PLAY;
            }
            service.setMusicModel(playModel);
            //设置图片
            showPlayModel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示播放模式的图片
     */
    private void showPlayModel() {
        try {
            int playModel = service.getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_normal_order);
                Toast.makeText(this,"顺序播放",Toast.LENGTH_SHORT).show();
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_play_all_repeat);
                Toast.makeText(this,"循环播放",Toast.LENGTH_SHORT).show();
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_playmode_singlerepeat);
                Toast.makeText(this,"单曲循环",Toast.LENGTH_SHORT).show();
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.random_play);
                Toast.makeText(this,"随机播放",Toast.LENGTH_SHORT).show();
            } else {
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_normal_order);
                Toast.makeText(this,"顺序播放",Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 检查播放模式的图片
     */
    private void checkPlayModel() {
        try {
            int playModel = service.getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_normal_order);
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_play_all_repeat);
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_playmode_singlerepeat);
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                btnMusicModel.setBackgroundResource(R.drawable.random_play);
            } else {
                btnMusicModel.setBackgroundResource(R.drawable.btn_now_playing_normal_order);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播接收者的内部类-----接手后执行的动作
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showPlayerView();
            checkPlayModel();
        }
    }

    /**
     * 显示播放界面的情况
     */
    private void showPlayerView() {
        try {
            tvMusicPlayerName.setText(service.getMusicName());
            tvMusicPlayerArtist.setText(service.getMusicArtist());
            seekbarMusic.setMax(service.getDuration());
            tvMusicTotaltime.setText(utils.stringForTime(service.getDuration()));
            handler.sendEmptyMessage(PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    try {
                        //得到当前进度
                        int currentPosition = service.getCurrentPosition();
                        seekbarMusic.setProgress(currentPosition);
                        tvMusicCurrenttime.setText(utils.stringForTime(currentPosition));
                        //一秒钟更新一次
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //取消广播注册
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver =null;
        }
        //解绑服务
        if(connection!=null){
            unbindService(connection);
            connection = null;
        }
        super.onDestroy();
    }

    /**
     * 设置音乐拖动的监听
     */
    private class myOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
