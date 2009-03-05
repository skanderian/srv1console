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

public class SRV1Servo1Command extends SRV1Command {
	public static final int MAX_PWM = 100; // 2ms pulse
	public static final int DEFAULT_PWM = 50; // 1.5ms pulse
	public static final int MIN_PWM = 0; // 1ms pulse

	private byte leftServo = DEFAULT_PWM;
	private byte rightServo = DEFAULT_PWM;

	public boolean setControls(int leftServo, int rightServo) {
		// If the values are bogus, set both servos to their default positions.
		if (leftServo < MIN_PWM || leftServo > MAX_PWM || rightServo < MIN_PWM
				|| rightServo > MAX_PWM) {
			leftServo = DEFAULT_PWM;
			rightServo = DEFAULT_PWM;
			return false;
		}

		this.leftServo = (byte) leftServo;
		this.rightServo = (byte) rightServo;

		return true;
	}

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {
		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write bytes to control the Servo.
		out.writeByte('S');

		// Write left Servo byte
		out.writeByte(leftServo);
		// Write right Servo byte
		out.writeByte(rightServo);

		// Verify unit recieved the request.
		if ((char) in.readByte() == '#' && (char) in.readByte() == 'S') {
			Log.d(SRV1Utils.TAG, "Controlled servo1 bank!");
			return true;
		}
		Log.d(SRV1Utils.TAG, "Could not control serv1 bank!");
		return false;

	}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}
