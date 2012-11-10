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

package org.mustard.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

public class GimmeMoreListView extends ListView implements ListView.OnScrollListener {

	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private int mFirstVisibleItem;
	private OnNeedMoreListener mOnNeedMoreListener;



	public GimmeMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		boolean result = super.onInterceptTouchEvent(event);

		if (mScrollState == OnScrollListener.SCROLL_STATE_FLING) {
			return true;
		}

		return result;
	}

	public static interface OnNeedMoreListener {
		public void needMore();
	}

	public void setOnNeedMoreListener(OnNeedMoreListener onNeedMoreListener) {
		mOnNeedMoreListener = onNeedMoreListener;
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mOnNeedMoreListener == null) {
			return;
		}

		if (firstVisibleItem != mFirstVisibleItem) {
			if (firstVisibleItem + visibleItemCount >= totalItemCount) {
				mOnNeedMoreListener.needMore();
			}
		} else {
			mFirstVisibleItem = firstVisibleItem;
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mScrollState = scrollState;
	}

}
