package org.mustard.uploader;

import org.mustard.util.MustardException;

import android.net.Uri;

public interface MediaUploader {

	public long upload(Uri uri) throws MustardException;
}
