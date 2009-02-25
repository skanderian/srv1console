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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SRV1Settings extends Activity {
	public static final String SRV1_SETTINGS = "SRV1_SETTINGS";
	public static final String DEFAULT_SERVER = "default_server";

	public static final String MOTION__CONTROL_MODE = "motion_control_mode";
	public static final int MOTION_CONTROL_SIMPLE = 0;
	public static final int MOTION_CONTROL_ADVANCED = 1;
	public static final int MOTION_CONTROL_DEFAULT = MOTION_CONTROL_SIMPLE;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.srv1settings);

		setLastSavedPreferences();
		
		Button revertButton = (Button) findViewById(R.id.revert_button);
		revertButton.setOnClickListener(revertButtonListener);

		Button saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(saveButtonListener);
	}

	private void setLastSavedPreferences()
	{
		SharedPreferences settings = getSharedPreferences(
				SRV1Settings.SRV1_SETTINGS, 0);
		
		EditText server_field = (EditText) findViewById(R.id.server_field);
		server_field.setText(settings.getString(SRV1Settings.DEFAULT_SERVER,
				getString(R.string.default_server)));

		CheckBox advancedControlCheckbox = (CheckBox) findViewById(R.id.advanced_control_checkbox);
		advancedControlCheckbox
				.setChecked(settings.getInt(MOTION__CONTROL_MODE,
						MOTION_CONTROL_DEFAULT) == MOTION_CONTROL_ADVANCED ? true
						: false);
	}
	private OnClickListener revertButtonListener = new OnClickListener() {

		public void onClick(View view) {
			setLastSavedPreferences();
		}

	};

	private OnClickListener saveButtonListener = new OnClickListener() {

		public void onClick(View view) {
			SharedPreferences settings = getSharedPreferences(
					SRV1Settings.SRV1_SETTINGS, 0);
			SharedPreferences.Editor editor = settings.edit();
			
			EditText server_field = (EditText) findViewById(R.id.server_field);
			editor.putString(DEFAULT_SERVER, server_field
					.getText().toString());

			CheckBox advancedControlCheckbox = (CheckBox) findViewById(R.id.advanced_control_checkbox);
			editor.putInt(MOTION__CONTROL_MODE, advancedControlCheckbox.isChecked() ? MOTION_CONTROL_ADVANCED : MOTION_CONTROL_DEFAULT);

			editor.commit();
			finish();
		}

	};

}
