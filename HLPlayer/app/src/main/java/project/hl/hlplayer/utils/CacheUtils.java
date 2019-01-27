package project.hl.hlplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import project.hl.hlplayer.service.MusicPlayerService;

public class CacheUtils {
    /**
     * 进行缓存
     * @param context
     * @param key
     * @param values
     */
    public static void setCache(Context context,String key,String values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("foreVideoCache",context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();
    }

    /**
     * 返回缓存
     * @param context
     * @param key
     * @return
     */
    public static String getCache(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("foreVideoCache",context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    /**
     * 进行缓存
     * @param context
     * @param key
     * @param values
     */
    public static void setCacheInt(Context context,String key,int values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("playModel",context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,values).commit();
    }

    /**
     * 返回缓存
     * @param context
     * @param key
     * @return
     */
    public static int getCacheInt(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("playModel",context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.ORDER_PLAY);
    }
}
