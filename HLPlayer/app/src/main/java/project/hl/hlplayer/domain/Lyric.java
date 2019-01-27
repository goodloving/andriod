package project.hl.hlplayer.domain;

public class Lyric {
    //歌词类容
    private String content;
    //歌词时间戳
    private Long timeOut;
    //歌词高亮显示时间
    private Long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }
}
