package com.example.securitysystem;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SIMChangeNotifiActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simchange_notifi);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simchange_notifi, menu);
		return true;
	}

}
