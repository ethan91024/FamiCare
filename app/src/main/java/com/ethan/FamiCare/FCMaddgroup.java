package com.ethan.FamiCare;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMaddgroup {
    private static String BASE_URL="https://fcm.googleapis.com/fcm/notification";

    public static void addgroup(Context context,String operation,String token) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//StrictMode 可以為你偵測是否在主執行緒執行了網路請求或檔案存取
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        JSONArray registrationIds = new JSONArray();
        registrationIds.put(token);
        System.out.println("registrationIds"+registrationIds);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operation", operation);
            jsonObject.put("notification_key_name", "appUser-Cindy");
            jsonObject.put("notification_key", "APA91bEg-xO9Rlyb72AGxpt3wNoyKAYsA-9-fdbWKSNxyaG8qxz2syGfiwWVXoHLwZ2EIygaygZXGF19Ge1lL9h40NDhimvwoYJXJc37P2X3gWZDn7O0cA4");
            jsonObject.put("registration_ids", registrationIds);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCMaddgroup" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NetworkError) {
                        // 網路錯誤
                        System.out.println("1");
                    } else if (error instanceof ServerError) {
                        // 伺服器錯誤
                        System.out.println("2");
                    } else if (error instanceof AuthFailureError) {
                        // 授權失敗錯誤
                        System.out.println(3);
                    }  else if (error instanceof NoConnectionError) {
                        // 沒有連線錯誤
                        System.out.println("4");
                    } else if (error instanceof TimeoutError) {
                        // 請求超時錯誤
                        System.out.println("5");
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=AAAAEKfheyQ:APA91bHD5kqCsPgFVag_k38QGfkA8DXSY_RYvzdMyBGnQicIU3XtGOEXRFBP80Ws9cQRNRau4KPRBbOAvewmHeOv-teJCV1npcKGNgMPUQqHefd_usgMWpLsDGmV5Ah51HCAVFmecyrF");
                    params.put("project_id", "71536048932");
                    return params;

                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
