package com.artdream.Nan_Wing.IAT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.artdream.Nan_Wing.InterActive.IA_handle;
import com.artdream.Nan_Wing.MainActivity;
import com.artdream.Nan_Wing.Public.PublicUtil;
import com.artdream.Nan_Wing.TTS.Tts_mainUtil;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by BulletYuan on 2016/7/26 0026.
 */
public class Iat_mainUtil {
    public SpeechRecognizer mspeech;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SharedPreferences mSharedPreferences;
    public Context mContext;
    public int mIatStatut=0;  //0:停止录音 | 1:开始录音
    public String mResult=""; //返回语音识别的文字结果
    public int stopCode=0;
    IA_handle ia_handle;
    PublicUtil publicUtil=new PublicUtil();
    Tts_mainUtil tts_mainUtil=new Tts_mainUtil(MainActivity.mContext);

    /**
     * 初始化IAT工具
     * @param c
     */
    public Iat_mainUtil(Context c){
        this.mContext=c;
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=578ed5c9");
        mSharedPreferences = getSharedPreferences("com.artdream.Nan_Wind", Activity.MODE_PRIVATE);
        initIat();
    }

    /**
     * 初始化IAT类
     */
    public Iat_mainUtil(){}

    /**
     * 开始录音监听
     * @return
     */
    public void IatStart(){
        int ret=mspeech.startListening(recognizerListener);
        mIatStatut=1;
    }

    /**
     * 停止录音监听
     */
    public void IatStop(){
        mspeech.stopListening();
        mIatStatut=0;
    }

    /**
     * 根据状态停止录音并传值
     * @param s
     */
    public void IatStop(int s) {
        mspeech.stopListening();
        mIatStatut = 0;
        stopCode = s;
    }

    /**
     * 初始化IAT参数
     */
    public void initIat(){
        InitRec();
        setParam();
    }

    /**
     * 创建监听服务
     */
    public void InitRec()
    {
        mspeech=SpeechRecognizer.createRecognizer(mContext, null);
        //Toast.makeText(MainActivity.this, "创建服务", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置语音识别参数
     */
    public void setParam()
    {
        mspeech.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        mspeech.setParameter(SpeechConstant.DOMAIN, "iat");
        mspeech.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mspeech.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        String lag = mSharedPreferences.getString("iat_language_preference","mandarin");
        mspeech.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mspeech.setParameter(SpeechConstant.ACCENT, lag);
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mspeech.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "3000"));
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mspeech.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "3000"));
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mspeech.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));
    }

    /**
     * 语音录制方法
     */
    public RecognizerListener recognizerListener = new RecognizerListener(){

        @Override
        public void onVolumeChanged(int i, byte[] bytes) { }

        @Override
        public void onBeginOfSpeech() {
            // TODO Auto-generated method stub
            mIatStatut=1;
        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
            mIatStatut = 0;
        }

        @Override
        public void onResult(RecognizerResult arg0, boolean arg1) {
            // TODO Auto-generated method stub
            MainActivity.setmButtonStat(0);
            String Result = printResult(arg0).toString();
            if (StringUtil.isBlank(mResult)) {
                if (!StringUtil.isBlank(Result)) {
                    MainActivity.resetWebView();
                    mResult = Result;
                    IatStop();
                    if (stopCode > 0) {
                        if (MainActivity._debug)
                            Log.d("Nan Say", Result);
                        try {
                            ia_handle = new IA_handle(Result);
                        } catch (Exception e) {
                            Log.d("Nan Error", e.toString());
                        }
                    }
                }
            } else {
                if (!mResult.equals(Result)) {
                    if (!StringUtil.isBlank(Result)) {
                        MainActivity.resetWebView();
                        mResult = Result;
                        IatStop();
                        if (stopCode > 0) {
                            if (MainActivity._debug)
                                Log.d("Nan Say", Result);
                            try {
                                ia_handle = new IA_handle(Result);
                            } catch (Exception e) {
                                Log.d("Nan Error", e.toString());
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onError(SpeechError arg0) {
            MainActivity.setmButtonStat(0);
            Log.d("Nan Error", arg0.getErrorCode() + "");
            if (MainActivity.time >= 1 && stopCode > 0) {
                if (arg0.getErrorCode() == 10118) {
                    IatStop();
                    tts_mainUtil.tts_play(publicUtil.lowVoice);
                }
            }
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub
        }

    };

    /**
     * 初始化监听事件
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            /*if (code==0)
            {
                //Log.d("jlyan", "login");
                Toast.makeText(MainActivity.this, "login", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.d("jlyan", "login error"+code);
                Toast.makeText(MainActivity.this, "login error"+code, Toast.LENGTH_SHORT).show();
            }*/
        }
    };

    /**
     * 返回的JSON识别结果解析
     * @param results
     * @return
     */
    private String printResult(RecognizerResult results) {
        String rs = "";
        String text = Iat_JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        rs = resultBuffer.toString();
        return rs;
    }

    /**
     * 重写系统自带方法
     * @param name
     * @param mode
     * @return
     */
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mContext.getSharedPreferences(name, mode);
    }

    /**
     * Resume状态事件
     */
    public void Flower_Resume(){
        FlowerCollector.onResume(mContext);
    }

    /**
     * Pause状态事件
     */
    public void Flower_Pause(){
        IatStop();
        FlowerCollector.onPause(mContext);
    }
}
