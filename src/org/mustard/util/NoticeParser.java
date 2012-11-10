package org.mustard.util;

import org.mustard.android.activity.MustardGroup;
import org.mustard.android.activity.MustardTag;

import android.content.Context;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class NoticeParser {

	private Context mContext;
	private TextView content;
	private String text;
	
	public NoticeParser(Context context, TextView content, String text) {
		mContext=context;
		this.content=content;
		this.text=text;
	}
	
	public void parseNotice() {
		
		// required to make ClickableSpans clickable
		content.setMovementMethod(LinkMovementMethod.getInstance());
		content.setText(text, BufferType.SPANNABLE);

		Spannable spans = (Spannable) content.getText();
		
		
		ClickableSpan clickSpan = getGroupClickableSpan("");
		spans.setSpan(clickSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		clickSpan = getTagClickableSpan("");
	}
	
	private ClickableSpan getGroupClickableSpan(final String group) {
		ClickableSpan clickSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				MustardGroup.actionHandleTimeline(mContext, group);
			}
			
			public void updateDrawState(TextPaint ds) {
			}
		};
		return clickSpan;
	}
	
	private ClickableSpan getTagClickableSpan(final String tag) {
		ClickableSpan clickSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				MustardTag.actionHandleTimeline(mContext, tag);
			}
			
			public void updateDrawState(TextPaint ds) {
			}
		};
		return clickSpan;
	}
}
