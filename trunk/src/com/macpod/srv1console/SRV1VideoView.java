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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SRV1VideoView extends SurfaceView implements
		SurfaceHolder.Callback {

	private SurfaceHolder holder;
	boolean visible;

	public SRV1VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
		visible = false;
	}

	private Bitmap frame = null;

	public void putFrame(Bitmap frame) {
		Bitmap temp;
		if (frame == null)
			return;

		temp = this.frame;
		this.frame = frame;
		if (temp != null)
			temp.recycle();

		if (!visible)
			return;

		Canvas c = null;
		try {
			c = holder.lockCanvas(null);
			synchronized (holder) {
				onDraw(c);
			}
		} finally {
			// do this in a finally so that if an exception is thrown
			// during the above, we don't leave the Surface in an
			// inconsistent state
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}

	}

	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(frame, 0, 0, null);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		//Log.d(SRV1Utils.TAG, "Surface changed");
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		visible = true;
		//Log.d(SRV1Utils.TAG, "Surface is available");
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		visible = false;
		//Log.d(SRV1Utils.TAG, "Surface gone!");
	}

}
