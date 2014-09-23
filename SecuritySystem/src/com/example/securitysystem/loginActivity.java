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
		System.out.println("SIM������:"+simSerial);
		
	}
	
	
	private void getPhoneSimStatus(){
		// 2>��ȡSIM��״̬
				int simStat=tpm.getSimState();
				System.out.println("SIM��״̬:"+simStat);
				// 3>��ȡSIM������
				String simSerial=tpm.getSimSerialNumber();
				System.out.println("SIM������:"+simSerial);
				// 4>��ȡSIM�������̺�
				String simOperato=tpm.getSimOperator();
				System.out.println("SIM�������̺�:"+simOperato);
				// 5>��ȡSIM������������
				String simOperatoName=tpm.getSimOperatorName();
				System.out.println("SIM������������:"+simOperatoName);
				// 6>��ȡSIM������
				String simCountryIso=tpm.getSimCountryIso();
				System.out.println("SIM������:"+simCountryIso);
				// 7>��ȡ�ֻ�����
				int phoneType=tpm.getPhoneType();
				System.out.println("�ֻ�����:"+phoneType);
				// 8>��ȡ��������
				int networkType=tpm.getNetworkType();
				System.out.println("��������:"+networkType);
				// 9>��ȡ���繩Ӧ�̺�
				String networkOperator=tpm.getNetworkOperator();
				System.out.println("���繩Ӧ�̺�:"+networkOperator);
				// 10>��ȡ���繩Ӧ������
				String NetworkOperatorName=tpm.getNetworkOperatorName();
				System.out.println("���繩Ӧ������:"+NetworkOperatorName);
				// 11>����ֻ�����
				String phoneNumber=tpm.getLine1Number();
				System.out.println("�ֻ�����:"+phoneNumber);
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
