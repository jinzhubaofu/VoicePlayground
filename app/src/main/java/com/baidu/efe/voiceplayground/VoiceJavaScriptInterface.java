package com.baidu.efe.voiceplayground;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.baidu.speech.VoiceRecognitionService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leon on 16/5/11.
 */
public class VoiceJavaScriptInterface implements RecognitionListener {

    private WebView webview;
    private SpeechRecognizer recognizer;
    private Context context;

    final private static String JS_INSTANCE_NAME = "__efe_voice_playground_native_sdk__";

    final private static String JS_CALLBACK_PREFIX = "javascript:efeVoicePlaygroundCallback";

    public VoiceJavaScriptInterface(Context context, WebView webview) {

        this.context = context;
        this.webview = webview;

        // 语音识别
        recognizer = SpeechRecognizer.createSpeechRecognizer(
                context,
                new ComponentName(context, VoiceRecognitionService.class)
        );

        recognizer.setRecognitionListener(this);

        webview.addJavascriptInterface(this, JS_INSTANCE_NAME);

    }

    @JavascriptInterface
    public void start() {

        Handler handler = new Handler(context.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {

                recognizer.cancel();

                Intent intent = new Intent();

                // 离线仅支持16000采样率
                intent.putExtra("sample", 16000);

                // 离线仅支持中文普通话
                intent.putExtra("language", "cmn-Hans-CN");

                recognizer.startListening(intent);

            }

        });


    }

    @JavascriptInterface
    public void stop() {

        Handler handler = new Handler(context.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                recognizer.stopListening();
            }

        });

    }

    @JavascriptInterface
    public void cancel() {

        Handler handler = new Handler(context.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                recognizer.cancel();
            }

        });

    }

    @Override
    public void onBeginningOfSpeech() {
        javaScriptCallback("speech-begin", "");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        javaScriptCallback("ready", "");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        javaScriptCallback("rms-change", Float.toString(rmsdB));
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        javaScriptCallback("speech-end", "");
    }

    @Override
    public void onError(int error) {
        javaScriptCallback("error", Integer.toString(error));
    }

    @Override
    public void onResults(Bundle results) {

        try {

            String text = results.getString("origin_result");
            JSONObject json = new JSONObject(text);
            JSONObject content = json.getJSONObject("content");
            JSONArray item = content.getJSONArray("item");

            javaScriptCallback("result", item.toString());

        }
        catch (Exception error) {
            javaScriptCallback("error", error.getMessage());
        }

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

        ArrayList<String> results = partialResults
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        javaScriptCallback(
                "partial-result",
                Arrays.toString(
                        results.toArray(new String[results.size()])
                )
        );

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    private void javaScriptCallback(String type, String data) {

        webview.loadUrl(
                JS_CALLBACK_PREFIX + "('" + type + "', '" + data + "')"
        );

    }


}
