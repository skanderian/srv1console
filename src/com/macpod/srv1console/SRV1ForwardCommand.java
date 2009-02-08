package com.macpod.srv1console;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

public class SRV1ForwardCommand extends SRV1Command {

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {
		
		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write a byte to go forwards
		out.writeByte('8');

		// Verify that robot went forward.
		if ((char)in.readByte() == '#' && (char)in.readByte() == '8') {
			Log.d("SRV1", "Robot went forwards!");
			return true;
		}
		Log.d("SRV1", "Did not go forwards!");
		return false;
	}

	public boolean repeat() {
		return false;
	}

	public boolean repeatOnFail() {
		return true;
	}

}
