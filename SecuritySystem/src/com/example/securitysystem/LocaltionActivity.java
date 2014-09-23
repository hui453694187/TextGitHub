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
	// 是否是 卫星地图
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
		// 普通地图
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		switch_mapBut = (Button) this.findViewById(R.id.switch_mapBut);

		mLocationClient = new LocationClient(this.getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		
		// 开启定位图层
		baiduMap.setMyLocationEnabled(true);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		

		even();

	}

	private void even() {
		switch_mapBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 如果不是 卫星地图
				if (!isSatelliteMap) {
					// 卫星地图
					baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					isSatelliteMap = true;
				} else {
					// 普通地图
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
			
			//位置发生变化 记录下新的数据
			if(Nowlatitude!=latitude||NowlongItude!=longItude||Nowradius!=radius){
				latitude=Nowlatitude;
				longItude=NowlongItude;
				radius=Nowradius;
				isWentTo=true;// 要更新地图了
				
				locData = new MyLocationData.Builder()
				.accuracy(radius)
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(latitude)
				.longitude(longItude).build();
				// 设置定位数据 
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
