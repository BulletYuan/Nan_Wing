package com.artdream.Nan_Wing.InterActive;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.artdream.Nan_Wing.MainActivity;
import com.artdream.Nan_Wing.Public.PublicUtil;
import com.artdream.Nan_Wing.TTS.Tts_mainUtil;
import com.artdream.Nan_Wing.Unicode.Unicode_util;
import com.artdream.Nan_Wing.WebPage.GetJSON;
import com.artdream.Nan_Wing.WebPage.JSInterface;
import com.artdream.Nan_Wing.WebPage.ListParams;
import com.artdream.Nan_Wing.WebPage.PageDOM;

import org.jsoup.helper.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BulletYuan on 2016/7/26 0026.
 */
public class IA_handle {
    IA_Commands ia_cmd=new IA_Commands();
    Unicode_util unicode_util=new Unicode_util();
    PageDOM pDom;
    Tts_mainUtil tts=new Tts_mainUtil(MainActivity.mContext);
    PublicUtil pUtil;
    JSInterface jsInterface=new JSInterface();

    public String memo="";
    public String iat_result="";
    public String iat_e_result="";

    public String ToolCmd_Addr="";
    public String ToolCmd_SrhAddr="";

    public int StartCmd_stat=-1;
    public int ToolCmd_stat=-1;
    public int ActionCmd_stat=-1;
    public int ActionType_stat=-1;
    public int FootCmd_stat=-1;

    public void resetParams() {
        memo = "";
        iat_result = "";
        iat_e_result = "";

        ToolCmd_Addr = "";
        ToolCmd_SrhAddr = "";

        StartCmd_stat = -1;
        ToolCmd_stat = -1;
        ActionCmd_stat = -1;
        ActionType_stat = -1;
        FootCmd_stat = -1;

    }

    /**
     * 初始化获取识别结果
     * @param s
     */
    public IA_handle(String s){
        if(!StringUtil.isBlank(s)) {
            this.iat_result = s;
//            Log.d("Nan Error", "IA_handle-findStartCmd");
            findStartCmd();
        }
    }

    /**
     * 查找句子首字是否符合开始指令
     */
    public void findStartCmd() {
        try {
            String scmd = this.iat_result;
            scmd = scmd.substring(0, 1);
            for (int si = 0; si < ia_cmd.StartCmd.length; si++) {
                String uni_scmd = unicode_util.ToUnicode(ia_cmd.StartCmd[si]).replace("\\", "");
                String uni_cmd = unicode_util.ToUnicode(scmd).replace("\\", "");

                Pattern p = Pattern.compile(uni_scmd);
                Matcher m = p.matcher(uni_cmd);

                if (m.matches()) {
                    this.StartCmd_stat = si;
                    this.iat_e_result = this.iat_result.substring(1, this.iat_result.length());
                    break;
                }

            }

//            Log.d("Nan Error", "findStartCmd-findActionCmd");
            findActionCmd();
        } catch (Exception e) {
            Log.d("Nan Error", e.toString());
        }
    }

    /**
     * 查找句子的动作指令
     */
    public void findActionCmd() {

        String ers = StringUtil.isBlank(this.iat_e_result) ? this.iat_result : this.iat_e_result;
        try {
            if (this.StartCmd_stat > 0) { //“把”动作指令

            } else  { //“用”动作指令 或 无起始命令

                String ers_0 = "", ers_1 = "";

                String uni_x = unicode_util.ToUnicode(ia_cmd.ActionCmd_B).replace("\\", "");
                String uni_xd = unicode_util.ToUnicode(ers).replace("\\", "");

                Pattern p1 = Pattern.compile(uni_x);
                Matcher m1 = p1.matcher(uni_xd);

                if(!m1.find()) {
                    for (int si = 0; si < ia_cmd.ActionCmd_A.length; si++) {
                        String uni_scmd = unicode_util.ToUnicode(ia_cmd.ActionCmd_A[si]).replace("\\", "");
                        String uni_cmd = unicode_util.ToUnicode(ers).replace("\\", "");

                        Pattern p = Pattern.compile(uni_scmd);
                        Matcher m = p.matcher(uni_cmd);

                        if (m.find()) {
                            this.ActionCmd_stat = si;
                            this.ActionType_stat = 0;
                            ers_0 = ers.split(ia_cmd.ActionCmd_A[si])[0].toString();
                            ers_1 = ers.split(ia_cmd.ActionCmd_A[si])[1].toString();
                            break;
                        }
                    }
                }else{
                    for (int si = 0; si < ia_cmd.ActionCmd_A.length; si++) {
                        String uni_scmd = unicode_util.ToUnicode(ia_cmd.ActionCmd_A[si]+ia_cmd.ActionCmd_B).replace("\\", "");
                        String uni_cmd = unicode_util.ToUnicode(ers).replace("\\", "");

                        Pattern p = Pattern.compile(uni_scmd);
                        Matcher m = p.matcher(uni_cmd);

                        if (m.find()) {
                            this.ActionCmd_stat = si;
                            this.ActionType_stat = 1;
                            ers_0 = ers.split(ia_cmd.ActionCmd_A[si]+ia_cmd.ActionCmd_B)[0].toString();
                            ers_1 = ers.split(ia_cmd.ActionCmd_A[si]+ia_cmd.ActionCmd_B)[1].toString();
                            break;
                        }
                    }
                }

                if (!StringUtil.isBlank(ers_0)) {
                    findToolCmd(ers_0);
                }else {
                    this.ToolCmd_stat = -1;
                    this.ToolCmd_Addr = this.ToolCmd_SrhAddr = "";
                }

                if (!StringUtil.isBlank(ers_1)) {
                    searchMemo(ers_1);
                }
            }

        } catch (Exception e) {
            Log.d("Nan Error", e.toString());
        }

    }

