package com.ethan.FamiCare;


import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class FCMsend {
    private static String BASE_URL="https://fcm.googleapis.com/fcm/send";

    public static void pushNotification(Context context,String Server_key,String event,String time){
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();//StrictMode 可以為你偵測是否在主執行緒執行了網路請求或檔案存取
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue= Volley.newRequestQueue(context);

        try{
            JSONObject json=new JSONObject();
            json.put("to",Server_key);
            JSONObject notification=new JSONObject();
            notification.put("title",event);
            notification.put("body",time);
            json.put("notification",notification);

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM"+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
                }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params=new HashMap<>();
                    params.put("Content-Type","application/json");
                    params.put("Authorization","key=AAAAEKfheyQ:APA91bHD5kqCsPgFVag_k38QGfkA8DXSY_RYvzdMyBGnQicIU3XtGOEXRFBP80Ws9cQRNRau4KPRBbOAvewmHeOv-teJCV1npcKGNgMPUQqHefd_usgMWpLsDGmV5Ah51HCAVFmecyrF");
                    return params;

                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
