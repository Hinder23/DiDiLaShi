package com.android.didilashi.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/2/12.
 */

public class OkHttp3Util {

    //get请求
    public static void SendRequest(String url,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);

    }
    //post
}
