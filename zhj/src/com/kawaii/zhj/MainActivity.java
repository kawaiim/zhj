package com.kawaii.zhj;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.kawaii.zhj.tools.DeviceUtil;

public class MainActivity extends Activity {

	MyView myView;
	String messString;
	String messString1;
	MyHandler ttsHandler = new MyHandler(this);

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("MainActivity", "这里调用了onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {

		Log.i("MainActivity", "这里调用了onPause");
		super.onPause();
	}

	@Override
	protected void onStart() {

		Log.i("MainActivity", "这里调用了onStart");
		super.onPause();
	}

	@Override
	protected void onRestart() {

		Log.i("MainActivity", "这里调用了onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {

		Log.i("MainActivity", "这里调用了onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.i("MainActivity", "这里调用了onStop");
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("MainActivity", " 这里调用了onCreate");
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 锁定横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		myView = new MyView(this, ttsHandler);
		setContentView(myView);
	}

	public void showDialog() {
		new AlertDialog.Builder(this)
				.setMessage(messString)
				.setPositiveButton(messString1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								reGame();
								MainActivity.this.finish();
							}
						}).setTitle("本轮结束").create().show();
	}

	// 重新开始游戏
	public void reGame() {

		if (DeviceUtil.checkPackage(this, "com.taobao.taobao")) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");

			String url = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=224606259.taobao.com/?spm=a230r.7195193.1997079397.9.QOVeCO";
			Uri uri = Uri.parse(url);
			intent.setData(uri);
			startActivity(intent);
		} else {

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, WebViewActivity.class);
			intent.putExtra("webUrl",
					"https://shop224606259.taobao.com/?spm=a230r.7195193.1997079397.9.QOVeCO");
			MainActivity.this.startActivity(intent);
		}
	}

	static class MyHandler extends Handler {
		WeakReference<MainActivity> mActivity;

		MyHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final MainActivity theActivity = mActivity.get();
			switch (msg.what) {
			case 0:
				theActivity.messString = msg.getData().getString("data");
				theActivity.messString1 = msg.getData().getString("data1");
				theActivity.showDialog();
				break;
			}
		}
	}

	public static boolean checkPackage(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}

	}
}
