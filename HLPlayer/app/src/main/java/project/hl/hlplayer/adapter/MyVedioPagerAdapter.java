package project.hl.hlplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.domain.MediaItem;
import project.hl.hlplayer.utils.Utils;

public class MyVedioPagerAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MediaItem> mediaItems;
    private Utils utils;

    public MyVedioPagerAdapter(Context context, ArrayList<MediaItem> mediaItems){
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
     * 对vedio每一文件的视图进行初始化和展示
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_vedio_pager, null);
            //实例化viewhoder并且将控件放入viewhoder中
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = convertView.findViewById(R.id.iv_vedio_icon);
            viewHoder.tv_name = convertView.findViewById(R.id.tv_vedio_name);
            viewHoder.tv_time = convertView.findViewById(R.id.tv_vedio_time);
            viewHoder.tv_size = convertView.findViewById(R.id.tv_vedio_size);
            convertView.setTag(viewHoder);
        }else {
            viewHoder = (ViewHoder) convertView.getTag();
        }
        //将当前文件的信息填入viewHoder中可视化
        MediaItem mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        viewHoder.tv_time.setText(utils.stringForTime(mediaItem.getTime().intValue()));
        //返回实例化的convertView
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
