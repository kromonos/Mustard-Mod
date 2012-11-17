package org.mumod.uploader;

import org.mumod.util.MustardException;

import android.net.Uri;

public interface MediaUploader {

	public long upload(Uri uri) throws MustardException;
}
