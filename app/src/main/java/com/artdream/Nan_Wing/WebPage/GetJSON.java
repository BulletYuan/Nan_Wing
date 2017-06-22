package com.artdream.Nan_Wing.WebPage;

import android.util.Log;

import com.artdream.Nan_Wing.MainActivity;
import com.artdream.Nan_Wing.Unicode.Unicode_util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by BulletYuan on 2016-10-10.
 */
public class GetJSON {

    public static List<ListParams> getJSONLastNews(String req_url) {
        String path = req_url;
        if (MainActivity._debug)
            Log.d("Nan Say", "搜索地址: " + req_url);
        String gS = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(50000);
//            conn.setReadTimeout(100000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                if (MainActivity._debug)
                    Log.d("Nan Say", "地址连接状态: 成功");
                InputStream is=conn.getInputStream();
//                int count = 0;
//                while (count == 0) {
//                    count = is.available();
//                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer out = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    out.append(line);
                }
                String json = out.toString();
//                Log.d("Nan Say", "返回数据:\n" + json);
                gS = json;
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.d("Nan Error", e.toString());
        }
        return parseJSON(gS);
    }

    private static List<ListParams> parseJSON(String json) {
        List<ListParams> list = new ArrayList<ListParams>();
        if (json.length() > 0) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int weight = jsonObject.getInt("weight");
                    String link = jsonObject.getString("link");
                    String title = jsonObject.getString("title");
                    Log.d("Nan Say", "序列化数据:\n" + link + "\n" + weight + "\n" + title + "\n\n");
                    list.add(new ListParams(link, weight, title));
                }
            } catch (Exception e) {
                Log.d("Nan Error", e.toString());
            }
        }
        return list;
    }
}