    /**
     * 查找分割句0的工具指示
     * @param tc_s
     */
    public void findToolCmd(String tc_s) {
        try {
            for (int si = 0; si < ia_cmd.ToolCmd.length; si++) {
                String uni_scmd = unicode_util.ToUnicode(ia_cmd.ToolCmd[si]).replace("\\", "");
                String uni_cmd = unicode_util.ToUnicode(tc_s).replace("\\", "");

                Pattern p = Pattern.compile(uni_scmd);
                Matcher m = p.matcher(uni_cmd);

                if (m.find()) {
                    this.ToolCmd_stat = si;
                    this.ToolCmd_Addr = ia_cmd.ToolCmd_Addr[si];
                    int bi=ia_cmd.ToolCmd_SrhAddr[si].indexOf("http");
                    if (bi > -1)
                        this.ToolCmd_SrhAddr = ia_cmd.ToolCmd_SrhAddr[si];
                    else
                        this.ToolCmd_SrhAddr = this.ToolCmd_Addr + ia_cmd.ToolCmd_SrhAddr[si];
                    break;
                }
            }
        } catch (Exception e) {
            Log.d("Nan Error", e.toString());
        }
    }

    /**
     * 搜索说出的关键字
     * @param ers_1
     */
    public void searchMemo(String ers_1){
        String memo = "", memo_utf8 = "";
        try {
            if (this.ToolCmd_stat > -1) {
                if (this.ActionType_stat > 0) {
                    for (int si = 0; si < ia_cmd.FootCmd.length; si++) {
                        String uni_scmd = unicode_util.ToUnicode(ia_cmd.FootCmd[si]).replace("\\", "");
                        String uni_cmd = unicode_util.ToUnicode(ers_1).replace("\\", "");

                        Pattern p = Pattern.compile(uni_scmd);
                        Matcher m = p.matcher(uni_cmd);

                        if (m.find()) {
                            this.FootCmd_stat = si;
                            memo = ers_1.split(ia_cmd.FootCmd[this.FootCmd_stat])[0].toString();
                            break;
                        }
                    }

                    if (StringUtil.isBlank(memo)) {
                        memo = ers_1.toString();
                        memo_utf8 = URLEncoder.encode(memo);
                        if (MainActivity._debug)
                            Log.d("Nan Say", "正在用 " + ia_cmd.ToolCmd[this.ToolCmd_stat] + " 帮你搜索 " + memo);
                        if (tts._speeking)
                            tts.tts_pause();
                        tts.tts_play("正在用 " + ia_cmd.ToolCmd[this.ToolCmd_stat] + " 帮你搜索 " + memo);
                        webView_search(this.ToolCmd_SrhAddr + memo_utf8);
                    } else {
                        memo_utf8 = URLEncoder.encode(memo);
                        if (MainActivity._debug)
                            Log.d("Nan Say", "正在用 " + ia_cmd.ToolCmd[this.ToolCmd_stat] + " 帮你搜索 " + memo);
                        if (tts._speeking)
                            tts.tts_pause();
                        tts.tts_play("正在用 " + ia_cmd.ToolCmd[this.ToolCmd_stat] + " 帮你搜索 " + memo);
                        webView_search(this.ToolCmd_SrhAddr + memo_utf8);
                    }
                } else if (this.ActionType_stat == 0) {
                    memo = ers_1.toString();
                    memo_utf8 = URLEncoder.encode(memo);
                    if (MainActivity._debug)
                        Log.d("Nan Say", "正在用 " + ia_cmd.ToolCmd[this.ToolCmd_stat] + " 帮你搜索 " + memo);
                    if (tts._speeking)
                        tts.tts_pause();
                    tts.tts_play("正在用 " + ia_cmd.ToolCmd[this.ToolCmd_stat] + " 帮你搜索 " + memo);
                    webView_search(this.ToolCmd_SrhAddr + memo_utf8);
                }
            } else {
                memo = ers_1.toString();
                final String m_utf8 = URLEncoder.encode(memo);
                if (MainActivity._debug)
                    Log.d("Nan Say", "正在搜索 " + memo);
                if (tts._speeking)
                    tts.tts_pause();
                tts.tts_play("正在搜索 " + memo);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /*pDom = new PageDOM();
                            pDom.setKeyTxt(m_utf8);
                            pDom.getPageDom(0);
                            pDom.getPageDom(1);
                            pDom.getPageDom(2);
                            //pDom.getPageDom(3);
                            List<ListParams> jsonlist ;
                            jsonlist = GetJSON.getJSONLastNews("http://fakepi.applinzi.com/?" + m_utf8);
                            if (MainActivity._debug)
                                Log.d("Nan Say", "搜索结果数目: " + jsonlist.size());

                            tts.tts_play("以上是关于 " + fmemo + " 的" + jsonlist.size() + "条综合搜索结果");
                            */
                            jsInterface.setKey(m_utf8);

                        } catch (Exception e) {
                            Log.d("Nan Error", e.toString());
                        }
                    }
                }).start();
            }

            resetParams();
        } catch (Exception e) {
            Log.d("Nan Error", e.toString());
        }

    }

    /**
     * 加载页面搜索关键词
     * @param url
     */
    public void webView_search(String url){
        if(MainActivity._debug)
            Log.d("Nan Say URL", url);
        try {
            if (MainActivity.webView.getVisibility() == View.GONE)
                MainActivity.webView.setVisibility(View.VISIBLE);
            MainActivity.webView.loadUrl(url);
        }catch (Exception e){ Log.d("Nan Error", e.toString());}
    }

}
