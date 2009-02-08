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
