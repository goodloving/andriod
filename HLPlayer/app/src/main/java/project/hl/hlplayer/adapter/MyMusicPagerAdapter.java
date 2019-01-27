package project.hl.hlplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.domain.MediaItem;
import project.hl.hlplayer.service.MusicPlayerService;
import project.hl.hlplayer.utils.Utils;

public class MyMusicPagerAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<MediaItem> mediaItems;
    private Utils utils;

    /**
     * 有参构造函数
     * @param context
     * @param mediaItems
     */
    public MyMusicPagerAdapter(Context context,ArrayList<MediaItem> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 将数据都写入界面，控件的复用
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder = new ViewHoder();
        if(convertView==null){
            convertView = View.inflate(context, R.layout.item_music_pager,null);
            viewHoder.iv_icon = convertView.findViewById(R.id.iv_music_icon);
            viewHoder.tv_name = convertView.findViewById(R.id.tv_music_name);
            viewHoder.tv_artist = convertView.findViewById(R.id.tv_music_artist);
            viewHoder.tv_size = convertView.findViewById(R.id.tv_music_size);
            convertView.setTag(viewHoder);
        }else {
            viewHoder = (ViewHoder) convertView.getTag();
        }
        //将信息放入viewHoder中
        MediaItem mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_artist.setText(mediaItem.getArtist());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
        return convertView;
    }

    private class ViewHoder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
        TextView tv_artist;
    }
}
