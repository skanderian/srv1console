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

import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SRV1VideoCommand extends SRV1Command {
	private int dataSize = 16000;
	private byte[] data = new byte[dataSize];
	private int tempSpaceSize = 16000;
	private byte[] storage = new byte[tempSpaceSize];
	private BitmapFactory.Options factory_options = new BitmapFactory.Options();
	private SRV1Video video;
	private Handler interface_handler;

	public SRV1VideoCommand(Handler interface_handler, SRV1Video video) {
		this.interface_handler = interface_handler;
		this.video = video;
		factory_options.inTempStorage = storage;
		factory_options.inDither = false;
		factory_options.inPreferredConfig = Config.RGB_565;
	}

	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {

		// Clear input stream in case there is data present.
		clearInputStream(in);

		// Request an image frame.
		while (in.available() == 0) {
			out.writeByte('I');
		}

		if (!goodHeader(in))
			return false;

		int imageSize = readImageSize(in);

		if (imageSize <= 0) { // Make sure it is a sane image size.
			return false;
		}
		if (imageSize > dataSize) { // Increase the size of our buffers if it is
			// bigger.
			Log.d("SRV1", "More video space has been requested.");
			byte[] tempData = new byte[imageSize];
			byte[] tempStorage = new byte[imageSize];

			if (tempData == null || tempStorage == null) { // Assume the size
				// was bogus (and
				// that we
				// didn't run out of memory)
				clearInputStream(in);
				return false;
			}
			data = tempData;
			storage = tempStorage;
		}

		// Read the image data
		readData(in, imageSize);

		// Stick it in a bitmap.
		video.putFrame(BitmapFactory.decodeByteArray(data, 0, imageSize,
				factory_options));

		// Display the image
		try {
			Message m = Message.obtain();
			m.what = SRV1Console.UPDATE_IMAGE;
			interface_handler.sendMessage(m);
		} catch (Exception e) {
		}
		return true;
	}

	public boolean repeat() {
		return true;
	}

	public boolean repeatOnFail() {
		return false;
	}

	private boolean goodHeader(DataInputStream in) throws Exception {
		// Read in header
		if ((char) in.readByte() != '#' || (char) in.readByte() != '#'
				|| (char) in.readByte() != 'I' || (char) in.readByte() != 'M'
				|| (char) in.readByte() != 'J' || (char) in.readByte() != '5') {
			return false;
		}
		return true;
	}

	private int readImageSize(DataInputStream in) throws Exception {
		// Read the size of the image
		return 0 | in.readUnsignedByte() | in.readUnsignedByte() << 8
				| in.readUnsignedByte() << 16 | in.readUnsignedByte() << 24;
	}

	private void readData(DataInputStream in, int byteCount) throws Exception {
		int bytesRead = 0;
		int bytesToRead = byteCount;
		while (bytesRead < bytesToRead) {
			int result = in.read(data, bytesRead, bytesToRead - bytesRead);
			if (result == -1)
				break;
			bytesRead += result;
		}
	}
}
