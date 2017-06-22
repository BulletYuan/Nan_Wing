package com.artdream.Nan_Wing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.artdream.Nan_Wing.IAT.Iat_mainUtil;
import com.artdream.Nan_Wing.Public.PublicUtil;
import com.artdream.Nan_Wing.TTS.Tts_mainUtil;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {
    Iat_mainUtil iat;
    Tts_mainUtil tts;
    PublicUtil publicUtil = new PublicUtil();

    public static boolean netbool = false;

    public static Context mContext;
    public static WebView webView;
    public static Button mButton;
    public static boolean _debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (int i = 0; i < networkInfo.length; i++) {
            if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                netbool = true;
            }
        }
        if (netbool) {

            webView = (WebView) findViewById(R.id.webView);
            mButton = (Button) findViewById(R.id.button1);
            iat = new Iat_mainUtil(MainActivity.this);
            tts = new Tts_mainUtil(MainActivity.this);

            initWebView();

            setmButtonStat(0);
            mButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onTouchEvent(event);
                    return false;
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "没网也要聊骚我，蠢货！", Toast.LENGTH_LONG).show();
        }
    }

    long startVoiceT = 0, endVoiceT = 0;
    int touch_flag = 1;
    public static int time = 0;

    /**
     * 按住说话监听事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (netbool) {
            int[] location = new int[2];
            mButton.getLocationInWindow(location);
            int btn_rc_Y = mButton.getHeight();
            int btn_rc_X = mButton.getWidth();

            if (event.getAction() == MotionEvent.ACTION_DOWN && touch_flag == 1) {//判断手势按下的位置是否是语音录制按钮的范围内
                if (MainActivity._debug)
                    Log.d("Nan Say", "按钮坐标 X: " + btn_rc_X + " Y: " + btn_rc_Y + " 触碰坐标 X:" + event.getX() + " Y: " + event.getY() + "\n");
                if (MainActivity._debug)
                    Log.d("Nan Say", "按下语音监听键\n");
                if (event.getX() > 0 && event.getY() > 0 && event.getY() < btn_rc_Y && event.getX() < btn_rc_X) {
                    if (netbool) {
                        publicUtil.Vibrate(MainActivity.this, 30);
                        if (MainActivity._debug)
                            Log.d("Nan Say", "执行语音监听事件\n");
                        setmButtonStat(1);
                        startVoiceT = System.currentTimeMillis();
                        touch_flag = 2;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (tts._speeking)
                                    tts.tts_pause();
                                iat.IatStart();
                            }
                        }).start();
                    } else {
                        Toast.makeText(MainActivity.this, "没网也要聊骚我，蠢货！", Toast.LENGTH_LONG).show();
                    }
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP && touch_flag == 2) {//松开手势时执行录制完成
                if (MainActivity._debug)
                    Log.d("Nan Say", "按钮坐标 X: " + btn_rc_X + " Y: " + btn_rc_Y + " 触碰坐标 X:" + event.getX() + " Y: " + event.getY() + "\n");
                if (MainActivity._debug)
                    Log.d("Nan Say", "松开语音监听键\n");
                endVoiceT = System.currentTimeMillis();
                touch_flag = 1;
                if (tts._speeking)
                    tts.tts_pause();
                setmButtonStat(0);
                if (event.getX() > 0 && event.getY() > 0 && event.getY() < btn_rc_Y && event.getX() < btn_rc_X) {
                    time = (int) ((endVoiceT - startVoiceT) / 1000);
                    if (MainActivity._debug)
                        Log.d("Nan Say", "按下语音监听键时间合计" + time + "\n");
                    if (time < 1) {
                        iat.IatStop(-1);
                        if (tts._speeking)
                            tts.tts_pause();
                        tts.tts_play(publicUtil.shortVoice);
                    } else {
                        iat.IatStop(1);
                    }
                } else {
                    iat.IatStop(-1);
                    return false;
                }

            }
            if (event.getAction() == MotionEvent.ACTION_MOVE && touch_flag == 2) {
                if (event.getX() < 0 || event.getY() < 0 || event.getY() > btn_rc_Y || event.getX() > btn_rc_X) {
                    mButton.setText("放弃搜索");
                    Resources resources = MainActivity.mContext.getResources();
                    Drawable btnDrawable = resources.getDrawable(R.drawable.mbutton_l);
                    mButton.setBackgroundDrawable(btnDrawable);
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "没网也要聊骚我，蠢货！", Toast.LENGTH_LONG).show();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置WebView监听事件
     */
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient() {
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //webView.loadUrl("javascript:setKey(\"%E9%92%A2%E9%93%81%E4%BE%A0\")");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

        webView.addJavascriptInterface(this, "Nan");

        webView.loadUrl("file:///android_asset/Nan/index.html");

    }

    /**
     * JS调用原生方法接口 打开新窗口
     *
     * @param nurl
     */
    @JavascriptInterface
    public void openNewActivity(String nurl) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("url", nurl);
        intent.putExtras(bundle);
        intent.setClass(MainActivity.this, WebPageActivity.class);
        startActivity(intent);
    }

    /**
     * 复原webview
     */
    public static void resetWebView() {
        webView.loadUrl("about:blank");
        if (MainActivity.webView.getVisibility() == View.GONE)
            MainActivity.webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearFormData();
        if (!webView.getUrl().contains("file:///"))
            webView.loadUrl("file:///android_asset/Nan/index.html");
    }

    /**
     * 调用页面的方法的js接口
     *
     * @param url
     */
    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    public static void setWebViewUrl(String url) {
        final String nu = url;

        webView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (MainActivity.webView.getVisibility() == View.GONE)
                        MainActivity.webView.setVisibility(View.VISIBLE);
                    if (!webView.getUrl().contains("file:///"))
                        webView.loadUrl("file:///android_asset/Nan/index.html");
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            webView.loadUrl(nu);
                        }
                    });
                    if (MainActivity._debug)
                        Log.d("Nan Say", "JS请求地址及参数:" + nu + "\n");
                } catch (Exception e) {
                    Log.d("Nan Error", e.toString());
                }
            }
        });
    }

    /**
     * 设置按钮状态
     *
     * @param s
     */
    public static void setmButtonStat(int s) {
        if (s > 0) {//不可按状态
            mButton.setText("我听着呢...");
            mButton.setTextColor(Color.argb(255, 80, 80, 80));
            Resources resources = MainActivity.mContext.getResources();
            Drawable btnDrawable = resources.getDrawable(R.drawable.mbutton_d);
            mButton.setBackgroundDrawable(btnDrawable);
        } else {//可按状态
            mButton.setText("按住说");
            mButton.setTextColor(Color.argb(255, 30, 30, 30));
            Resources resources = MainActivity.mContext.getResources();
            Drawable btnDrawable = resources.getDrawable(R.drawable.mbutton_s);
            mButton.setBackgroundDrawable(btnDrawable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        iat = new Iat_mainUtil(MainActivity.mContext);
        tts = new Tts_mainUtil(MainActivity.mContext);
        iat.Flower_Resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        iat.Flower_Pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.setVisibility(View.GONE);
        webView.removeAllViews();
        webView.destroy();
        setContentView(R.layout.activity_null);
    }
}
