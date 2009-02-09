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
	public static final int MAX_FORWARD_SPEED = 100;
	public static final int MIN_FORWARD_SPEED = 1;
	public static final int STOP_SPEED = 0;
	public static final int MIN_REVERSE_SPEED = -1;
	public static final int MAX_REVERSE_SPEED = -100;
	public static final int MIN_DURATION = 1;
	public static final int MAX_DURATION = 255;
	public static final int INFINITE_DURATION = 0;

	byte leftMotor = STOP_SPEED;
	byte rightMotor = STOP_SPEED;
	byte duration = INFINITE_DURATION;

	public SRV1DirectMotorControlCommand(byte leftMotor, byte rightMotor, byte duration) {
		this.duration = duration;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
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
