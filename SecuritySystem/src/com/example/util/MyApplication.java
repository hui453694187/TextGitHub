package com.example.util;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MyApplication extends Application {
	
	
	@Override
	public void onCreate() {
		SDKInitializer.initialize(this.getApplicationContext()); 
		super.onCreate();
		//Ӧ�ó�������ʱ������������
	}
	
}
