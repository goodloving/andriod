// IMusicPlayerService.aidl
package project.hl.hlplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
    /**
     * 根据位置打开音乐
     * @param position
     */
    void openMusic(int position);
    /**
     * 开始播放
     */
    void start();
    /**
     * 暂停播放
     */
    void pause();
    /**
     * 停止播放
     */
    void stop();
    /**
     * 获得当前播放进度
     * @return
     */
    int getCurrentPosition();
    /**
     * 获得当前音频总时长
     * @return
     */
    int getDuration();
    /**
     * 获得当前音频名字
     * @return
     */
    String getMusicName();
    /**
     * 获得当前音频艺术家
     * @return
     */
    String getMusicArtist();
    /**
     * 获得当前音频路径
     * @return
     */
    String getMusicPath();
    /**
     * 下一首
     */
    void nextMusic();
    /**
     * 上一首
     */
    void preMusic();
    /**
     * 设置播放模式----单曲，顺序，全部，随机
     */
    void setMusicModel(int playModel);
    /**
     * 获得播放模式----单曲，顺序，全部，随机
     */
    int getMusicModel();
    /**
     * 是否在播放
     * @return
     */
    boolean isPlaying();
    /**
    * 拖动音乐进度
    */
    void seekTo(int position);
}
