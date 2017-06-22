package com.artdream.Nan_Wing.WebPage;

import android.util.Log;

import com.artdream.Nan_Wing.InterActive.IA_Commands;
import com.artdream.Nan_Wing.MainActivity;
import com.artdream.Nan_Wing.Public.PublicUtil;
import com.artdream.Nan_Wing.TTS.Tts_mainUtil;
import com.artdream.Nan_Wing.Unicode.Unicode_util;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BulletYuan on 2016/7/25 0025.
 */
public class PageDOM {
    Unicode_util unicode_util = new Unicode_util();
    Tts_mainUtil tts=new Tts_mainUtil(MainActivity.mContext);
    JSInterface jsInterface=new JSInterface();
    IA_Commands ia_commands=new IA_Commands();
    PublicUtil publicUtil=new PublicUtil();

    public String PageAddr,KeyTxt;
    public Document doc;
    public int cot=0;

    public ArrayList<String> search_rs_tit=new ArrayList<String>();
    public ArrayList<String> search_rs_href=new ArrayList<String>();

    /**
     * 初始化搜索地址
     * @param
     */
    public PageDOM(){
//        this.PageAddr=pa;
//        this.cot=c;
    }

    /**
     * 获取当前搜索地址
     * @return
     */
    public String getPageAddr(){
        String pa=this.PageAddr;
        return pa;
    }

    /**
     * 设置搜索关键词（UTF8）
     * @param kt
     */
    public void setKeyTxt(String kt){
        this.KeyTxt=kt;
    }

    /**
     * 获取当前关键词（String）
     * @return
     */
    public String getKeyTxt(){
        String kt= URLDecoder.decode(this.KeyTxt);
        return kt;
    }

    /**
     * 初始化页面DOM并返回JSON结果
     * @return
     */
    public void getPageDom(int a) {
        final int b = a;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    PageAddr = ia_commands.ToolCmd_Addr[b] + ia_commands.ToolCmd_SrhAddr[b];

                    String k;
                    if (getPageAddr().contains("zhihu"))
                        k = KeyTxt;
                    else
                        k = getKeyTxt();
                    org.jsoup.Connection conn = Jsoup.connect(getPageAddr() + k);
                    doc = conn.get();
                    if (MainActivity._debug)
                        Log.d("Nan Say", "搜索网址: " + getPageAddr() + getKeyTxt() + "\n");
                    getDomResult();

                } catch (IOException e) {
                    Log.d("Nan Error", e.toString());
                } catch (JSONException e) {
                    Log.d("Nan Error", e.toString());
                }
            }
        }).start();

    }

    /**
     * 拿到document模块之后的操作（DOM核心）
     * @throws JSONException
     */
    public void getDomResult() throws JSONException {
        Pattern pat;
        Matcher mat;
        String kt = this.KeyTxt;
//        Log.d("HTML DOM", "Address --- " + getPageAddr() + "\n");
//        Log.d("HTML DOM","Title --- "+doc.title()+"\n");
//        Log.d("HTML DOM"," a ---------- \n\r");

        String ktArr[] = null;
        String hostUrl = doc.location().split("com")[0] + "com";
        if (kt.indexOf("+") > 0) {
            ktArr = kt.split("[+]");
        }
        Element doc_body = doc.body();
        Elements a_ems = doc_body.getElementsByTag("a");
        for (Element link : a_ems) {
            String linkText = link.text().replaceAll("\\s*", "").toLowerCase();
            if (ktArr != null) {
                for (int z = 0; z < ktArr.length; z++) {
                    String p_str = unicode_util.ToUnicode(URLDecoder.decode(ktArr[z])).replace("\\", "");
                    String m_str = unicode_util.ToUnicode(linkText).replace("\\", "");
                    pat = Pattern.compile(p_str);
                    mat = pat.matcher(m_str);
                    boolean b = mat.find();
                    if (!StringUtil.isBlank(linkText.toString()) && b) {
                        String linkHref = link.attr("href");
                        if (!linkHref.contains("http"))
                            linkHref = hostUrl + linkHref;
                        search_rs_tit.add("\"" + publicUtil.lookAtHostname(hostUrl) + " :<br>  " + linkText + "\"");
                        search_rs_href.add("\"" + linkHref + "\"");
                        break;
                    }
                }
            } else {
                String kk = getKeyTxt();
                kk=kk.replaceAll("[0-9]","");
                for (int v = 0; v < kk.length(); v++) {
                    String kc = kk.substring(v, v + 1);
                    if (!StringUtil.isBlank(linkText.toString()) && linkText.contains(kc)) {
                        String linkHref = link.attr("href");
                        if (!linkHref.contains("http"))
                            linkHref = hostUrl + linkHref;
                        String hn=publicUtil.lookAtHostname(hostUrl);
                        search_rs_tit.add("\"" + hn + " :<br>  " + linkText + "\"");
                        search_rs_href.add("\"" + linkHref + "\"");
                        break;
                    }
                }
            }
        }

        try {
            Thread.sleep(800);
        }catch (InterruptedException e){
            Log.d("Nan Error", e.toString());
        }

        cot += 1;
        if (cot == 3) {
            if (MainActivity._debug) {
                Log.d("Nan Say", "结果条数: " + search_rs_tit.size() + "\n");
                for (int i = 0; i < search_rs_tit.size(); i++) {
                    Log.d("Nan Say", "结果" + i + ": " + search_rs_tit.get(i) + "\n");
                    Log.d("Nan Say", "结果" + i + " 的链接: " + search_rs_href.get(i) + "\n");
                }
                Log.d("Nan Say", "数组结果: " + search_rs_tit.toString() + "\n");
            }

            for(int v=0; v<search_rs_tit.size();v++) {
                jsInterface.setResult(search_rs_tit.get(v), search_rs_href.get(v));
            }

            tts.tts_play("以上是关于 " + getKeyTxt() + " 的" + search_rs_tit.size() + "条综合搜索结果");

            a_ems=null;
            doc=null;
            pat=null;
            mat=null;
            search_rs_tit=null;
            search_rs_href=null;
        }

    }


}

