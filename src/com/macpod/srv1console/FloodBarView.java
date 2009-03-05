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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class FloodBarView extends View {
	public static final int MIN_PERCENT = 0;
	public static final int MAX_PERCENT = 100;
	private int percent = MIN_PERCENT;

	public FloodBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPercent(int percent) {
		if (percent < MIN_PERCENT)
			this.percent = MIN_PERCENT;
		else if (percent > MAX_PERCENT)
			this.percent = MAX_PERCENT;
		else
			this.percent = percent;

		this.invalidate();
	}

	protected void onDraw(Canvas canvas) {
		int red = SRV1Utils.map(percent, MIN_PERCENT, MAX_PERCENT, 0, 255);
		canvas.drawRGB(red, 0, 0);
	}
}
