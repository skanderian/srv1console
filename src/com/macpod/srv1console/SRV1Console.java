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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.macpod.srv1console.SRV1SetCaptureResolutionCommand.CaptureResolution;

public class SRV1Console extends Activity {

	private SRV1Communicator communicator = null;
	private SRV1Video video = new SRV1Video();
	private Menu optionsMenu = null;
	private int motion_mode = SRV1Settings.MOTION_CONTROL_DEFAULT;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.srv1console);
		ImageView iv = (ImageView) findViewById(R.id.video_view);
		iv.setImageDrawable(video);

		communicator = new SRV1Communicator();

		Button laserOnButton = (Button) findViewById(R.id.laser_on_button);
		laserOnButton.setOnClickListener(laserOnButtonListener);

		Button laserOffButton = (Button) findViewById(R.id.laser_off_button);
		laserOffButton.setOnClickListener(laserOffButtonListener);

		Button s1_1Button = (Button) findViewById(R.id.s1_1_button);
		s1_1Button.setOnClickListener(s1_1ButtonListener);

		Button s1_2Button = (Button) findViewById(R.id.s1_2_button);
		s1_2Button.setOnClickListener(s1_2ButtonListener);

		Button s2_1Button = (Button) findViewById(R.id.s2_1_button);
		s2_1Button.setOnClickListener(s2_1ButtonListener);

		Button s2_2Button = (Button) findViewById(R.id.s2_2_button);
		s2_2Button.setOnClickListener(s2_2ButtonListener);

		updateInterface();
		loadSettings();
	}

	private void loadSettings() {
		SharedPreferences settings = getSharedPreferences(
				SRV1Settings.SRV1_SETTINGS, 0);

		motion_mode = settings.getInt(SRV1Settings.MOTION__CONTROL_MODE,
				SRV1Settings.MOTION_CONTROL_DEFAULT);
	}

	private OnClickListener laserOnButtonListener = new OnClickListener() {

		public void onClick(View view) {
			SRV1Command command = new SRV1LaserOnCommand();
			communicator.putCommand(command);
		}

	};

	private OnClickListener laserOffButtonListener = new OnClickListener() {

		public void onClick(View view) {
			SRV1Command command = new SRV1LaserOffCommand();
			communicator.putCommand(command);
		}
	};

	private OnClickListener s1_1ButtonListener = new OnClickListener() {

		public void onClick(View view) {
			SRV1Servo2Command command = new SRV1Servo2Command();
			command.setControls(SRV1Servo2Command.MIN_PWM,
					SRV1Servo2Command.MIN_PWM);
			communicator.putCommand(command);
		}
	};

	private OnClickListener s1_2ButtonListener = new OnClickListener() {

		public void onClick(View view) {
			SRV1Servo2Command command = new SRV1Servo2Command();
			command.setControls(SRV1Servo2Command.MAX_PWM,
					SRV1Servo2Command.MAX_PWM);
			communicator.putCommand(command);
		}
	};

	private OnClickListener s2_1ButtonListener = new OnClickListener() {

		public void onClick(View view) {
			// Do something
		}
	};

	private OnClickListener s2_2ButtonListener = new OnClickListener() {

		public void onClick(View view) {
			// Do something
		}
	};

	protected void onStop() {
		Log.d("SRV1", "Stopping");
		if (communicator != null)
			communicator.disconnect();
		updateInterface();
		super.onStop();
	}

	protected static final int UPDATE_INTERFACE = 0;
	protected static final int UPDATE_IMAGE = 2;

	private Handler interface_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_INTERFACE:
				updateInterface();
				break;
			case UPDATE_IMAGE:
				updateImage();
				break;
			}
		}
	};

	public static final int DIR_NONE = 0;
	public static final int DIR_FORWARD = 1;
	public static final int DIR_REVERSE = 2;
	public static final int DIR_RIGHT = 3;
	public static final int DIR_LEFT = 4;
	private SensorListener tilt = new SensorListener() {
		// private static final int Z_ORIENTATION = 0;
		private static final int Y_ORIENTATION = 1;
		private static final int X_ORIENTATION = 2;

		// int baseZ = Integer.MAX_VALUE;
		// Screen facing up is 0
		// tilting unit away from you while in landscape mode yields positive #s
		// tilting unit towards you while in landscape mode yields negative #s
		private int xBase = -25;

		// screen facing up is 0
		// tilting unit to your left while in landscape mode yields positive #s
		// tilting unit to your right while in landscape mode yields negative #s
		private int yBase = 0;

		// Store last measured tilt settings.
		private int xTilt = DIR_NONE;
		private int yTilt = DIR_NONE;
		private byte leftMotor = SRV1DirectMotorControlCommand.STOP_SPEED;
		private byte rightMotor = SRV1DirectMotorControlCommand.STOP_SPEED;

		private int xMinThreshold = 10; // Degrees
		private int yMinThreshold = 10;

		// Used for advanced motion control.
		private int xMaxThreshold = 40; // Degrees
		private int yMaxThreshold = 40;

		private int motorTolerance = 5;
		
		private int cheapSlowdown = 0;
		private int cheapSlowdownIncrement = 5;

		private int map(int x, int in_min, int in_max, int out_min, int out_max) {
			return (x - in_min) * (out_max - out_min) / (in_max - in_min)
					+ out_min;
		}

		public void onAccuracyChanged(int sensor, int accuracy) {
			Log.d("SRV1", "Sensor accuracty changed");
		}

		public void onSensorChanged(int sensor, float[] values) {
			int xTilt, yTilt;
			xTilt = (int) values[X_ORIENTATION];
			yTilt = (int) values[Y_ORIENTATION];

			if (motion_mode == SRV1Settings.MOTION_CONTROL_ADVANCED) {
				advancedReactionToTilt(xTilt, yTilt);
			} else {
				simplereactToTilt(xTilt, yTilt);
			}
		}

		public void advancedReactionToTilt(int xTilt, int yTilt) {
			//int xBorder, yBorder;
			int leftMotor = SRV1DirectMotorControlCommand.STOP_SPEED;
			int rightMotor = SRV1DirectMotorControlCommand.STOP_SPEED;

			byte temp;

			// Ignore some events... we should use a timer instead of doing this.
			if (cheapSlowdown > 0) {
				cheapSlowdown--;
				return;
			}
			cheapSlowdown = cheapSlowdownIncrement;
			
			if (xTilt > xBase + xMinThreshold) {
				//xBorder = DIR_FORWARD;
				// Bound xTilt value.
				xTilt = xTilt > xBase + xMaxThreshold ? xBase + xMaxThreshold
						: xTilt;
				leftMotor = rightMotor = (byte) map(xTilt - xBase,
						xMinThreshold, xMaxThreshold,
						SRV1DirectMotorControlCommand.MIN_FORWARD_SPEED,
						SRV1DirectMotorControlCommand.MAX_FORWARD_SPEED);
			} else if (xTilt < xBase - xMinThreshold) {
				//xBorder = DIR_REVERSE;
				xTilt = xTilt < xBase - xMaxThreshold ? xBase - xMaxThreshold
						: xTilt;
				leftMotor = rightMotor = (byte) map(xTilt - xBase, -1
						* xMaxThreshold, -1 * xMinThreshold,
						SRV1DirectMotorControlCommand.MAX_REVERSE_SPEED,
						SRV1DirectMotorControlCommand.MIN_REVERSE_SPEED);
			} else {
				//xBorder = DIR_NONE;
				leftMotor = SRV1DirectMotorControlCommand.STOP_SPEED;
			}

			if (yTilt > yBase + yMinThreshold) {
				//yBorder = DIR_LEFT;
				yTilt = yTilt > yBase + yMaxThreshold ? yBase + yMaxThreshold
						: yTilt;
				temp = (byte) map(yTilt - yBase, yMinThreshold, yMaxThreshold,
						SRV1DirectMotorControlCommand.MIN_FORWARD_SPEED,
						SRV1DirectMotorControlCommand.MAX_FORWARD_SPEED);
				leftMotor -= temp;
				rightMotor += temp;

			} else if (yTilt < yBase - yMinThreshold) {
				//yBorder = DIR_RIGHT;
				yTilt = yTilt < yBase - yMaxThreshold ? yBase - yMaxThreshold
						: yTilt;
				temp = (byte) map(yTilt - yBase, -1 * yMaxThreshold, -1
						* yMinThreshold,
						SRV1DirectMotorControlCommand.MAX_REVERSE_SPEED,
						SRV1DirectMotorControlCommand.MIN_REVERSE_SPEED);

				leftMotor -= temp;
				rightMotor += temp;

			} else {
				//yBorder = DIR_NONE;
			}

			// Adjust values to stay in ranges.
			leftMotor = leftMotor > SRV1DirectMotorControlCommand.MAX_FORWARD_SPEED ? 
					SRV1DirectMotorControlCommand.MAX_FORWARD_SPEED
					: leftMotor < SRV1DirectMotorControlCommand.MAX_REVERSE_SPEED ? 
							SRV1DirectMotorControlCommand.MAX_REVERSE_SPEED
							: leftMotor;
			rightMotor = rightMotor > SRV1DirectMotorControlCommand.MAX_FORWARD_SPEED ? 
					SRV1DirectMotorControlCommand.MAX_FORWARD_SPEED
					: rightMotor < SRV1DirectMotorControlCommand.MAX_REVERSE_SPEED ? 
							SRV1DirectMotorControlCommand.MAX_REVERSE_SPEED
							: rightMotor;

			// Only continue if there is a significanct different sensor reading
			// than last time.
			if (this.leftMotor + motorTolerance >= leftMotor
					&& leftMotor >= this.leftMotor - motorTolerance
					&& this.rightMotor + motorTolerance >= rightMotor
					&& rightMotor >= this.rightMotor - motorTolerance)
				return;

			this.leftMotor = (byte)leftMotor;
			this.rightMotor = (byte)rightMotor;

			// Draw the tilt border.
			// drawTiltBorder(xBorder, yBorder); // commented out.. causing
			// crashes?

			// Send control to robot.
			SRV1DirectMotorControlCommand command = new SRV1DirectMotorControlCommand();
			command.setControls((byte)leftMotor, (byte)rightMotor,
					SRV1DirectMotorControlCommand.INFINITE_DURATION);
			communicator.putCommand(command);
			Log.d("SRV1", "LeftMotor: " + leftMotor + " RightMotor: "
					+ rightMotor);
		}

		public void simplereactToTilt(int xTilt, int yTilt) {
			if (xTilt > xBase + xMinThreshold)
				xTilt = DIR_FORWARD;
			else if (xTilt < xBase - xMinThreshold)
				xTilt = DIR_REVERSE;
			else
				xTilt = DIR_NONE;

			if (yTilt > yBase + yMinThreshold)
				yTilt = DIR_LEFT;
			else if (yTilt < yBase - yMinThreshold)
				yTilt = DIR_RIGHT;
			else
				yTilt = DIR_NONE;

			// Only continue if there is a different sensor reading than last
			// time.
			if (this.xTilt == xTilt && this.yTilt == yTilt)
				return;

			// Update the tracked tilt values so we don't run the same
			// command again unnecessarily.
			this.xTilt = xTilt;
			this.yTilt = yTilt;

			// Draw the tilt border.
			drawTiltBorder(xTilt, yTilt);

			// Control the motors according to what thresholds we are in.
			SRV1DirectMotorControlCommand command = new SRV1DirectMotorControlCommand();

			drawTiltBorder(xTilt, yTilt);
			if (xTilt == DIR_NONE && yTilt == DIR_RIGHT) {
				Log.d("SRV1", "Right");
				command.setRight();
			} else if (xTilt == DIR_NONE && yTilt == DIR_LEFT) {
				Log.d("SRV1", "Left");
				command.setLeft();
			} else if (xTilt == DIR_FORWARD && yTilt == DIR_NONE) {
				Log.d("SRV1", "Forward");
				command.setForward();
			} else if (xTilt == DIR_FORWARD && yTilt == DIR_RIGHT) {
				Log.d("SRV1", "Forward - right");
				command.setForwardRightDrift();
			} else if (xTilt == DIR_FORWARD && yTilt == DIR_LEFT) {
				Log.d("SRV1", "Forward - left");
				command.setForwardLeftDrift();
			} else if (xTilt == DIR_REVERSE && yTilt == DIR_NONE) {
				Log.d("SRV1", "Reverse");
				command.setReverse();
			} else if (xTilt == DIR_REVERSE && yTilt == DIR_RIGHT) {
				Log.d("SRV1", "Reverse - right");
				command.setReverseRightDrift();
			} else if (xTilt == DIR_REVERSE && yTilt == DIR_LEFT) {
				Log.d("SRV1", "Reverse -left");
				command.setReverseLeftDrift();
			} else { // Lets assume they are stopping.
				Log.d("SRV1", "Stopped");
				command.setStop();
			}

			communicator.putCommand(command);
		}

	};

	// Everything about how this border is made is awful.
	// It will be fixed when I have more time.
	private void drawTiltBorder(int xTilt, int yTilt) {

		ImageView left_bar = (ImageView) findViewById(R.id.left_bar);
		ImageView right_bar = (ImageView) findViewById(R.id.right_bar);
		ImageView top_bar = (ImageView) findViewById(R.id.top_bar);
		ImageView bottom_bar = (ImageView) findViewById(R.id.bottom_bar);

		Bitmap left_bar_bm = Bitmap.createBitmap(5, 480, Bitmap.Config.RGB_565);
		Bitmap right_bar_bm = Bitmap
				.createBitmap(5, 480, Bitmap.Config.RGB_565);
		Bitmap top_bar_bm = Bitmap.createBitmap(640, 5, Bitmap.Config.RGB_565);
		Bitmap bottom_bar_bm = Bitmap.createBitmap(640, 5,
				Bitmap.Config.RGB_565);

		left_bar_bm.eraseColor(yTilt == DIR_LEFT ? Color.RED : Color.BLACK);
		right_bar_bm.eraseColor(yTilt == DIR_RIGHT ? Color.RED : Color.BLACK);
		top_bar_bm.eraseColor(xTilt == DIR_FORWARD ? Color.RED : Color.BLACK);
		bottom_bar_bm
				.eraseColor(xTilt == DIR_REVERSE ? Color.RED : Color.BLACK);

		left_bar.setImageBitmap(left_bar_bm);
		right_bar.setImageBitmap(right_bar_bm);
		top_bar.setImageBitmap(top_bar_bm);
		bottom_bar.setImageBitmap(bottom_bar_bm);
	}

	public void updateInterface() {
		Menu menu = getOptionsMenu();
		if (menu == null)
			return;
		if (communicator == null || !communicator.connected()) {
			((SensorManager) getSystemService(SENSOR_SERVICE))
					.unregisterListener(tilt);
			Log.d("SRV1", "DISCONNECTED");
			menu.findItem(CONNECT_MENU_ITEM).setEnabled(true);
			menu.findItem(DISCONNECT_MENU_ITEM).setEnabled(false);
			drawTiltBorder(DIR_NONE, DIR_NONE);
		} else {
			Log.d("SRV1", "connected");
			menu.findItem(CONNECT_MENU_ITEM).setEnabled(false);
			menu.findItem(DISCONNECT_MENU_ITEM).setEnabled(true);
			((SensorManager) getSystemService(SENSOR_SERVICE))
					.registerListener(tilt, SensorManager.SENSOR_ORIENTATION,
							SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void updateImage() {
		// Log.d("SRV1", "Updating image!");
		try {
			ImageView iv = (ImageView) findViewById(R.id.video_view);
			iv.invalidate();
		} catch (Exception e) {
			// Ignore. Could be thrown because of a null bitmap or because
			// the
			// socket is dead.
		}
	}

	private void Dialogue(CharSequence message) {
		new AlertDialog.Builder(this).setMessage(message).setNeutralButton(
				"OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();
	}

	private static final int CONNECT_MENU_ITEM = 0;
	private static final int DISCONNECT_MENU_ITEM = 1;
	private static final int SETTINGS_MENU_ITEM = 2;

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		optionsMenu = menu;

		menu.add(0, CONNECT_MENU_ITEM, Menu.NONE, "Connect");
		menu.add(0, DISCONNECT_MENU_ITEM, Menu.NONE, "Disconnect");
		menu.add(0, SETTINGS_MENU_ITEM, Menu.NONE, "Settings");
		updateInterface();
		return true;
	}

	public static final int SRV1CONNECT_REQUEST_CODE = 0;
	public static final int SRV1SETTINGS_REQUEST_CODE = 1;

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONNECT_MENU_ITEM:
			Intent connect_intent = new Intent(this, SRV1Connect.class);
			startActivityForResult(connect_intent, SRV1CONNECT_REQUEST_CODE);
			return true;
		case DISCONNECT_MENU_ITEM:
			communicator.disconnect();
			return true;
		case SETTINGS_MENU_ITEM:
			Intent settings_intent = new Intent(this, SRV1Settings.class);
			startActivityForResult(settings_intent, SRV1SETTINGS_REQUEST_CODE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SRV1CONNECT_REQUEST_CODE && resultCode == RESULT_OK) {
			try {

				// Setup commands to initially send:
				BlockingQueue<SRV1Command> commandQueue = new LinkedBlockingQueue<SRV1Command>();
				commandQueue.put(new SRV1SetCaptureResolutionCommand(
						CaptureResolution.RES320x240));
				commandQueue
						.put(new SRV1VideoCommand(interface_handler, video));
				// Connect
				if (!communicator.connect(data.getStringExtra("server"),
						interface_handler, commandQueue)) {

					// Report a vague error for users to ponder over if we
					// can't connect.
					Dialogue("Could not connect to given server.");
				}
			} catch (Exception e) {
				Dialogue("An error occured while attempting to connect.");
			}
		} else if (requestCode == SRV1SETTINGS_REQUEST_CODE) {
			// Reload settings in case something changed.
			loadSettings();
		}
	}

	private Menu getOptionsMenu() {
		return optionsMenu;
	}
}