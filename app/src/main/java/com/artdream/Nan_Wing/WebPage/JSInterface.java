package com.artdream.Nan_Wing.WebPage;

import com.artdream.Nan_Wing.MainActivity;


/**
 * Created by BulletYuan on 2016/7/27 0027.
 * JS与Android交互接口方法
 */
public class JSInterface {

    /**
     * Android传解析到的数据集合给WEB
     * @param tit
     * @param link
     * @return
     */
    public void setResult(String tit,String link) {
        MainActivity.setWebViewUrl("javascript:setResult(" + tit + "," + link + ")");
    }

    /**
     * Android返回的关键字给WEB
     * @param keyword
     * @return
     */
    public void setKey(String keyword) {
        MainActivity.setWebViewUrl("javascript:setKey(\"" + keyword + "\")");
    }
}
