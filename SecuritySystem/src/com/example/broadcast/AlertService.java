package com.example.broadcast;

import com.example.securitysystem.R;

import controls.ClearEditText;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;

public class AlertService extends Service {

	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	private ImageView mFloatView;
	private ClearEditText passEdt;

	AnimationDrawable drawable;

	private MediaPlayer mp;
	private AudioManager am;

	private SharedPreferences shpPass;

	private static final String TAG = "FxService";

	@Override
	public void onCreate() {
		shpPass = this.getSharedPreferences("password.xml",
				Context.MODE_PRIVATE);

		// 初始化音乐资源
		try {
//			 am=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//			 // 调整音量
//			 am.setMode(AudioManager.MODE_NORMAL);
//			 //音量最大音量
//			 am.setStreamVolume(AudioManager.STREAM_MUSIC,
//			 am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//			 //调整音量 到最高
//			 am.adjustVolume(AudioManager.ADJUST_RAISE,0);

			// 创建MediaPlayer对象
			mp = new MediaPlayer();
			mp = MediaPlayer.create(AlertService.this, R.raw.alert_music);
			// mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "oncreat");
		createFloatView();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createFloatView() {
		// 布局的参数
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		Log.i(TAG, "mWindowManager--->" + mWindowManager);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		// wmParams.flags = LayoutParams.FLAG_;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		// wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/*
		 * // 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;
		 */

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,
				null);
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		// 浮动窗口按钮
		mFloatView = (ImageView) mFloatLayout.findViewById(R.id.alert_image);
		passEdt = (ClearEditText) mFloatLayout.findViewById(R.id.pass_float);

		drawable = (AnimationDrawable) mFloatView.getBackground();
		drawable.start();

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		// 设置监听浮动窗口的触摸移动
		mFloatView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub

				// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
				wmParams.x = (int) event.getRawX()
						- mFloatView.getMeasuredWidth() / 2;
				Log.i(TAG, "RawX" + event.getRawX());
				Log.i(TAG, "X" + event.getX());
				// 减25为状态栏的高度

				wmParams.y = (int) event.getRawY()
						- mFloatView.getMeasuredHeight() / 2 - 25;
				Log.i(TAG, "RawY" + event.getRawY());
				Log.i(TAG, "Y" + event.getY());
				// 刷新
				mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				return false; // 此处必须返回false，否则OnClickListener获取不到监听
			}

		});

		mFloatView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub 这里 判断输入的密码是否正确，正确的话解除 警报
				String passStr = passEdt.getText().toString();
				String passWorld = shpPass.getString("password", null);
				if (passWorld != null) {
					if (passStr.equals(passWorld)) {
						onDestroy();
						shpPass.edit().putBoolean("isAlert",false).commit();
					}
				}

			}
		});
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		

		playAlertMusic();

		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	/***
	 * 播放报警音乐
	 */
	private void playAlertMusic() {

		// 开始播放音乐
		mp.start();
		// 音乐播放完毕的事件处理
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
//				 am=(AudioManager)AlertService.this.
//						 getSystemService(Context.AUDIO_SERVICE);
//				 // 调整音量
//				 am.setMode(AudioManager.MODE_NORMAL);
//				 //音量最大音量
//				 am.setStreamVolume(AudioManager.STREAM_MUSIC,
//				 am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//				 //调整音量 到最高
//				 am.adjustVolume(AudioManager.ADJUST_RAISE,0);
				 
				// 循环播放
				try {
					mp.start();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// 播放音乐时发生错误的事件处理
		mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				// 释放资源
				try {
					mp.release();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return false;
			}
		});

	}

	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
		if (mp != null && mp.isPlaying()) {
			mp.stop();
			mp.release();
		}

	}
}
