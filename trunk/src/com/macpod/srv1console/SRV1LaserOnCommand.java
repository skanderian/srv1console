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
		if ((char)in.readByte() == '#' && (char)in.readByte() == 'l') {
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
