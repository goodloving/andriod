package project.hl.hlplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.domain.MediaItem;
import project.hl.hlplayer.utils.Utils;

public class MyVideoPalyer extends Activity implements View.OnClickListener{
    //视频进度消息
    private static final int PROGRESS = 1;
    private static final int HIDE_CONTROLLER = 2;
    //设置滑动距离
    private static final float FLIP_DISTANCE = 50;
    private static final int VOICE_CONTRALLER = 3;
    private static final int LIGHT_CONTRALLER = 4;
    private static final int SHOE_NETSPEED = 5;
    private Integer totalTime;

    //定义手势识别器
    private GestureDetector detector;

    private project.hl.hlplayer.view.VideoView videoView;
    private Uri uri;
    //控制器按钮
    private Button btnVideoExit;
    private TextView tvVideoName;
    private Button btnVideoFullscreen;
    private TextView tvVideoCurrenttime;
    private SeekBar seekbarVoice;
    private TextView tvVideoTotaltime;
    private Button btnVideoPre;
    private Button btnVideoPause;
    private Button btnVideoNext;

    private LinearLayout voice_layout;
    private TextView tv_voice;

    private LinearLayout light_layout;
    private TextView tv_light;

    //控制面板的操作
    private RelativeLayout media_controller;
    private boolean hideOrShow = false;
    private boolean isFullscreen = false;
    /**
     * 视频原始宽高
     */
    private int videoWidth = 0;
    private int videoHeight = 0;
    /**
     * 手机的宽高
     */
    private int phoneWidth = 0;
    private int phoneHeight = 0;

    private Utils utils;
    private MyReceiver receiver;
    private ArrayList<MediaItem> mediaItems;
    private int position;
    /**
     * 音频管理器,-------当前音量-----和------最大音量
     */
    private AudioManager am;
    private int currentVoice;
    private int maxVoice;
    private boolean voice_h_s = false;
    private float startY;     //手指按下时手指Y坐标
    private int mVol;
    private int touchRang;
    private float endY;

    /**
     * 亮度理器,-------当前亮度-----和------最大亮度
     */
    private int currentLight = 0;
    private int maxLight;
    private boolean light_h_s = false;
    private float startX;     //手指按下时手指X坐标
    private int mLight;
    private boolean isNetUri;

