package com.baidu.efe.voiceplayground;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.url);

        assert editText != null;

        editText.setOnEditorActionListener(this);

        ImageButton goButton = (ImageButton) findViewById(R.id.go);


        assert goButton != null;

        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText urlEditText = (EditText) findViewById(R.id.url);
                openPage(urlEditText.getText().toString());

            }

        });

        Button demoButton = (Button) findViewById(R.id.demo_button);

        assert demoButton != null;

        demoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String t = Long.toHexString(System.currentTimeMillis());

                openPage("http://boscdn.bpc.baidu.com/mms-res/efe-voice-playground/index4.html?t=" + t);

            }

        });

    }

    private void openPage(String uri) {

        if (uri == null || uri.length() == 0) {
            return;
        }

        // 收起键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        Intent intent = new Intent(this, PageActivity.class);

        intent.putExtra("uri", uri);
        startActivity(intent);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {

            case EditorInfo.IME_ACTION_GO:

                EditText urlEditText = (EditText) findViewById(R.id.url);
                openPage(urlEditText.getText().toString());

                return true;

            default:
                return false;

        }
    }

}
