package com.example.securitysystem;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Found_passActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_found_pass);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.found_pass, menu);
		return true;
	}

}
