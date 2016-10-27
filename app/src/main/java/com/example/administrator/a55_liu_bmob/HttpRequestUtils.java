package com.example.administrator.a55_liu_bmob;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/12.
 */

public class HttpRequestUtils {
    private static final String TAG = "必胜";
    private static HttpRequestUtils httpRequestUtils;
    private Context context;
    private RequestQueue queue;
    private ImageLoader imageLoader;
    private HttpRequestUtils(Context context){
        this.context = context;
        queue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(queue,new BitmapCache());
    }

    public interface ResponsListener<T>{
        public void onSuccess(T t);
        public void onError(String error);
    }

//    class MyStringRequest extends StringRequest{
//          //请求方式，请求路径，成功返回的回调接口，失败返回的回调接口
//        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
//            super(method, url, listener, errorListener);
//        }
//
//        @Override
//        protected Map<String, String> getParams() throws AuthFailureError {
//            return super.getParams();
//        }
//    }
    //封装get请求
    public void get(String url,final HashMap<String,String> params,
                    final ResponsListener<String> listener){
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        listener.onSuccess(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }

    //封装post
    public void post(String url,final HashMap<String,String> params,
                     final ResponsListener<String> listener){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        listener.onSuccess(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }
    //封装JSON数据的get请求
    public void jsonGet(String url, JSONObject params, final ResponsListener<JSONObject> listener){
        //参数：请求方式，URL，请求参数（JSONObject），响应的回调接口，错误回调接口
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        listener.onSuccess(jsonObject);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    //封装JSON数据的post请求
    public void jsonPost(String url, JSONObject params, final ResponsListener<JSONObject> listener){
        //参数：请求方式，URL，请求参数（JSONObject），响应的回调接口，错误回调接口
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        listener.onSuccess(jsonObject);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError.getMessage());
            }
        });
        queue.add(request);
    }
    //加载网络图片（URL，最大的宽，最大的高，显示图片的组件）
    public void loadImage(String url, int maxWidth, int maxHeight, final ImageView view){
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                view.setImageBitmap(bitmap);
            }
        }, maxWidth, maxHeight, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onErrorResponse: load image error"+volleyError);
            }
        });
        queue.add(request);
    }



    public void getImage(String url,int maxWidth, int maxHeight,final ImageView view){
        //图片加载的监听器。参数：图片组件，默认图片，加载失败的图片。
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(view,
                R.mipmap.ic_launcher, android.R.drawable.ic_delete);
        imageLoader.get(url,imageListener,maxWidth,maxHeight);
    }

    /**
     * 实现缓存策略的缓存类
     */
    class BitmapCache implements ImageLoader.ImageCache{
        private LruCache<String,Bitmap> cache;
        //最大的缓存对象个数，如果想作为最大的字节数来表示那么需要在创建lruCache时重写sizeOf()方法，
        //来返回每个图片的字节大小。
        private int maxSize = 10;
        public BitmapCache(){
            cache = new LruCache<>(maxSize);
//            把缓存的大小用字节数来表示
//            cache = new LruCache<String,Bitmap>(maxSize){
//                @Override
//                protected int sizeOf(String key, Bitmap value) {
//                    return super.sizeOf(key, value);
//                }
//            };
        }

        @Override
        public Bitmap getBitmap(String key) {
            return cache.get(key);
        }

        @Override
        public void putBitmap(String key, Bitmap bitmap) {
                cache.put(key,bitmap);
        }
    }

    public void showImage(String url,NetworkImageView netWorkImageView){
        netWorkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        netWorkImageView.setErrorImageResId(android.R.drawable.ic_delete);
        netWorkImageView.setImageUrl(url,imageLoader);
    }

    public static HttpRequestUtils getInstance(Context context) {
        if (httpRequestUtils==null) {
            synchronized (HttpRequestUtils.class){
                if (httpRequestUtils==null) {
                    httpRequestUtils = new HttpRequestUtils(context);
                }
            }
        }
        return httpRequestUtils;
    }


}
