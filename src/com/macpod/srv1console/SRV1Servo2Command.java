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

public class SRV1Servo2Command extends SRV1Command {
	public static final int MAX_FORWARD_SPEED = 100;
	public static final int STOP_SPEED = 50;
	public static final int MAX_REVERSE_SPEED = 0;

	byte leftServo = STOP_SPEED;
	byte rightServo = STOP_SPEED;

	public SRV1Servo2Command(byte leftServo, byte rightServo) {
		this.leftServo = leftServo;
		this.rightServo = rightServo;
	}

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {
		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write bytes to control the Servo.
		out.writeByte('s');

		// Write left Servo byte
		out.writeByte(leftServo);
		// Write right Servo byte
		out.writeByte(rightServo);

		// Verify unit recieved the request.
		if ((char) in.readByte() == '#' && (char) in.readByte() == 's') {
			Log.d("SRV1", "Controlled servo2 bank!");
			return true;
		}
		Log.d("SRV1", "Could not control servo2 bank!");
		return false;

	}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}
