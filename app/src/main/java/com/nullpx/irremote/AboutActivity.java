package com.nullpx.irremote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sajjadg on 2/10/17.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //load about html into webview
        WebView wvAbout = (WebView) findViewById(R.id.wv_about);
        wvAbout.setNetworkAvailable(false);
        wvAbout.setWebViewClient(new WebViewClient());
        wvAbout.loadUrl("file:///android_asset/about.html");

    }
}
