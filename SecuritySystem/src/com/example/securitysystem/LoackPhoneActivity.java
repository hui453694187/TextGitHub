package com.example.securitysystem;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LoackPhoneActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loack_phone);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loack_phone, menu);
		return true;
	}

}
