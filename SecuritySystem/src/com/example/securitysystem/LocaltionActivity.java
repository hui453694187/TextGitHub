package com.example.securitysystem;

import java.io.IOException;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationData.Builder;
import com.baidu.mapapi.model.LatLng;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LocaltionActivity extends Activity {

	private MapView mapView;
	private ImageView backImag;
	private BaiduMap baiduMap;
	private Button switch_mapBut;
	// �Ƿ��� ���ǵ�ͼ
	private boolean isSatelliteMap = false;
	private boolean isWentTo=true;
	double latitude,longItude;
	float radius;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public BitmapDescriptor mCurrentMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localtion);
		backImag=(ImageView)this.findViewById(R.id.back_localtionImage);
		mapView = (MapView) this.findViewById(R.id.baidu_map);
		baiduMap = mapView.getMap();
		// ��ͨ��ͼ
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		switch_mapBut = (Button) this.findViewById(R.id.switch_mapBut);

		mLocationClient = new LocationClient(this.getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		
		// ������λͼ��
		baiduMap.setMyLocationEnabled(true);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		

		even();

	}

	private void even() {
		switch_mapBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ������� ���ǵ�ͼ
				if (!isSatelliteMap) {
					// ���ǵ�ͼ
					baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					isSatelliteMap = true;
				} else {
					// ��ͨ��ͼ
					baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					isSatelliteMap = false;
				}

			}
		});

		backImag.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LocaltionActivity.this.finish();
				
			}
		});
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
			MyLocationData locData =null;
			
			//λ�÷����仯 ��¼���µ�����
			if(Nowlatitude!=latitude||NowlongItude!=longItude||Nowradius!=radius){
				latitude=Nowlatitude;
				longItude=NowlongItude;
				radius=Nowradius;
				isWentTo=true;// Ҫ���µ�ͼ��
				
				locData = new MyLocationData.Builder()
				.accuracy(radius)
				// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
				.direction(100).latitude(latitude)
				.longitude(longItude).build();
				// ���ö�λ���� 
	 	        baiduMap.setMyLocationData(locData); 
			}
			System.out.println(latitude+"--"+longItude+"--"+radius+"----"+location.getAddrStr());
			
			 
           
 	        
 	        if(isWentTo){
 	        	isWentTo=false;
 	        	
 	        	LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
 	        	
 	        }
			

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.localtion, menu);
		return true;
	}

	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mLocationClient.unRegisterLocationListener(myListener);
		mapView.onPause();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}

}
