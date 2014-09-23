package com.example.broadcast;

import java.io.IOException;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.securitysystem.loginActivity;
import com.example.securitysystem.LocaltionActivity.MyLocationListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SystemBroadCast extends BroadcastReceiver {

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private String[] orderStr = new String[] { "baojin", "shanchu", "dinwei",
			"shuopin", "beifen", "shanchuruanjian" };

	// 安全密码
	private String passStr = null;
	private SharedPreferences spf, saveSpf;
	private Boolean isStartService = false;

	// 这个用来标记是否要发送短信 通知SIM 卡被更换了
	private boolean isToSendMsm = true;
	private boolean isListenSIM = false;
	private boolean isAlert=false;

	private String simSerial, safeNumber;

	private SmsManager Sendsms;

	private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

	// 位置信息
	private LocationManager locationManager;
	private GpsStatus gpsstatus;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.v("sms", "receivBroad" + arg1.getAction());
		Sendsms = SmsManager.getDefault();

		spf = arg0.getSharedPreferences("password.xml", Context.MODE_PRIVATE);
		saveSpf = arg0.getSharedPreferences("saveNumber", Context.MODE_PRIVATE);

		safeNumber = saveSpf.getString("safeNumber", "0");
		passStr = spf.getString("password", "");
		isListenSIM = spf.getBoolean("isListenSIM", false);
		isAlert=spf.getBoolean("isAlert",false);
		// 从XML 文件中获取是否开启了手机保护。
		isStartService = spf.getBoolean("isStartService", false);

		Log.v("sms", "isStartService:" + isStartService);
		if (isStartService) {// 判断服务是否开启
			if (arg1.getAction().equals(
					"android.provider.Telephony.SMS_RECEIVED")) {
				// 发送短信
				receivedMSG(arg0, arg1);
				
			} else if (arg1.getAction().equals(ACTION_SIM_STATE_CHANGED)
					&& isListenSIM) { // SIM
				SIMListen(arg0);
			}else if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
				// 监听开机广播  判断 警报是否解除 如果没解除 继续报警
				if(isAlert){
					//警报未解除， 继续 报警
					arg0.startService(new Intent(arg0, AlertService.class));
				}
				
			}
		}
	}
	
	private void SIMListen(Context arg0){
		// 卡更换的监听
		Log.v("SIM_stat", "COEM THE ACTION_SIM_STATE_CHANGED");
		TelephonyManager tm = (TelephonyManager) arg0
				.getSystemService(Service.TELEPHONY_SERVICE);
		int state = tm.getSimState();
		simSerial = spf.getString("simSerial", "0");

		switch (state) {
		case TelephonyManager.SIM_STATE_READY: // sim 卡准备完毕

			simIsReady(tm, arg0);
			
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			Log.v("SIM_stat", state + "==SIM_STATE_UNKNOWN");
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			Log.v("SIM_stat", state + "==SIM_STATE_ABSENT");
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			Log.v("SIM_stat", state + "==SIM_STATE_PIN_REQUIRED");
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			Log.v("SIM_stat", state + "==SIM_STATE_PUK_REQUIRED");
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			Log.v("SIM_stat", state + "==SIM_STATE_NETWORK_LOCKED");
			break;
		default:
			Log.v("SIM_stat", state + "default");
			break;
		}
	}

	/***
	 * SIM 卡准备完毕 开始匹配当前SIM 卡和 旧SIM 卡是否同一张
	 */
	private void simIsReady(TelephonyManager tm, Context arg0) {
		String nowSimSerial = tm.getSimSerialNumber();

		System.out.println(safeNumber);
		System.out.println(simSerial + "--" + nowSimSerial);//

		// 从XML 文件中获取是否已经发送过换卡通知短信 没有的话就说明没发过 所以就设为true
		isToSendMsm = spf.getBoolean("isToSendMsm", true);

		// 如果是自己的SIM 卡 者变回可发 SIM 卡改变短信
		if (("" + simSerial).equals(nowSimSerial + "")) {
			isToSendMsm = true;
		}

		if (!(nowSimSerial + "").equals("null") && !safeNumber.equals("0")
				&& !simSerial.equals("0") && !nowSimSerial.equals(simSerial)) {// 新旧SIM卡不同
			// SIm 卡换了 发送短信到 安全手机 提示 手机SIM 卡被更换了
			if (isToSendMsm) {
				
				Sendsms.sendTextMessage(safeNumber, null,
						"我的手机在启动安全模式下被更换了SIM卡了，你可以发送指令协助我操作手机", null, null);
				arg0.startService(new Intent(arg0, AlertService.class));
				isToSendMsm = false;
				
			}
		}
		// 标急 换卡后已经发过一次了
		spf.edit().putBoolean("isToSendMsm", isToSendMsm).commit();
	}

	private void receivedMSG(Context arg0, Intent arg1) {
		// SmsManager sms = SmsManager.getDefault();
		Bundle bundle = arg1.getExtras();
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < pdus.length; i++)
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

			for (SmsMessage message : messages) {

				String msg = message.getMessageBody();
				String to = message.getOriginatingAddress();
				// sms.sendTextMessage(to, null, msg, null, null);//
				// 把受到的消息发回回去
				Log.e("sms", to + ":" + msg);
				statAction(arg0, to, msg);

			}
		}
	}

	/***
	 * 处理受到的短信指令，提取短信内容 内容分为 安全密码 和指令 匹配指令 做出处理 若没有密码或者密码不正确不做任何处理
	 * 
	 * @param to
	 *            发送指令的 手机号码
	 * @param msg
	 *            短信内容
	 */
	private void statAction(Context context, String to, String msg) {
		int index = -1;
		String[] oder = msg.split("\\*");
		// System.out.println(oder[0]+"--"+oder[1]);
		if(oder.length!=2){
			return;
		}
		if (oder[1].equals(passStr)) {// 判断安全密码是否正确
			for (int i = 0; i < orderStr.length; i++) { // 匹配指令
				if (oder[0].equals(orderStr[i])) {
					index = i;
				}
			}
			switch (index) {
			case 0: // 报警
				// 启动一个 Service 打开一个悬浮窗口 播放报警音
				context.startService(new Intent(context, AlertService.class));
				// 标记 进入 锁屏 报警状态
				spf.edit().putBoolean("isAlert", true).commit();
				Log.e("sms", "报警");
				break;
			case 1:// 删除
				Log.e("sms", "删除");
				deleContact(context);
				break;
			case 2:// 定位
				mLocationClient = new LocationClient(context.getApplicationContext()); // 声明LocationClient类
				mLocationClient.registerLocationListener(myListener); // 注册监听函数
				
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// 打开gps
				option.setCoorType("bd09ll"); // 设置坐标类型
				option.setIsNeedAddress(true);
				option.setScanSpan(1000);
				mLocationClient.setLocOption(option);
				mLocationClient.start();
				int result=mLocationClient.requestLocation();
				
				Log.e("sms", "定位"+result);
				//String address = getLocationAdress(context);
//				Sendsms.sendTextMessage(safeNumber, null,
//						"我的手机当前位置：" + "", null, null);
				
				break;
			case 3:// 锁屏
				
				Log.e("sms", "锁屏");
				break;
			case 4:// 删除保护软件

				Log.e("sms", "删除保护软件");
				break;
			default :
				
				break;
			}
		}
	}

	private String getLocationAdress(Context arg0) {
		String Address = "";
		// 获取到LocationManager对象
		locationManager = (LocationManager) arg0
				.getSystemService(arg0.LOCATION_SERVICE);
		// 创建一个Criteria对象
		Criteria criteria = new Criteria();
		// 设置粗略精确度
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
		// 设置是否需要返回海拔信息
		criteria.setAltitudeRequired(false);
		// 设置是否需要返回方位信息
		criteria.setBearingRequired(false);
		// 设置是否允许付费服务
		criteria.setCostAllowed(true);
		// 设置电量消耗等级
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		// 设置是否需要返回速度信息
		criteria.setSpeedRequired(false);
		// 根据设置的Criteria对象，获取最符合此标准的provider对象 41
		String currentProvider = locationManager
				.getBestProvider(criteria, true);
		
		Log.d("Location", "currentProvider: " + currentProvider);
		// 根据当前provider对象获取最后一次位置信息 44
		Location currentLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		Log.d("Location", "currentLocation1:" + currentLocation);

		// 如果位置信息为null，则请求更新位置信息 46
		if (currentLocation == null) {

			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							0, 0, locationListener);

			Log.d("Location", "currentLocation3:" + currentLocation);

		}
		Log.d("Location", "currentLocation4:" + currentLocation);

		// 直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度 50
		// 每隔10秒获取一次位置信息 51
		while (true) {
			locationManager
			.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					0, 0, locationListener);
			// currentLocation = locationManager
			// .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Log.d("Location", "while (true): " + currentLocation);

			if (currentLocation != null) {
				Log.d("Location", "Latitude: " + currentLocation.getLatitude());
				Log.d("Location", "location: " + currentLocation.getLongitude());
				break;
			} else {
				Log.d("Location", "Latitude: " + 0);
				Log.d("Location", "location: " + 0);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.e("Location", e.getMessage());
			}
		}
		
		// 解析地址并显示 69
		Geocoder geoCoder = new Geocoder(arg0);
		try {
			double latitude = currentLocation.getLatitude();
			double longitude = currentLocation.getLongitude();
			List<Address> list = geoCoder.getFromLocation(latitude, longitude,
					2);
			for (int i = 0; i < list.size(); i++) {
				Address address = list.get(i);

				for (int j = 0; i < address.getMaxAddressLineIndex(); i++) {
					Log.e("Location",
							"line:(" + j + ")" + address.getAddressLine(j));
					Address = address.getAddressLine(j);
					//builder.append(address.getAddressLine(i)).append("\n");
				}
				return Address;
			}
		} catch (IOException e) {
			// Toast.makeText(PositionActivity.this, e.getMessage(),
			// Toast.LENGTH_LONG).show();
		}
		return "获取失败";

	}

	// 创建位置监听器
	private LocationListener locationListener = new LocationListener() {
		// 位置发生改变时调用
		@Override
		public void onLocationChanged(Location location) {
			Log.d("Location", "onLocationChanged");
		}

		// provider失效时调用
		@Override
		public void onProviderDisabled(String provider) {
			Log.d("Location", "onProviderDisabled");
		}

		// provider启用时调用
		@Override
		public void onProviderEnabled(String provider) {
			Log.d("Location", "onProviderEnabled");
		}

		// 状态改变时调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("Location", "onStatusChanged");
		}
	};

	//1781088950
	/***
	 * 删除 联系人
	 * @param context
	 */
	private void deleContact(Context context){
		//Cursor curso=
		Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
		int count=context.getContentResolver().delete(uri, "_id!=-1", null);
		Log.v("count","---"+count+"--");
	}
	
public class MyLocationListener implements BDLocationListener {

		
		@Override
		public void onReceiveLocation(BDLocation location) {
			System.out.println("MSG-Location"); 
			
			if (location == null)
				return;
			
			double Nowlatitude=location.getLatitude();
			double NowlongItude=location.getLongitude();
			float  Nowradius=location.getRadius();
			
 	       Log.v("location",Nowlatitude+"---"+NowlongItude+"---"+Nowradius+"---"+location.getAddrStr());
			

		}
	}
	
}
