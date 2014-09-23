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

	// ��ȫ����
	private String passStr = null;
	private SharedPreferences spf, saveSpf;
	private Boolean isStartService = false;

	// �����������Ƿ�Ҫ���Ͷ��� ֪ͨSIM ����������
	private boolean isToSendMsm = true;
	private boolean isListenSIM = false;
	private boolean isAlert=false;

	private String simSerial, safeNumber;

	private SmsManager Sendsms;

	private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

	// λ����Ϣ
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
		// ��XML �ļ��л�ȡ�Ƿ������ֻ�������
		isStartService = spf.getBoolean("isStartService", false);

		Log.v("sms", "isStartService:" + isStartService);
		if (isStartService) {// �жϷ����Ƿ���
			if (arg1.getAction().equals(
					"android.provider.Telephony.SMS_RECEIVED")) {
				// ���Ͷ���
				receivedMSG(arg0, arg1);
				
			} else if (arg1.getAction().equals(ACTION_SIM_STATE_CHANGED)
					&& isListenSIM) { // SIM
				SIMListen(arg0);
			}else if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
				// ���������㲥  �ж� �����Ƿ��� ���û��� ��������
				if(isAlert){
					//����δ����� ���� ����
					arg0.startService(new Intent(arg0, AlertService.class));
				}
				
			}
		}
	}
	
	private void SIMListen(Context arg0){
		// �������ļ���
		Log.v("SIM_stat", "COEM THE ACTION_SIM_STATE_CHANGED");
		TelephonyManager tm = (TelephonyManager) arg0
				.getSystemService(Service.TELEPHONY_SERVICE);
		int state = tm.getSimState();
		simSerial = spf.getString("simSerial", "0");

		switch (state) {
		case TelephonyManager.SIM_STATE_READY: // sim ��׼�����

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
	 * SIM ��׼����� ��ʼƥ�䵱ǰSIM ���� ��SIM ���Ƿ�ͬһ��
	 */
	private void simIsReady(TelephonyManager tm, Context arg0) {
		String nowSimSerial = tm.getSimSerialNumber();

		System.out.println(safeNumber);
		System.out.println(simSerial + "--" + nowSimSerial);//

		// ��XML �ļ��л�ȡ�Ƿ��Ѿ����͹�����֪ͨ���� û�еĻ���˵��û���� ���Ծ���Ϊtrue
		isToSendMsm = spf.getBoolean("isToSendMsm", true);

		// ������Լ���SIM �� �߱�ؿɷ� SIM ���ı����
		if (("" + simSerial).equals(nowSimSerial + "")) {
			isToSendMsm = true;
		}

		if (!(nowSimSerial + "").equals("null") && !safeNumber.equals("0")
				&& !simSerial.equals("0") && !nowSimSerial.equals(simSerial)) {// �¾�SIM����ͬ
			// SIm ������ ���Ͷ��ŵ� ��ȫ�ֻ� ��ʾ �ֻ�SIM ����������
			if (isToSendMsm) {
				
				Sendsms.sendTextMessage(safeNumber, null,
						"�ҵ��ֻ���������ȫģʽ�±�������SIM���ˣ�����Է���ָ��Э���Ҳ����ֻ�", null, null);
				arg0.startService(new Intent(arg0, AlertService.class));
				isToSendMsm = false;
				
			}
		}
		// �꼱 �������Ѿ�����һ����
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
				// ���ܵ�����Ϣ���ػ�ȥ
				Log.e("sms", to + ":" + msg);
				statAction(arg0, to, msg);

			}
		}
	}

	/***
	 * �����ܵ��Ķ���ָ���ȡ�������� ���ݷ�Ϊ ��ȫ���� ��ָ�� ƥ��ָ�� �������� ��û������������벻��ȷ�����κδ���
	 * 
	 * @param to
	 *            ����ָ��� �ֻ�����
	 * @param msg
	 *            ��������
	 */
	private void statAction(Context context, String to, String msg) {
		int index = -1;
		String[] oder = msg.split("\\*");
		// System.out.println(oder[0]+"--"+oder[1]);
		if(oder.length!=2){
			return;
		}
		if (oder[1].equals(passStr)) {// �жϰ�ȫ�����Ƿ���ȷ
			for (int i = 0; i < orderStr.length; i++) { // ƥ��ָ��
				if (oder[0].equals(orderStr[i])) {
					index = i;
				}
			}
			switch (index) {
			case 0: // ����
				// ����һ�� Service ��һ���������� ���ű�����
				context.startService(new Intent(context, AlertService.class));
				// ��� ���� ���� ����״̬
				spf.edit().putBoolean("isAlert", true).commit();
				Log.e("sms", "����");
				break;
			case 1:// ɾ��
				Log.e("sms", "ɾ��");
				deleContact(context);
				break;
			case 2:// ��λ
				mLocationClient = new LocationClient(context.getApplicationContext()); // ����LocationClient��
				mLocationClient.registerLocationListener(myListener); // ע���������
				
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// ��gps
				option.setCoorType("bd09ll"); // ������������
				option.setIsNeedAddress(true);
				option.setScanSpan(1000);
				mLocationClient.setLocOption(option);
				mLocationClient.start();
				int result=mLocationClient.requestLocation();
				
				Log.e("sms", "��λ"+result);
				//String address = getLocationAdress(context);
//				Sendsms.sendTextMessage(safeNumber, null,
//						"�ҵ��ֻ���ǰλ�ã�" + "", null, null);
				
				break;
			case 3:// ����
				
				Log.e("sms", "����");
				break;
			case 4:// ɾ���������

				Log.e("sms", "ɾ���������");
				break;
			default :
				
				break;
			}
		}
	}

	private String getLocationAdress(Context arg0) {
		String Address = "";
		// ��ȡ��LocationManager����
		locationManager = (LocationManager) arg0
				.getSystemService(arg0.LOCATION_SERVICE);
		// ����һ��Criteria����
		Criteria criteria = new Criteria();
		// ���ô��Ծ�ȷ��
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
		// �����Ƿ���Ҫ���غ�����Ϣ
		criteria.setAltitudeRequired(false);
		// �����Ƿ���Ҫ���ط�λ��Ϣ
		criteria.setBearingRequired(false);
		// �����Ƿ������ѷ���
		criteria.setCostAllowed(true);
		// ���õ������ĵȼ�
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		// �����Ƿ���Ҫ�����ٶ���Ϣ
		criteria.setSpeedRequired(false);
		// �������õ�Criteria���󣬻�ȡ����ϴ˱�׼��provider���� 41
		String currentProvider = locationManager
				.getBestProvider(criteria, true);
		
		Log.d("Location", "currentProvider: " + currentProvider);
		// ���ݵ�ǰprovider�����ȡ���һ��λ����Ϣ 44
		Location currentLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		Log.d("Location", "currentLocation1:" + currentLocation);

		// ���λ����ϢΪnull�����������λ����Ϣ 46
		if (currentLocation == null) {

			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							0, 0, locationListener);

			Log.d("Location", "currentLocation3:" + currentLocation);

		}
		Log.d("Location", "currentLocation4:" + currentLocation);

		// ֱ��������һ��λ����ϢΪֹ�����δ������һ��λ����Ϣ������ʾĬ�Ͼ�γ�� 50
		// ÿ��10���ȡһ��λ����Ϣ 51
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
		
		// ������ַ����ʾ 69
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
		return "��ȡʧ��";

	}

	// ����λ�ü�����
	private LocationListener locationListener = new LocationListener() {
		// λ�÷����ı�ʱ����
		@Override
		public void onLocationChanged(Location location) {
			Log.d("Location", "onLocationChanged");
		}

		// providerʧЧʱ����
		@Override
		public void onProviderDisabled(String provider) {
			Log.d("Location", "onProviderDisabled");
		}

		// provider����ʱ����
		@Override
		public void onProviderEnabled(String provider) {
			Log.d("Location", "onProviderEnabled");
		}

		// ״̬�ı�ʱ����
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("Location", "onStatusChanged");
		}
	};

	//1781088950
	/***
	 * ɾ�� ��ϵ��
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
