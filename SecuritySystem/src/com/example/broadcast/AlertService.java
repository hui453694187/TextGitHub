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

	// ���帡�����ڲ���
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// ���������������ò��ֲ����Ķ���
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

		// ��ʼ��������Դ
		try {
//			 am=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//			 // ��������
//			 am.setMode(AudioManager.MODE_NORMAL);
//			 //�����������
//			 am.setStreamVolume(AudioManager.STREAM_MUSIC,
//			 am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//			 //�������� �����
//			 am.adjustVolume(AudioManager.ADJUST_RAISE,0);

			// ����MediaPlayer����
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
		// ���ֵĲ���
		wmParams = new WindowManager.LayoutParams();
		// ��ȡ����WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		Log.i(TAG, "mWindowManager--->" + mWindowManager);
		// ����window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// ����ͼƬ��ʽ��Ч��Ϊ����͸��
		wmParams.format = PixelFormat.RGBA_8888;
		// ���ø������ڲ��ɾ۽���ʵ�ֲ���������������������ɼ����ڵĲ�����
		// wmParams.flags = LayoutParams.FLAG_;
		// ������������ʾ��ͣ��λ��Ϊ����ö�
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ�������gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// �����������ڳ�������
		// wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/*
		 * // �����������ڳ������� wmParams.width = 200; wmParams.height = 80;
		 */

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// ��ȡ����������ͼ���ڲ���
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,
				null);
		// ���mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		// �������ڰ�ť
		mFloatView = (ImageView) mFloatLayout.findViewById(R.id.alert_image);
		passEdt = (ClearEditText) mFloatLayout.findViewById(R.id.pass_float);

		drawable = (AnimationDrawable) mFloatView.getBackground();
		drawable.start();

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		// ���ü����������ڵĴ����ƶ�
		mFloatView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub

				// getRawX�Ǵ���λ���������Ļ�����꣬getX������ڰ�ť������
				wmParams.x = (int) event.getRawX()
						- mFloatView.getMeasuredWidth() / 2;
				Log.i(TAG, "RawX" + event.getRawX());
				Log.i(TAG, "X" + event.getX());
				// ��25Ϊ״̬���ĸ߶�

				wmParams.y = (int) event.getRawY()
						- mFloatView.getMeasuredHeight() / 2 - 25;
				Log.i(TAG, "RawY" + event.getRawY());
				Log.i(TAG, "Y" + event.getY());
				// ˢ��
				mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				return false; // �˴����뷵��false������OnClickListener��ȡ��������
			}

		});

		mFloatView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub ���� �ж�����������Ƿ���ȷ����ȷ�Ļ���� ����
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
	 * ���ű�������
	 */
	private void playAlertMusic() {

		// ��ʼ��������
		mp.start();
		// ���ֲ�����ϵ��¼�����
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
//				 am=(AudioManager)AlertService.this.
//						 getSystemService(Context.AUDIO_SERVICE);
//				 // ��������
//				 am.setMode(AudioManager.MODE_NORMAL);
//				 //�����������
//				 am.setStreamVolume(AudioManager.STREAM_MUSIC,
//				 am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//				 //�������� �����
//				 am.adjustVolume(AudioManager.ADJUST_RAISE,0);
				 
				// ѭ������
				try {
					mp.start();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// ��������ʱ����������¼�����
		mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				// �ͷ���Դ
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
			// �Ƴ���������
			mWindowManager.removeView(mFloatLayout);
		}
		if (mp != null && mp.isPlaying()) {
			mp.stop();
			mp.release();
		}

	}
}
