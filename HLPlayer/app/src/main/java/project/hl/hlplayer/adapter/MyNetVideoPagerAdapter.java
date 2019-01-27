package project.hl.hlplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.xutils.x;

import java.util.ArrayList;

import project.hl.hlplayer.R;
import project.hl.hlplayer.domain.MediaItem;

public class MyNetVideoPagerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MediaItem> mediaItems;

    public MyNetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
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
        //判断convertView是否为空，为空则要重新生成，不为空说明模板已经定义，直接数据放入即可
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item_forevideo_pager,null);
            viewHoder = new ViewHoder();
            viewHoder.video_name = convertView.findViewById(R.id.tv_foreVideo_title);
            viewHoder.video_desc = convertView.findViewById(R.id.tv_foreVideo_desc);
            viewHoder.video_icon = convertView.findViewById(R.id.iv_foreVideo);
            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(position);
        viewHoder.video_name.setText(mediaItem.getName());
        viewHoder.video_desc.setText(mediaItem.getDesc());

        //方法一：使用xUtils请求图片
        //x.image().bind(viewHoder.video_icon,mediaItem.getImageUri());
        //方法二：使用glide请求图片
        Glide.with(context)
                .load(mediaItem.getImageUri())
                .into(viewHoder.video_icon);
        //方法三：使用picasso请求图片
//        Picasso.get()
//                .load(mediaItem.getImageUri())
//                .into(viewHoder.video_icon);

        return convertView;
    }

    private class ViewHoder{
        ImageView video_icon;
        TextView video_name;
        TextView video_desc;
    }
}
