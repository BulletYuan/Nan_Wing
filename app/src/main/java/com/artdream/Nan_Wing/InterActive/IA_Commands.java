package com.artdream.Nan_Wing.InterActive;

/**
 * Created by BulletYuan on 2016/7/26 0026.
 *
 *  句子格式为：起始命令 + 工具指示 + 动作指示(A类/B类) + 目的词汇 + 结尾指示(仅B类检测)
 *      示例： 用（起始命令）百度（工具指示）搜索（动作指示）Bullet（目的词汇）
 *      示例： 用（起始命令）百度（工具指示）搜索关于（动作指示）Bullet（目的词汇）的东西(结尾指示)
 *      示例： 搜索（动作指示）Bullet（目的词汇）
 *      示例： 把（起始命令）QQ（工具指示）打开（动作指示）
 *      示例： 把（起始命令）QQ（工具指示）移动到（动作指示）SD卡中（目的词汇）
 */
public class IA_Commands {

    //起始命令集
    public String[] StartCmd={
            "用",
            "把"
    };

    //工具指示集
    public String[] ToolCmd={
            "百度", //综合查询 标识为 0
            "中国搜索",
            "知乎",

            "今日头条", //新闻查询 标识为 1

            "京东", //商品查询 标识为 2
            "淘宝",
            "天猫",

            "网易云", //音乐查询 标识为 3

            "美团", //生活查询 标识为 4
            "大众点评"
    };
    //工具指示的指向网址
    public String[] ToolCmd_Addr={
            "https://www.baidu.com/",
            "http://m.chinaso.com/",
            "https://www.zhihu.com/",
            "http://m.toutiao.com/",
            "http://www.jd.com/",
            "http://www.taobao.com/",
            "http://www.tmall.com/",
            "http://music.163.com/",
            "http://www.meituan.com/",
            "http://www.dianping.com/"
    };
    //工具指示的搜索接口
    public String[] ToolCmd_SrhAddr={
            "s?ie=UTF-8&wd=",
            "page/search.htm?from=wap&keys=",
            "search?type=content&q=",
            "search/?from=search_tab&keyword=",
            "http://search.jd.com/Search?enc=utf-8&keyword=",
            "https://s.taobao.com/search?q=",
            "https://list.tmall.com/search_product.htm?q=",
            "#/search/m/?s=",
            "http://www.meituan.com/search/city?keyword=",
            "http://www.dianping.com/ajax/json/suggest/search?do=hsc&c=8&s=0&q="
    };

    //A类动作命令集
    public String[] ActionCmd_A={
            "查一下",
            "搜一下",
            "搜索一下",
            "看一下",

            "查查",
            "搜索",
            "搜搜",
            "看看",

            "搜",
            "查",
            "看"
    };
    //B类动作命令后缀
    public String ActionCmd_B="关于";

    //结尾指示集
    public String[] FootCmd={
            "的东西",
            "的事情"
    };


}
