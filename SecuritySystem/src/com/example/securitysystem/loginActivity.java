package com.example.securitysystem;

import controls.ClearEditText;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class loginActivity extends Activity {

	private Button loginBut, forgetPassBut;
	private ClearEditText passEdt;

	private String password;
	private SharedPreferences spf;
 
	private TelephonyManager tpm; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spf = this.getSharedPreferences("password.xml", MODE_PRIVATE);
		password = spf.getString("password", null);
		//
		if (password == null) {

			this.startActivity(new Intent()
					.setClass(this, RegistActivity.class));

		}
		setContentView(R.layout.activity_login);
		init();

		event();

	}

	private void init() {
		loginBut = (Button) this.findViewById(R.id.But_login);
		forgetPassBut = (Button) this.findViewById(R.id.forget_passBut);
		passEdt = (ClearEditText) this.findViewById(R.id.login_et_passwork);
		tpm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		String simSerial=tpm.getSimSerialNumber();
		System.out.println("SIM卡卡号:"+simSerial);
		
	}
	
	
	private void getPhoneSimStatus(){
		// 2>获取SIM卡状态
				int simStat=tpm.getSimState();
				System.out.println("SIM卡状态:"+simStat);
				// 3>获取SIM卡卡号
				String simSerial=tpm.getSimSerialNumber();
				System.out.println("SIM卡卡号:"+simSerial);
				// 4>获取SIM卡供货商号
				String simOperato=tpm.getSimOperator();
				System.out.println("SIM卡供货商号:"+simOperato);
				// 5>获取SIM卡供货商名称
				String simOperatoName=tpm.getSimOperatorName();
				System.out.println("SIM卡供货商名称:"+simOperatoName);
				// 6>获取SIM卡国别
				String simCountryIso=tpm.getSimCountryIso();
				System.out.println("SIM卡国别:"+simCountryIso);
				// 7>获取手机类型
				int phoneType=tpm.getPhoneType();
				System.out.println("手机类型:"+phoneType);
				// 8>获取网络类型
				int networkType=tpm.getNetworkType();
				System.out.println("网络类型:"+networkType);
				// 9>获取网络供应商号
				String networkOperator=tpm.getNetworkOperator();
				System.out.println("网络供应商号:"+networkOperator);
				// 10>获取网络供应商名称
				String NetworkOperatorName=tpm.getNetworkOperatorName();
				System.out.println("网络供应商名称:"+NetworkOperatorName);
				// 11>获得手机号码
				String phoneNumber=tpm.getLine1Number();
				System.out.println("手机号码:"+phoneNumber);
	}

	private void event() {

		loginBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// password
				if (password.equals(passEdt.getText().toString())) {
					
					loginActivity.this.startActivity(new Intent().setClass(
							loginActivity.this, MaineActivity.class));
					loginActivity.this.finish();
				}

			}
		});

		passEdt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
