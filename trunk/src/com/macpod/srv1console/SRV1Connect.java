package com.macpod.srv1console;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SRV1Connect extends Activity {

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTheme(android.R.style.Theme_Dialog);
		setContentView(R.layout.srv1connect);
		
		EditText server_field = (EditText) findViewById(R.id.server_field);
		SharedPreferences settings = getSharedPreferences(SRV1Settings.SRV1_SETTINGS, 0);
		server_field.setText(settings.getString(SRV1Settings.DEFAULT_SERVER, getString(R.string.default_server)));
		

		Button connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(connectButtonListener);
	}
	
	private OnClickListener connectButtonListener = new OnClickListener() {

		public void onClick(View view) {
			EditText server = (EditText) findViewById(R.id.server_field);
			Intent data = new Intent();
			data.putExtra("server", server.getText().toString());
			setResult(Activity.RESULT_OK, data);
			finish();
		}

	};
}