    /**
     * 视频卡顿缓冲
     */
    private LinearLayout buffer_video;
    private TextView tv_video_buffer;
    private boolean isUseSystem = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);

        initData();
        initView();
        setListener();
        getData();
    }

    /**
     * 获取传递过来的数据和设置数据
     */
    private void getData() {
        uri = getIntent().getData();//文件夹，图片浏览器，QQ空间
        //获取播放列表
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videoList");
        //获取当前视频的位置
        position = getIntent().getIntExtra("position",0);
        //根据数据设置uri
        if(mediaItems != null && mediaItems.size() > 0){
            uri = Uri.parse(mediaItems.get(position).getDataPath());
            isNetUri = utils.isNetUri(mediaItems.get(position).getDataPath());
            //修改视频名称
            tvVideoName.setText(mediaItems.get(position).getName());
            videoView.setVideoURI(uri);
        }else if (uri != null) {
            isNetUri = utils.isNetUri(uri.toString());
            videoView.setVideoURI(uri);
            //修改视频名称
            tvVideoName.setText(uri.toString());
        }else{
            Toast toast = Toast.makeText(this, "没有数据！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化工具类
        utils = new Utils();
        //初始化接受广播的参数
        receiver = new MyReceiver();                                          //注册电量广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);                   //当电量过低时发送这条广播
        registerReceiver(receiver,intentFilter);                                //动态注册广播
        //实现手势识别器的方法
        detector = new GestureDetector(MyVideoPalyer.this,new GestureDetector.SimpleOnGestureListener(){
            //单击------控制面板显示和隐藏
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(hideOrShow){
                    hideController();
                    handler.removeMessages(HIDE_CONTROLLER);
                }else {
                    showController();
                    handler.sendEmptyMessageDelayed(HIDE_CONTROLLER,4000);
                }
                return super.onSingleTapConfirmed(e);
            }
            //双击------全屏和退出全屏
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullscreenOrDefualt();
                return super.onDoubleTap(e);
            }
            //长按-------视频暂停和播放
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                setStartAndPause();

            }
        });
    }

    /**
     * 监听方法的集合方法
     */
    public void setListener(){
        //设置准备好了的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //设置播放报错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());
        //设置播放完了的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());
        //系统底层自带的控制按钮
        //videoView.setMediaController(new MediaController(this));
        //设置seekbar的进度监听事件
        seekbarVoice.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //监听卡
        if(isUseSystem ){                //使用系统videoview自带的监听卡
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }

    /**
     * 初始化界面
     */
    public void initView() {
        videoView = findViewById(R.id.sys_videoview);
        btnVideoExit = (Button)findViewById( R.id.btn_video_exit );
        tvVideoName = (TextView)findViewById( R.id.tv_video_name );
        btnVideoFullscreen = (Button)findViewById( R.id.btn_video_fullscreen );
        tvVideoCurrenttime = (TextView)findViewById( R.id.tv_video_currenttime );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        tvVideoTotaltime = (TextView)findViewById( R.id.tv_video_totaltime );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoPause = (Button)findViewById( R.id.btn_video_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        media_controller = findViewById(R.id.media_controller);
        voice_layout = findViewById(R.id.linear_layout_voice);
        tv_voice = findViewById(R.id.tv_voice);
        light_layout = findViewById(R.id.linear_layout_right);
        tv_light = findViewById(R.id.tv_light);

        btnVideoExit.setOnClickListener( this );
        btnVideoFullscreen.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );

        buffer_video = findViewById(R.id.buffer_video_linearlayout);
        tv_video_buffer = findViewById(R.id.tv_video_buffer);

        //默认控制面板为隐藏
        hideController();
        //默认音量控制面板为隐藏
        hideVoice();
        //默认亮度控制面板为隐藏
        hideLight();
        //获取手机宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        phoneWidth = dm.widthPixels;
        phoneHeight = dm.heightPixels;

        //获取系统的音量调节服务
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        //当前音量
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //系统最大音量
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        tv_voice.setText((int) (currentVoice*1.0/maxVoice*100)+"%");

        //获取系统的亮度
        try {
            currentLight = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
            maxLight =  225;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        //更新网速
        handler.sendEmptyMessage(SHOE_NETSPEED);
    }

    @Override
    public void onClick(View v) {
        if ( v == btnVideoExit ) {
            //退出当前播放界面
            this.finish();
        } else if ( v == btnVideoFullscreen ) {
            //设置播放是全屏和非全屏
            setFullscreenOrDefualt();
        } else if ( v == btnVideoPre ) {
            if(mediaItems!=null){
                if(position == 0){
                    Toast toast = Toast.makeText(this, "当前为第一个视频", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    position -= 1;
                    //修改视频名称
                    tvVideoName.setText(mediaItems.get(position).getName());
                    uri = Uri.parse(mediaItems.get(position).getDataPath());
                    isNetUri = utils.isNetUri(mediaItems.get(position).getDataPath());
                    videoView.setVideoURI(uri);
                }
            }else{
                Toast toast = Toast.makeText(this, "当前为第一个视频", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        } else if ( v == btnVideoPause ) {
            setStartAndPause();
        } else if ( v == btnVideoNext ) {
            if(mediaItems!=null){
                if(position == mediaItems.size()-1){
                    Toast toast = Toast.makeText(this, "当前为最后一个视频", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    position += 1;
                    //修改视频名称
                    tvVideoName.setText(mediaItems.get(position).getName());
                    uri = Uri.parse(mediaItems.get(position).getDataPath());
                    isNetUri = utils.isNetUri(mediaItems.get(position).getDataPath());
                    videoView.setVideoURI(uri);
                }
            }else{
                Toast toast = Toast.makeText(this, "当前为最后一个视频", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }

        }
        //点击按钮后4秒隐藏控制面板
        handler.removeMessages(HIDE_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_CONTROLLER,4000);
    }

    /**
     * 控制视频播放和暂停
     */
    private void setStartAndPause() {
        if(videoView.isPlaying()){
            //视频在播放----设置为暂停状态----按钮图片更换
            videoView.pause();
            btnVideoPause.setBackgroundResource(R.drawable.video_start_pressed);
        }else{
            //视频在暂停----设置为播放状态----按钮图片更换
            videoView.start();
            btnVideoPause.setBackgroundResource(R.drawable.video_paused_presse);
        }
    }

    /**
     * 控制视频全屏或者默认
     */
    private void setFullscreenOrDefualt(){
        if(isFullscreen){
            //屏幕的宽和高
            int width = phoneWidth;
            int height = phoneHeight;
            //当前状态----全屏------>改为默认
            if ( videoHeight * width  > videoWidth * height ) {
                width = phoneHeight * videoWidth / videoHeight;
            } else if (videoHeight * width  < videoWidth * height ) {
                //Log.i("@@@", "image too tall, correcting");
                height = phoneWidth * videoHeight / videoWidth;
            }
            videoView.setVideoSize(width, height);
            btnVideoFullscreen.setBackgroundResource(R.drawable.btn_full_screen_normal);
            isFullscreen = false;
        }else {
            //当前状态----默认------>改为全屏
            videoView.setVideoSize(phoneWidth,phoneHeight);
            btnVideoFullscreen.setBackgroundResource(R.drawable.btn_default_screen_normal);
            isFullscreen = true;
        }
    }

    private Integer precurrentPosition = -1000;
    /**
     * handler发送和接受消息
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //判断消息来源进行相应操作
            switch (msg.what){
                case SHOE_NETSPEED:                      //显示网速
                    //得到网速
                    String netSpeed = utils.getNetSpeed(MyVideoPalyer.this);
                    tv_video_buffer.setText("缓冲中··· " + netSpeed);
                    handler.removeMessages(SHOE_NETSPEED);
                    handler.sendEmptyMessageDelayed(SHOE_NETSPEED,2000);
                    break;
                case PROGRESS:                          //进度条
                    //1.得到当前的事件
                    Integer currentPosition = videoView.getCurrentPosition();
                    //2.将当前事件与seekbar绑定
                    seekbarVoice.setProgress(currentPosition);
                    //3.当前时间在tv上显示
                    tvVideoCurrenttime.setText(utils.stringForTime(currentPosition));

                    //缓存进度的更新
                    if (isNetUri) {
                        //只有网络资源才有缓存效果
                        int buffer = videoView.getBufferPercentage();//0~100
                        int totalBuffer = buffer * seekbarVoice.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVoice.setSecondaryProgress(secondaryProgress);
                    } else {
                        //本地视频没有缓冲效果
                        seekbarVoice.setSecondaryProgress(0);
                    }
                    //自定义监听卡
                    if (!isUseSystem && videoView.isPlaying()){
                        if(videoView.isPlaying()){
                            int buffer = currentPosition - precurrentPosition;
                            if(buffer < 500){               //卡拉
                                buffer_video.setVisibility(View.VISIBLE);
                            }else{
                                buffer_video.setVisibility(View.GONE);
                            }
                        }
                    }
                    precurrentPosition = currentPosition;

                    //4.消息回调,每秒钟更新一次
                    handler.removeMessages(PROGRESS);           //一般先将消息移除
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);    //自己给自己发消息，自动今行时间上的更新（延迟一秒钟）-----循环
                    break;
                case HIDE_CONTROLLER:                   //控制面板
                    hideController();
                    break;
                case VOICE_CONTRALLER:                  //声音控制
                    hideVoice();
                    break;
                case LIGHT_CONTRALLER:                  //声音控制
                    hideLight();
                    break;
            }
        }
    };

    /**
     *视频准备好的监听内部类
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //视频准备好了-----绑定seekbar的总时长
            totalTime = videoView.getDuration();
            seekbarVoice.setMax(totalTime);
            //设置总时长，显示在tv
            tvVideoTotaltime.setText(utils.stringForTime(totalTime));
            //发送消息到handler执行相应操作
            handler.sendEmptyMessage(PROGRESS);
            //播放视频
            videoView.start();

            //获取视频宽高
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

        }
    }

    /**
     * 视频播放报错的监听内部类
     */
    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //Toast toast = Toast.makeText(MyVideoPalyer.this, "播放出错了！", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER,0,0);
            //toast.show();
            startVitamioPlayer();
            return false;
        }
    }

    /**
     * 跳转到万能哦放弃
     */
    private void startVitamioPlayer() {
        if(videoView != null){
            videoView.stopPlayback();
        }
        Intent intent = new Intent(this, VitamioVideoPalyer.class);
        if(mediaItems!=null && mediaItems.size()>0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
        }else if(uri != null){
            intent.setData(uri);
        }
        this.startActivity(intent);
        finish();
    }

    /**
     * 视频播放完了的监听内部类
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(mediaItems!=null){
                if(position != mediaItems.size()-1){
                    Toast toast = Toast.makeText(MyVideoPalyer.this, "播放播放完了，准备播放下一个！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    //设置延迟3秒钟
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            position += 1;
                            uri = Uri.parse(mediaItems.get(position).getDataPath());
                            videoView.setVideoURI(uri);
                        }
                    },3000);
                }
            }else{
                Toast toast = Toast.makeText(MyVideoPalyer.this, "播放播放完了！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

    /**
     * seekbar人为改变的监听事件
     */
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 进度改变的监听方法
         * @param seekBar
         * @param progress
         * @param fromUser  自动更新时也会回调该方法，如果是自动该改变----false，如果是用过户引起的改变----true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                videoView.seekTo(progress);
            }
        }
        //触摸进度条的监听方法
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //移除消息
            handler.removeMessages(HIDE_CONTROLLER);
        }
        //放开进度条的监听方法
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //发送消息
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER,4000);
        }
    }

    /**
     * activity销毁，释放资源
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);   //注销此广播
        super.onDestroy();   //  先释放子类资源（写在该行前面），再释放父类资源------因为子类可能调用了父类的资源
    }

    /**
     * 获取手机电量的广播
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);
            Toast toast = Toast.makeText(context, "当前电量为," + String.valueOf(level) + "%，请注意充电！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    /**
     * 屏幕触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将屏幕触摸的事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:           //手指按下
                startY = event.getY();
                startX = event.getX();
                touchRang = Math.min(phoneHeight,phoneWidth);
                //判断手指在那一边
                if(startX < Math.max(phoneHeight,phoneWidth)/2){     //左边
                    mLight = currentLight;
                    handler.removeMessages(LIGHT_CONTRALLER);
                }
                else {                                                  //右边
                    mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    handler.removeMessages(VOICE_CONTRALLER);
                }
                break;
            case MotionEvent.ACTION_MOVE:           //手指移动
                endY = event.getY();
                Float distanceY = startY - endY;

                if(startX < Math.max(phoneHeight,phoneWidth)/2){     //左边
                    Float delta = (distanceY/touchRang)*maxLight;
                    int light = (int) Math.min(maxLight,Math.max((int)(mLight+delta),0));
                    if(delta != 0){
                        updataLight(light);
                    }
                }
                else {                                                  //右边
                    Float delta = (distanceY/touchRang)*maxVoice;
                    int voice = (int) Math.min(maxVoice,Math.max((int)(mVol+delta),0));
                    if(delta != 0){
                        updataVoice(voice,false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:             //手指离开
                //判断手指在那一边
                if(startX < Math.max(phoneHeight,phoneWidth)/2){     //左边
                    handler.sendEmptyMessageDelayed(LIGHT_CONTRALLER,2000);
                }
                else {                                                  //右边
                    handler.sendEmptyMessageDelayed(VOICE_CONTRALLER,2000);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 声音更新
     * @param voice
     * @param isMute
     */
    private void updataVoice(int voice, boolean isMute) {
        showVoice();
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, voice, 0);
            int voice_maxvoice = (int) (voice*1.0/maxVoice*100);
            tv_voice.setText(voice_maxvoice+"%");
            currentVoice = voice;
        }
    }

    /**
     * 亮度更新
     * @param brightness
     */
    public void updataLight(int brightness) {
        showLight();
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = (float) (brightness*1.0/maxLight);
        tv_light.setText((int)((brightness*1.0/ maxLight)*100)+"%");
        window.setAttributes(lp);
        currentLight = brightness;
    }

    /**
     * 隐藏控制面板
     */
    public void hideController(){
        media_controller.setVisibility(View.GONE);
        hideOrShow = false;
    }
    /**
     * 显示控制面板
     */
    public void showController(){
        media_controller.setVisibility(View.VISIBLE);
        hideOrShow = true;
    }

    /**
     * 隐藏音量
     */
    public void hideVoice(){
        voice_layout.setVisibility(View.GONE);
        voice_h_s = false;
    }
    /**
     * 显示音量
     */
    public void showVoice(){
        voice_layout.setVisibility(View.VISIBLE);
        voice_h_s = true;
    }
    /**
     * 隐藏亮度
     */
    public void hideLight(){
        light_layout.setVisibility(View.GONE);
        light_h_s = false;
    }
    /**
     * 显示亮度
     */
    public void showLight(){
        light_layout.setVisibility(View.VISIBLE);
        light_h_s = true;
    }


    /**
     * 通过手机按键控制声音
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){                        //如果按下减音键
            currentVoice --;
            if (currentVoice<0) currentVoice = 0;
            updataVoice(currentVoice,false);
            handler.removeMessages(VOICE_CONTRALLER);
            handler.sendEmptyMessageDelayed(VOICE_CONTRALLER,2000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice ++;
            if (currentVoice>maxVoice) currentVoice = maxVoice;
            updataVoice(currentVoice,false);
            handler.removeMessages(VOICE_CONTRALLER);
            handler.sendEmptyMessageDelayed(VOICE_CONTRALLER,2000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 视频卡了，缓冲的舰艇内部类
     */
    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    buffer_video.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    buffer_video.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
}
