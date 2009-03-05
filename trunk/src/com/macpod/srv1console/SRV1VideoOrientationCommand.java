package com.macpod.srv1console;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

public class SRV1VideoOrientationCommand extends SRV1Command {
	public static enum VideoOrientation {
		NORMAL_ORIENTATION, FLIPPED_ORIENTATION
	}

	char orientation = 'Y';

	public SRV1VideoOrientationCommand(VideoOrientation orientation) {

		switch (orientation) {
		case NORMAL_ORIENTATION:
			this.orientation = 'Y';
			break;
		case FLIPPED_ORIENTATION:
			this.orientation = 'y';
			break;
		}
	}

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {

		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Write a byte to orient video capture.
		out.writeByte(orientation);

		// Verify video capture orientation was set.
		if ((char) in.readByte() == '#' && (char) in.readByte() == orientation) {
			Log.d(SRV1Utils.TAG, "Set video orientation!");
			return true;
		}
		Log.d(SRV1Utils.TAG, "Could not set video orientation!");
		return false;
	}

	public boolean repeat() {
		return false;
	}

	@Override
	public boolean repeatOnFail() {
		return true;
	}

}
