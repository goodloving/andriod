package project.hl.hlplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import project.hl.hlplayer.MyApplication;
import project.hl.hlplayer.R;
import project.hl.hlplayer.activity.MyVideoPalyer;
import project.hl.hlplayer.adapter.MyNetVideoPagerAdapter;
import project.hl.hlplayer.base.BasePage;
import project.hl.hlplayer.domain.MediaItem;
import project.hl.hlplayer.utils.CacheUtils;
import project.hl.hlplayer.utils.Constants;
import project.hl.hlplayer.utils.Utils;
import project.hl.hlplayer.view.XListView;

public class NetVedioPager extends BasePage {
    //该页面的控件
    private View view;

    @ViewInject(R.id.tv_no_not_forevedio)
    private TextView netNoVideo;
    @ViewInject(R.id.lv_net_foreshow_vedio)
    private XListView netForeList;
    @ViewInject(R.id.pb_fore_loading)
    private ProgressBar netForePb;

    private String cache = "";

    //初始化预告视频列表
    ArrayList<MediaItem> mediaItems;
    private MyNetVideoPagerAdapter adapter;
    private boolean isLoadMore = false;
    /**
     * 重写构造函数
     * @param context
     */
    public NetVedioPager(Context context) {
        super(context);
    }

    /**
     * 继承父类强制执行的方法，初始化该页面的控件
     * @return
     */
    @Override
    public View initView() {
       view = View.inflate(context, R.layout.net_video_pager,null);
        x.view().inject(NetVedioPager.this,view);
        //设置点击事件监听
        netForeList.setOnItemClickListener(new MyOnItemClickListener());
        netForeList.setPullLoadEnable(true);
        netForeList.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                getDataFromNet();
            }

            @Override
            public void onLoadMore() {
                getMoreFromNet();
            }
        });
        return view;
    }

    /**
     * 加载或更多资源
     */
    private void getMoreFromNet() {
        //注意：一般一个页面一个url，所以加载更多的时候要更换url
        RequestParams params = new RequestParams(Constants.NET_VIDEO_FORESHOW_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 联网成功
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                isLoadMore = true;
                showForeVideo(result);
            }
            /**
             * 联网出错
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isLoadMore = false;
            }
            /**
             * 返回信息
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {
                isLoadMore = false;
            }
            /**
             * 联网完成
             */
            @Override
            public void onFinished() {
                isLoadMore = false;
            }
        });
    }

    /**
     * 立即加载下拉刷新后
     */
    private void onLoad() {
        netForeList.stopRefresh();
        netForeList.stopLoadMore();
        Utils utils = new Utils();
        netForeList.setRefreshTime("更新时间:"+utils.getSystemTime());
    }
    /**
     * 重写父类初始化数据的方法
     */
    @Override
    public void initData() {
        super.initData();
        //读取缓存
        cache = CacheUtils.getCache(context, Constants.NET_VIDEO_FORESHOW_URL);
        if(!TextUtils.isEmpty(cache)){
            showForeVideo(cache);
        }
        getDataFromNet();

    }

    /**
     * 从网络获取资源
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_FORESHOW_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 联网成功
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                //进行缓存
                CacheUtils.setCache(context,Constants.NET_VIDEO_FORESHOW_URL,result);
                showForeVideo(result);
            }
            /**
             * 联网出错
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(TextUtils.isEmpty(cache)){
                    netNoVideo.setVisibility(View.VISIBLE);
                    netForePb.setVisibility(View.GONE);
                }
                Toast toast = Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            /**
             * 返回信息
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {

            }

            /**
             * 联网完成
             */
            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 显示预告视频列表
     * @param json
     */
    private void showForeVideo(String json) {
        if(!isLoadMore){
            mediaItems = parseJson(json);
            //判断是否存在数据
            if(mediaItems != null && mediaItems.size() >0){
                adapter = new MyNetVideoPagerAdapter(context,mediaItems);
                netForeList.setAdapter(adapter);
                onLoad();
                netNoVideo.setVisibility(View.GONE);
            }else {
                netNoVideo.setVisibility(View.VISIBLE);
            }
            //隐藏进度条
            netForePb.setVisibility(View.GONE);
        }else{
            //加载更多----数据添加到原来集合中
            ArrayList<MediaItem> moreMediaItems = parseJson(json);
            isLoadMore = false;
            mediaItems.addAll(moreMediaItems);
            //刷新适配器
            adapter.notifyDataSetChanged();
            onLoad();
        }


    }

    /**
     * 解析json数据
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        //开始解析数据----系统方法
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray trailers = jsonObject.optJSONArray("trailers");
            if(trailers != null && trailers.length()>0){
                for (int i=0;i<trailers.length();i++) {
                    JSONObject jsonObjectItem = (JSONObject) trailers.get(i);
                    if(jsonObjectItem != null){
                        MediaItem mediaItem = new MediaItem();
                        String videoName = jsonObjectItem.optString("movieName");
                        mediaItem.setName(videoName);
                        String videoTitle = jsonObjectItem.optString("videoTitle");
                        mediaItem.setDesc(videoTitle);
                        String imageUrl = jsonObjectItem.optString("coverImg");
                        mediaItem.setImageUri(imageUrl);
                        String videoUrl = jsonObjectItem.optString("hightUrl");
                        mediaItem.setDataPath(videoUrl);
                        mediaItems.add(mediaItem);
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

    /**
     * 点击进入播放页面播放
     */
    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, MyVideoPalyer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);
        }
    }
}
