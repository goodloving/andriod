package project.hl.hlplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import project.hl.hlplayer.IMusicPlayerService;
import project.hl.hlplayer.R;
import project.hl.hlplayer.activity.MyMusicPlayer;
import project.hl.hlplayer.domain.MediaItem;
import project.hl.hlplayer.utils.CacheUtils;

public class MusicPlayerService extends Service {
    public static final String OPENMUSIC = "OPENMUSIC";
    private ArrayList<MediaItem> mediaItems;
    public int position;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;

    /**
     * 播放模式---顺序，全部，单曲，随机
     */
    public static final int ORDER_PLAY = 1;
    public static final int ALL_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    public static final int RANDOM_PLAY = 4;

    private int playModel= ORDER_PLAY;

    @Override
    public void onCreate() {
        super.onCreate();
        playModel = CacheUtils.getCacheInt(this,"playModel");
        //加载音乐列表
        getDataFromLocal();
    }

    /**
     * 加载音乐列表
     */
    private void getDataFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                //类容解析者
                ContentResolver resolver = getContentResolver();
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
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        //内部类调用服务方法
        MusicPlayerService service = MusicPlayerService.this;
        @Override
        public void openMusic(int position) throws RemoteException {
            service.openMusic(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getMusicName() throws RemoteException {
            return service.getMusicName();
        }

        @Override
        public String getMusicArtist() throws RemoteException {
            return service.getMusicArtist();
        }

        @Override
        public String getMusicPath() throws RemoteException {
            return service.getMusicPath();
        }

        @Override
        public void nextMusic() throws RemoteException {
            service.nextMusic();
        }

        @Override
        public void preMusic() throws RemoteException {
            service.preMusic();
        }

        @Override
        public void setMusicModel(int playModel) throws RemoteException {
            service.setMusicModel(playModel);
        }

        @Override
        public int getMusicModel() throws RemoteException {
            return service.getMusicModel();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

    };

    /**
     * 根据位置打开音乐
     * @param position
     */
    private void openMusic(int position){
        this.position = position;
        if(mediaItems!=null && mediaItems.size()>0){
            mediaItem = mediaItems.get(position);
            if(mediaPlayer!=null){
                mediaPlayer.reset();
            }
            try {
                mediaPlayer = new MediaPlayer();
                //设置监听
                mediaPlayer.setOnPreparedListener(new myOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new myOnCompletionListener());
                mediaPlayer.setOnErrorListener(new myOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getDataPath());

                mediaPlayer.prepareAsync();

                if(playModel == MusicPlayerService.SINGLE_PLAY){
                    //循环播放------不会触发播放完成的监听
                    mediaPlayer.setLooping(true);
                }else {
                    mediaPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this,"没有音乐！",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 开始播放
     */
    private void start(){
        mediaPlayer.start();

        //开始包房音乐时，在任务栏显示音乐内容
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //主要
        Intent intent = new Intent(this, MyMusicPlayer.class);
        //表示来自状态栏
        intent.putExtra("Notification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.music_icon)
                .setContentTitle("手机影音音乐")
                .setContentText("正在播放:"+getMusicName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);
    }
    /**
     * 暂停播放
     */
    private void pause(){
        mediaPlayer.pause();
        manager.cancel(1);
    }
    /**
     * 停止播放
     */
    private void stop(){

    }
    /**
     * 获得当前播放进度
     * @return
     */
    private int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    /**
     * 获得当前音频总时长
     * @return
     */
    private int getDuration(){
        return mediaPlayer.getDuration();
    }
    /**
     * 获得当前音频名字
     * @return
     */
    private String getMusicName(){
        return mediaItem.getName();
    }
    /**
     * 获得当前音频艺术家
     * @return
     */
    private String getMusicArtist(){
        return mediaItem.getArtist();
    }
    /**
     * 获得当前音频路径
     * @return
     */
    private String getMusicPath(){
        return null;
    }
    /**
     * 下一首
     */
    private void nextMusic(){
        setNextPosition();
        openNextMusic();
    }

    /**
     * 设置下一首的位置
     */
    private void setNextPosition() {
        try {
            int playModel = getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                position++;
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                position++;
                if(position>=mediaItems.size()){
                    position=0;
                }
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                position++;
                if(position>=mediaItems.size()){
                    position=0;
                }
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                Random random = new Random();
                position = random.nextInt(mediaItems.size());
            } else {
                position++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置下一首播放
     */
    private void openNextMusic() {
        try {
            int playModel = getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                if(position<mediaItems.size()){
                    openMusic(position);
                }else {
                    position = mediaItems.size()-1;
                }
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                openMusic(position);
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                openMusic(position);
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                openMusic(position);
            } else {
                if(position<mediaItems.size()){
                    openMusic(position);
                }else {
                    position = mediaItems.size()-1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上一首
     */
    private void preMusic(){
        setPrePosition();
        openPreMusic();
    }
    /**
     * 设置上一首的位置
     */
    private void setPrePosition() {
        try {
            int playModel = getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                position--;
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                position--;
                if(position<0){
                    position = mediaItems.size()-1;
                }
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                position--;
                if(position<0){
                    position = mediaItems.size()-1;
                }
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                Random random = new Random();
                position = random.nextInt(mediaItems.size());
            } else {
                position--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置上一首播放
     */
    private void openPreMusic() {
        try {
            int playModel = getMusicModel();
            if(playModel == MusicPlayerService.ORDER_PLAY){
                if(position>=0){
                    openMusic(position);
                }else {
                    position = 0;
                }
            } else if(playModel == MusicPlayerService.ALL_PLAY){
                openMusic(position);
            } else if(playModel == MusicPlayerService.SINGLE_PLAY){
                openMusic(position);
            } else if(playModel == MusicPlayerService.RANDOM_PLAY){
                openMusic(position);
            } else {
                if(position>=0){
                    openMusic(position);
                }else {
                    position = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置播放模式----单曲，顺序，全部，随机
     */
    private void setMusicModel(int playModel){
        this.playModel =playModel;
        CacheUtils.setCacheInt(this,"playModel",playModel);

        if(playModel == MusicPlayerService.SINGLE_PLAY){
            //循环播放------不会触发播放完成的监听
            mediaPlayer.setLooping(true);
        }else {
            mediaPlayer.setLooping(false);
        }
    }
    /**
     * 获得播放模式----单曲，顺序，全部，随机
     */
    private int getMusicModel(){
        return playModel;
    }
    /**
     * 是否在播放
     * @return
     */
    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    /**
     * 音乐拖动定位
     * @param position
     */
    private void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    /**
     * 音乐播放完了的监听
     */
    private class myOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            nextMusic();
        }
    }

    /**
     * 音乐准备好了的监听
     */
    private class myOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //音乐准备好了，发送广播-----其他接收者准备接受歌曲信息
            notifyChange(OPENMUSIC);
            start();
        }
    }

    /**
     * 根据动作发广播
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 音乐错误的监听
     */
    private class myOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            nextMusic();
            return true;
        }
    }
}
