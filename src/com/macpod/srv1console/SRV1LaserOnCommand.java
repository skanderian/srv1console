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

public class SRV1LaserOnCommand extends SRV1Command {

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {

		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write a byte to turn the lasers on.
		out.writeByte('l');

		// Verify lasers were turned on.
		if ((char) in.readByte() == '#' && (char) in.readByte() == 'l') {
			Log.d("SRV1", "Turned on lasers!");
			return true;
		}
		Log.d("SRV1", "Could not on off lasers!");
		return false;
	}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}
