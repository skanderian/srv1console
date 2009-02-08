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

public class SRV1SetCaptureResolutionCommand extends SRV1Command {
	public enum CaptureResolution {
		RES160x120, RES320x240, RES640x480, RES1280x1024
	}

	char resolution;

	public SRV1SetCaptureResolutionCommand(CaptureResolution res) {
		switch (res) {
		case RES160x120:
			resolution = 'a';
			break;
		case RES320x240:
			resolution = 'b';
			break;
		case RES640x480:
			resolution = 'c';
			break;
		case RES1280x1024:
			resolution = 'A';
			break;
		}
	}

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {

		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write a byte to set the resolution
		out.writeByte('b');

		// Verify resolution was set.
		if ((char) in.readByte() == '#' && (char) in.readByte() == resolution) {
			Log.d("SRV1", "Set resolution OK!");
			return true;
		}
		Log.d("SRV1", "Could not setup resolution!");
		return false;
	}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}
