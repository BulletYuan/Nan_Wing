package com.artdream.Nan_Wing.Public;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

import com.artdream.Nan_Wing.InterActive.IA_handle;

import org.jsoup.helper.StringUtil;

/**
 * Created by BulletYuan on 2016/7/27 0027.
 */
public class PublicUtil {
    IA_handle ia_handle;
    //帮助说明文字
    public String HelpText="你可以对我直接说\"搜索人工智能\"，使用我的综合搜索。";
    //时间过短提示文字
    public String shortVoice="你说的时间太短了，我没听到你说的什么！";
    //说话声音过小提示文字
    public String lowVoice="你说的太小声了，我听不到你说什么！";

    /**
     * 解析地址返回站点名称
     * @param hosturl
     * @return
     */
    public String lookAtHostname(String hosturl){
        String hn = "";
        if(!StringUtil.isBlank(hosturl)) {

            if (hosturl.contains("zhihu"))
                hn = "知乎";
            else if (hosturl.contains("baidu"))
                hn = "百度";
            else if (hosturl.contains("chinaso"))
                hn = "中国搜索";
            else if (hosturl.contains("toutiao"))
                hn = "今日头条";
            else if (hosturl.contains("jd"))
                hn = "京东";
            else if (hosturl.contains("taobao"))
                hn = "淘宝";
            else if (hosturl.contains("tmall"))
                hn = "天猫";
            else if (hosturl.contains("music.163"))
                hn = "网易云音乐";
            else if (hosturl.contains("meituan"))
                hn = "美团";
            else if (hosturl.contains("dianping"))
                hn = "大众点评";
            else
                hn="其他";
        }
        return hn;
    }

    /**
     * 手机震动类
     * @param activity
     * @param milliseconds
     */
    public void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

}
