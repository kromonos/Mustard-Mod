/*
 * MUSTARD: Android's Client for StatusNet
 * 
 * Copyright (C) 2009-2010 macno.org, Michele Azzolari
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.mumod.android.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class MustardStatusTextView extends TextView {

	private URLSpan mClickedLink;
	private ForegroundColorSpan mClickedColorStyle = new ForegroundColorSpan(Color.CYAN);

	public MustardStatusTextView(Context context) {
		super(context);
		setLinksClickable(false);
		setLinkTextColor(getTextColors().getDefaultColor());
	}

	public MustardStatusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLinksClickable(false);
		setLinkTextColor(getTextColors().getDefaultColor());
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {


		CharSequence text = getText();
		int action = event.getAction();

		if (!(text instanceof Spannable)) {
			return super.onTouchEvent(event);
		}

		Spannable buffer = (Spannable) text;

		if (action == MotionEvent.ACTION_UP || 
				action == MotionEvent.ACTION_DOWN ||
				action == MotionEvent.ACTION_MOVE) {
			TextView widget = this;

			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= getTotalPaddingLeft();
			y -= getTotalPaddingTop();

			x += getScrollX();
			y += getScrollY();


			int line = getLayout().getLineForVertical(y);
			int off = getLayout().getOffsetForHorizontal(line, x);

			URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);

			if (link.length != 0) {
				if (action == MotionEvent.ACTION_UP) {
					if (mClickedLink == link[0]) {
						link[0].onClick(widget);
					}
					mClickedLink = null;
					buffer.removeSpan(mClickedColorStyle);
				} else if (action == MotionEvent.ACTION_DOWN) {
					mClickedLink = link[0];
					buffer.setSpan(mClickedColorStyle, buffer.getSpanStart(link[0]), buffer
							.getSpanEnd(link[0]), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				return true;
			}
		}

		mClickedLink = null;
		buffer.removeSpan(mClickedColorStyle);

		return super.onTouchEvent(event);
	}

}
