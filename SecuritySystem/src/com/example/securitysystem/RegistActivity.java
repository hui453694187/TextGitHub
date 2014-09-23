package com.example.securitysystem;

import com.example.broadcast.AlertService;

import controls.ClearEditText;
import android.os.Bundle;
import android.preference.Preference;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RegistActivity extends Activity {

	private ClearEditText passEdt, pass2Edt, questEdt, answerEdt;
	private Button registBut, cleanBut;

	private TelephonyManager tpm;
	
	private SharedPreferences shp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		init();

		even();
	}

	private void init() {
		shp=this.getSharedPreferences("password.xml",MODE_PRIVATE);
		
		
		passEdt = (ClearEditText) this.findViewById(R.id.passwork1_Edt);
		pass2Edt = (ClearEditText) this.findViewById(R.id.passwork2_Edt);
		questEdt = (ClearEditText) this.findViewById(R.id.question_Edt);
		answerEdt = (ClearEditText) this.findViewById(R.id.answer_Edt);

		registBut = (Button) this.findViewById(R.id.regist_But);
		cleanBut = (Button) this.findViewById(R.id.clean_But);
		
		tpm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//SIM 卡唯一标示
		String simSerial=tpm.getSimSerialNumber();
		shp.edit().putString("simSerial",simSerial).commit();

	}

	private void even() {
		//確定註冊
		registBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String pas1=passEdt.getText().toString();
				String pas2=pass2Edt.getText().toString();
				String que=questEdt.getText().toString();
				String ans=answerEdt.getText().toString();
				
				if(pas1.trim().length()>0&&pas2.trim().length()>0&&
				que.trim().length()>0&&ans.trim().length()>0){
					if(pas1.equals(pas2)){
						Editor edit=shp.edit();
						edit.putString("password", pas1);
						edit.putString("question",que);
						edit.putString("answer",ans);
						
						if(edit.commit()){
							Toast.makeText(RegistActivity.this, "註冊成功", Toast.LENGTH_LONG).show();
							RegistActivity.this.startActivity(new Intent().setClass(RegistActivity.this, loginActivity.class));
						}
						
					}else{
						Toast.makeText(RegistActivity.this, "兩次密碼不正確", Toast.LENGTH_LONG).show();
					}
					
				}else{
					Toast.makeText(RegistActivity.this, "以上信息不可以為空", Toast.LENGTH_LONG).show();
				}
				
				
				
				
			}
		});

		//清除
		cleanBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RegistActivity.this.startService(new Intent(RegistActivity.this,AlertService.class));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.regist, menu);
		return true;
	}

}
