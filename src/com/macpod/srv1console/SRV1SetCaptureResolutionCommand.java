package com.macpod.srv1console;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

public class SRV1SetCaptureResolutionCommand extends SRV1Command {
	public enum CaptureResolution {
		RES160x120 , RES320x240, RES640x480 , RES1280x1024
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
		if ((char)in.readByte() == '#' && (char)in.readByte() == resolution) {
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
