package com.macpod.srv1console;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

public class SRV1StopCommand extends SRV1Command {

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {
		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write a byte to stop the robot
		out.writeByte('l');

		// Verify robot stopped.
		if ((char)in.readByte() == '#' && (char)in.readByte() == 'l') {
			Log.d("SRV1", "Stopped!");
			return true;
		}
		Log.d("SRV1", "Could not stop!");
		return false;
		}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}
