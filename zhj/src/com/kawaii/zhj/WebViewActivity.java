package com.kawaii.zhj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("WebViewActivity", " ���������onCreate");
		super.onCreate(savedInstanceState);
		webView = (WebView) findViewById(R.id.webView);
		webView = new WebView(this);
		Intent intent = this.getIntent();
		// ������Ҫ��ʾ����ҳ
		Bundle url = intent.getExtras();
		webView.loadUrl(url.get("webUrl").toString());

		setContentView(R.layout.activity_main);
	}

}
