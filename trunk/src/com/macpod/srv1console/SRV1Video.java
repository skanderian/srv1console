package com.macpod.srv1console;

import android.graphics.Bitmap;

public class SRV1Video {
	Bitmap frame = null;

	public SRV1Video() {
	}

	public void putFrame(Bitmap frame) {
		if (frame != null)
			this.frame = frame;
	}

	public Bitmap getFrame() {
		return frame;
	}
}
