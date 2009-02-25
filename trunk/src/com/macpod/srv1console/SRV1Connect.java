/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  srv1console - Android G1 phone console for Surveyor SRV-1 
 *    Copyright (C) 2005-2009  Surveyor Corporation and Jeffrey Nelson
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details (www.gnu.org/licenses)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.macpod.srv1console;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SRV1Connect extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTheme(android.R.style.Theme_Dialog);
		setContentView(R.layout.srv1connect);

		EditText server_field = (EditText) findViewById(R.id.server_field);
		SharedPreferences settings = getSharedPreferences(
				SRV1Settings.SRV1_SETTINGS, 0);
		server_field.setText(settings.getString(SRV1Settings.DEFAULT_SERVER,
				getString(R.string.default_server)));

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
