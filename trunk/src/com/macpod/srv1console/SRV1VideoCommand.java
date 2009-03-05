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
import android.util.Log;

public class SRV1VideoCommand extends SRV1Command {
	private static final int JPEG_HEADER_COUNT = 6;
	private static final char JPEG_HEADER_1 = '#';
	private static final char JPEG_HEADER_2 = '#';
	private static final char JPEG_HEADER_3 = 'I';
	private static final char JPEG_HEADER_4 = 'M';
	private static final char JPEG_HEADER_5 = 'J';
	private static final char JPEG_HEADER_6 = '5';

	private static final int IMAGE_SIZE_BYTE_COUNT = 4;

	private boolean high_priority;
	private int dataSize = 16000;
	private byte[] data = new byte[dataSize];
	private int tempSpaceSize = 16000;
	private byte[] storage = new byte[tempSpaceSize];
	private BitmapFactory.Options factory_options = new BitmapFactory.Options();
	private SRV1VideoView video;

	public SRV1VideoCommand(SRV1VideoView video, boolean high_priority) {
		this.video = video;
		this.high_priority = high_priority;
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

		if (imageSize > dataSize) { // Bump up the size of our buffers if needed
			Log.d(SRV1Utils.TAG, "More video space has been requested.");
			byte[] tempData = new byte[imageSize];
			byte[] tempStorage = new byte[imageSize];

			if (tempData == null || tempStorage == null) {
				// Assume size was bogus (and that we didn't run out of memory)
				return false;
			}
			data = tempData;
			storage = tempStorage;
		}

		// Read the image data
		readData(in, imageSize);

		// Display image.
		video.putFrame(BitmapFactory.decodeByteArray(data, 0, imageSize,
				factory_options));

		return true;
	}

	public boolean repeat() {
		return true;
	}

	public boolean repeatOnFail() {
		return high_priority;
	}

	private boolean goodHeader(DataInputStream in) throws Exception {
		if (in.available() >= JPEG_HEADER_COUNT
				&& (char) in.readByte() == JPEG_HEADER_1
				&& (char) in.readByte() == JPEG_HEADER_2
				&& (char) in.readByte() == JPEG_HEADER_3
				&& (char) in.readByte() == JPEG_HEADER_4
				&& (char) in.readByte() == JPEG_HEADER_5
				&& (char) in.readByte() == JPEG_HEADER_6)
			return true;
		return false;
	}

	private int readImageSize(DataInputStream in) throws Exception {
		// Make sure we can read in the image size, then read it.
		if (in.available() < IMAGE_SIZE_BYTE_COUNT)
			return 0;
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
