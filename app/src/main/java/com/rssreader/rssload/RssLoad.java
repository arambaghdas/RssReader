package com.rssreader.rssload;

import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RssLoad {
     private Context context;
     public interface OnResponseListener {
        void onResponse(String status, String text);
    }
    private OnResponseListener listener;
    public void setEventListener(OnResponseListener listener) {
        this.listener = listener;
    }
    public RssLoad(Context context) {
        this.context = context;
    }
    public void sendRequest(String url) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse("Success", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse("Fail", "");

            }
        });

        RetryPolicy policy = new DefaultRetryPolicy(20000, 0, 1);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

}
