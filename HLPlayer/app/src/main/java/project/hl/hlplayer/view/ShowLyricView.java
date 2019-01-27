package project.hl.hlplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.ArrayList;

import project.hl.hlplayer.domain.Lyric;

/**
 * 自定义歌词显示的textview控件
 */
public class ShowLyricView extends AppCompatTextView {
    //歌词列表
    ArrayList<Lyric> lyrics;

    /**
     * 设置歌词
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
