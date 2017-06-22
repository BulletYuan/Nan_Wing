package com.artdream.Nan_Wing.TTS;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.artdream.Nan_Wing.MainActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by BulletYuan on 2016/7/21 0021.
 */
public class Tts_mainUtil {
    public SpeechSynthesizer mTts;
    private String voicer = "xiaoyan";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public boolean _speeking=false;

    /**
     * 初始化此TTS类
     * @param c
     */
    public Tts_mainUtil(Context c){
        this.mContext=c;
        this.mSharedPreferences = getSharedPreferences("com.artdream.Nan_Wind", Activity.MODE_PRIVATE);
        tts_init();
    }

    /**
     * 初始化TTS
     */
    public void tts_init(){
        //voiceText=vtxt;
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=578ed5c9");
        mEngineType = SpeechConstant.TYPE_CLOUD;
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(mContext, null);
    }

    /**
     * TTS开始播放方法
     * @param text
     */
    public void tts_play(String text){
        //FlowerCollector.onEvent(ttsc, "tts_play");
        // 设置参数
        setttsParam();
        //Log.d("NAN TTSSay", text);
        mTts.startSpeaking(text, mTtsListener);
    }

    /**
     * TTS暂停播放方法
     */
    public void tts_pause(){
        //FlowerCollector.onEvent(ttsc, "tts_play");
        // 设置参数
        setttsParam();
        //Log.d("NAN TTSSay", text);
        mTts.stopSpeaking();
    }

    /**
     * 参数设置
     * @param
     * @return
     */
    private void setttsParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "57"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "46"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "10"));
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        //mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }


    /**
     * 初始化监听。
     */
    public InitListener mTtsInitListener=new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Log.d("NAN Error", "初始化失败,错误码：" + i);
            }
            else{
                //tts_play(voiceText);
                if(MainActivity._debug)
                    Log.d("NAN TTSSay", "" + i);
            }
        }
    };

    /**
     * 合成回调监听。
     */
    public SynthesizerListener mTtsListener=new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            _speeking=true;
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            mPercentForBuffering = i;
        }

        @Override
        public void onSpeakPaused() {
            _speeking=false;
        }

        @Override
        public void onSpeakResumed() {
            _speeking=true;
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            mPercentForPlaying = i;
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            //Log.d("NAN TTSSay", "over");
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 重写系统自带方法
     * @param name
     * @param mode
     * @return
     */
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mContext.getSharedPreferences(name, mode);
    }

}
