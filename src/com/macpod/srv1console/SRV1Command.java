package com.macpod.srv1console;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

abstract public class SRV1Command {

	abstract public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception;

	abstract public boolean repeatOnFail();

	abstract public boolean repeat();

	protected int clearInputStream(DataInputStream in) throws Exception {
		int avail_count;
		int total_skip_count = 0;

		do {
			avail_count = in.available();
			total_skip_count += in.skip(avail_count);
		} while (avail_count > 0);
		if (total_skip_count > 0)
			Log.d("SRV1", "Cleared " + total_skip_count + "bytes.");
		return total_skip_count;
	}

}
