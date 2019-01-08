package project.hl.hlplayer.domain;

import java.io.Serializable;

public class MediaItem implements Serializable {
   private String name;
   private Long time;
   private Long size;
   private String dataPath;
   private String artist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", size=" + size +
                ", dataPath='" + dataPath + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
