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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScaleBarView extends View {
	public static final int MIN_PERCENT = 0;
	public static final int MAX_PERCENT = 100;
	private int percent = 50;
	private int size = 4;
	private Paint paint = new Paint();
	private boolean disabled = true;
	
	public ScaleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setColor(Color.RED);
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

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
		this.invalidate();
	}

	public boolean disabled() {
		return disabled;
	}

	protected void onDraw(Canvas canvas) {
		// Get size of the view
		int width = getWidth();
		int height = getHeight();

		// Draw background color
		canvas.drawColor(Color.BLACK);

		// Only draw the background
		if (disabled)
			return;

		// Draw position according to what axis is longer.
		if (width > height) {
			int position = SRV1Utils.map(percent, MIN_PERCENT, MAX_PERCENT, 0, width);
			int left = position - size / 2;
			int right = position + size / 2;

			if (left < 0) {
				left = 0;
				right = size > width ? width : size;
			} else if (right > width) {
				left = width - size < 0 ? 0 : width - size;
				right = width;

			}
			canvas.drawRect(left, 0, right, height, paint);
		} else {
			int position = SRV1Utils.map(percent, MIN_PERCENT, MAX_PERCENT, 0, height);
			int top = position - size / 2;
			int bottom = position + size / 2;
			if (top < 0) {
				top = 0;
				bottom = size > height ? height : size;
			} else if (bottom > height) {
				top = top - size < 0 ? 0 : top - size;
				bottom = height;

			}
			canvas.drawRect(0, top, width, bottom, paint);
		}
	}

}
