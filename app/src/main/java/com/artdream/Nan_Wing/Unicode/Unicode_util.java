package com.artdream.Nan_Wing.Unicode;

/**
 * Created by BulletYuan on 2016/7/26 0026.
 */
public class Unicode_util {

    /**
     * 字符串转码为UNICODE
     * @param str
     * @return
     */
    public String ToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            result+="\\u" + Integer.toHexString(chr1);
            /*if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }*/
        }
        return result;
    }

    /**
     * UNICODE转码为字符串
     * @param dataStr
     * @return
     */
    public String ToString(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }
}
