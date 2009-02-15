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

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

public class SRV1DirectMotorControlCommand extends SRV1Command {
	public static final byte MAX_FORWARD_SPEED = 100;
	public static final byte MIN_FORWARD_SPEED = 1;
	public static final byte STOP_SPEED = 0;
	public static final byte MIN_REVERSE_SPEED = -1;
	public static final byte MAX_REVERSE_SPEED = -100;
	public static final int MIN_DURATION = 1; // = 1 * 10ms = 10ms
	public static final int MAX_DURATION = 255; // = 255 * 10ms = 2550ms
	public static final byte INFINITE_DURATION = 0;

	public static final byte DEFAULT_FORWARD_SPEED = 40;
	public static final byte DEFAULT_REVERSE_SPEED = -40;
	public static final byte DEFAULT_FORWARD_STRONG_DRIFT = 48;
	public static final byte DEFAULT_FORWARD_WEAK_DRIFT = 24;
	public static final byte DEFAULT_REVERSE_STRONG_DRIFT = -48;
	public static final byte DEFAULT_REVERSE_WEAK_DRIFT = -24;

	private byte leftMotor = STOP_SPEED;
	private byte rightMotor = STOP_SPEED;
	private int duration = INFINITE_DURATION;

	private byte boundDuration (int duration) {
		duration = duration/10;
		return  (byte) (duration > MAX_DURATION ? MAX_DURATION : duration < INFINITE_DURATION ? INFINITE_DURATION : duration);
	}
	
	private byte boundLeftMotor(int leftMotor) {
		return (byte) (leftMotor > MAX_FORWARD_SPEED ? MAX_FORWARD_SPEED : leftMotor < MAX_REVERSE_SPEED ? MAX_REVERSE_SPEED : leftMotor);
	}
	
	private byte boundRightMotor(int rightMotor) {
		return (byte) (rightMotor > MAX_FORWARD_SPEED ? MAX_FORWARD_SPEED : rightMotor < MAX_REVERSE_SPEED ? MAX_REVERSE_SPEED : rightMotor);
	}
	
	
	public boolean setControls(byte leftMotor, byte rightMotor, byte duration) {
		// Bound values if they are not appropriate.
		this.duration = boundDuration(duration);
		this.leftMotor = boundLeftMotor(leftMotor);
		this.rightMotor = boundRightMotor(rightMotor);
		return true;
	}

	public void setStop(int duration) {
		setControls(STOP_SPEED, STOP_SPEED, boundDuration(duration));
	}

	public void setRight(int duration) {
		setControls(DEFAULT_FORWARD_SPEED, DEFAULT_REVERSE_SPEED,
				boundDuration(duration));
	}

	public void setLeft(int duration) {
		setControls(DEFAULT_REVERSE_SPEED, DEFAULT_FORWARD_SPEED,
				boundDuration(duration));
	}

	public void setForward(int duration) {
		setControls(DEFAULT_FORWARD_SPEED, DEFAULT_FORWARD_SPEED,
				boundDuration(duration));
	}

	public void setForwardRightDrift(int duration) {
		setControls(DEFAULT_FORWARD_STRONG_DRIFT, DEFAULT_FORWARD_WEAK_DRIFT,
				INFINITE_DURATION);
	}

	public void setForwardLeftDrift(int duration) {
		setControls(DEFAULT_FORWARD_WEAK_DRIFT, DEFAULT_FORWARD_STRONG_DRIFT,
				boundDuration(duration));
	}

	public void setReverse(int duration) {
		setControls(DEFAULT_REVERSE_SPEED, DEFAULT_REVERSE_SPEED,
				boundDuration(duration));
	}

	public void setReverseRightDrift(int duration) {
		setControls(DEFAULT_REVERSE_STRONG_DRIFT, DEFAULT_REVERSE_WEAK_DRIFT,
				boundDuration(duration));
	}

	public void setReverseLeftDrift(int duration) {
		setControls(DEFAULT_FORWARD_WEAK_DRIFT, DEFAULT_REVERSE_STRONG_DRIFT, INFINITE_DURATION);
	}

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {
		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write bytes to control the motor.
		out.writeByte('M');

		// Write left motor byte
		out.writeByte(leftMotor);
		// Write right motor byte
		out.writeByte(rightMotor);
		// Write duration byte
		out.writeByte(duration);

		// Verify unit recieved the request.
		if ((char) in.readByte() == '#' && (char) in.readByte() == 'M') {
			Log.d("SRV1", "Controlled motors!");
			return true;
		}
		Log.d("SRV1", "Could not control motors!");
		return false;

	}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}