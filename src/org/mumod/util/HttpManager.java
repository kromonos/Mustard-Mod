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

package org.mumod.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mumod.android.MustardApplication;
import org.mumod.android.R;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class HttpManager {

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";

	private static final Integer DEFAULT_REQUEST_TIMEOUT = 30000;
	private static final Integer DEFAULT_POST_REQUEST_TIMEOUT = 40000;
	
	//private AuthScope mAuthScope;
	private DefaultHttpClient mClient;

	private String mHost;
	private CommonsHttpOAuthConsumer consumer ;
	private Context mContext;
	
	public HttpManager(Context context) {
		mContext=context;
		HttpParams params = getHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schemeRegistry.register(new Scheme("https", UntrustedSSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        mClient = new DefaultHttpClient(manager,params);
	}
	
	public HttpParams getHttpParams() {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");

        HttpConnectionParams.setStaleCheckingEnabled(params, true);
//        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_REQUEST_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 2*8192);

//        HttpClientParams.setRedirecting(params, true);

		HttpProtocolParams.setUserAgent(params, getUserAgent());
        HttpProtocolParams.setUseExpectContinue(params,false);
        
        return params;
        
	}
	
    private String getUserAgent() {
    	if(mContext != null) {
    		try {
    			// Read package name and version number from manifest
    			PackageManager manager = mContext.getPackageManager();
    			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
    			return String.format(mContext.getString(R.string.template_user_agent),
    					mContext.getString(R.string.app_name), info.versionName);

    		} catch(NameNotFoundException e) {
    			Log.e("Mustard", "Couldn't find package information in PackageManager", e);
    		}
    	}
        return "Mustard/1.0";
    }
    
	public HttpManager(Context context,String host) {
		mContext=context;
		HttpParams params = getHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schemeRegistry.register(new Scheme("https", UntrustedSSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        mClient = new DefaultHttpClient(manager,params);
		mHost=host;
	}

	public void setHost(String host) {
		mHost=host;
	}
	
	public void setCredentials(String username, String password) {

		Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
		String host=AuthScope.ANY_HOST;
		if (mHost!=null)
			host=mHost;
		BasicCredentialsProvider cP = new BasicCredentialsProvider(); 
		cP.setCredentials(new AuthScope(host, AuthScope.ANY_PORT, AuthScope.ANY_REALM), defaultcreds);
		mClient.setCredentialsProvider(cP);
		mClient.addRequestInterceptor(preemptiveAuth, 0);
		
	}

	public DefaultHttpClient getHttpClient() {
		return mClient;
	}
	
	public void setOAuthConsumer(CommonsHttpOAuthConsumer consumer ) {
		this.consumer=consumer;
	}
	
	public CommonsHttpOAuthConsumer getOAuthConsumer() {
		return consumer;
	}
	
	public JSONObject getJsonObject(String url) throws IOException,MustardException,AuthException {
		return getJsonObject(url,GET,null);
	}
	
	public JSONObject getJsonObject(String url, String httpMethod) throws IOException,MustardException,AuthException {
		return getJsonObject(url,httpMethod,null);
	}
	
	public JSONObject getJsonObject(String url, String httpMethod,
			ArrayList<NameValuePair> params) throws IOException,MustardException,AuthException {
		JSONObject json = null;
		try {
			json = new JSONObject(StreamUtil.toString(requestData(url,httpMethod,params)));
		} catch (JSONException e) {
			throw new MustardException(998,"Non json response: " + e.toString());
		}
		return json;
	}
	
	public JSONObject getJsonObject(String url, ArrayList<NameValuePair> params,String attachmentParam, File attachment) throws IOException,MustardException,AuthException {
		JSONObject json = null;
		try {
			json = new JSONObject(StreamUtil.toString(requestData(url,params,attachmentParam,attachment)));
		} catch (JSONException e) {
			throw new MustardException(998,"Non json response: " + e.toString());
		}
		return json;
	}
	
	public JSONArray getJsonArray(String url) throws IOException,MustardException,AuthException {
		return getJsonArray(url,GET,null);
	}
	
	public JSONArray getJsonArray(String url, String httpMethod) 
									throws IOException,MustardException,AuthException {
		return getJsonArray(url,httpMethod,null);
	}
	
	public JSONArray getJsonArray(String url, String httpMethod,
			ArrayList<NameValuePair> params) throws IOException,MustardException,AuthException {
		return getJsonArray(url,httpMethod,params,false);
	}

	public JSONArray getJsonArray(String url, String httpMethod,
			ArrayList<NameValuePair> params, boolean debug) throws IOException,MustardException,AuthException {
		JSONArray json = null;
		InputStream is = null;
		try {
			is = requestData(url,httpMethod,params);
			String s = StreamUtil.toString(is);
			if(debug)
				Log.d("Mustard","\n"+s+"\n");
			json = new JSONArray(s);
		} catch (JSONException e) {
			throw new MustardException(998,"Non json response: " + e.toString());
		} finally {
			if(is != null) {
				try { is.close();} catch (Exception e){}
			}
		}
		return json;
	}

	public InputStream requestData(String url, String httpMethod,ArrayList<NameValuePair> params) throws IOException,MustardException,AuthException {
		return requestData(url, httpMethod,params,0);
	}
	
	private HashMap<String,String> mHeaders;
	
	public void setExtraHeaders(HashMap<String,String> headers) {
		mHeaders = headers;
	}
	
	public InputStream requestData(String url, String httpMethod,ArrayList<NameValuePair> params, int loop) throws IOException,MustardException,AuthException {

		URI uri;

		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new IOException("Invalid URL.");
		}
		if (MustardApplication.DEBUG) Log.d("HTTPManager","Requesting " + uri);
		HttpUriRequest method;

		if (POST.equals(httpMethod)) {
			HttpPost post = new HttpPost(uri);
			if(params != null)
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			method = post;
		} else if (DELETE.equals(httpMethod)) {
			method = new HttpDelete(uri);
		} else {
			method = new HttpGet(uri);
		}

		if (mHeaders != null) {
			Iterator<String> headKeys = mHeaders.keySet().iterator();
			while(headKeys.hasNext()) {
				String key = headKeys.next();
				method.setHeader(key, mHeaders.get(key));
			}
		}
		if (consumer != null) {
			try {
				consumer.sign(method);
			} catch (OAuthMessageSignerException e) {
				 
			}catch (OAuthExpectationFailedException e) {
				
			} catch (OAuthCommunicationException e) {
				
			}
		}

		HttpResponse response;
		try {
			response = mClient.execute(method);
		} catch (ClientProtocolException e) {
			throw new IOException("HTTP protocol error.");
		}

		int statusCode = response.getStatusLine().getStatusCode();
//		Log.d("HttpManager", url + " >> " + statusCode);
		if (statusCode == 401) {
			throw new AuthException(401,"Unauthorized");
		} else if (statusCode == 400 || statusCode == 403 || statusCode == 406) {
			try {
				JSONObject json = null;
				try {
					json = new JSONObject(StreamUtil.toString(response.getEntity().getContent()));
				} catch (JSONException e) {
					throw new MustardException(998,"Non json response: " + e.toString());
				}
				throw new MustardException(statusCode, json.getString("error"));
			} catch (IllegalStateException e) {
				throw new IOException("Could not parse error response.");
			} catch (JSONException e) {
				throw new IOException("Could not parse error response.");
			}
		} else if (statusCode == 404) {
			// User/Group or page not found
			throw new MustardException(404,"Not found: " + url);
		} else if ( (statusCode == 301 || statusCode == 302 || statusCode == 303) && GET.equals(httpMethod) && loop < 3) {
//			Log.v("HttpManager", "Got : " + statusCode);
			Header hLocation = response.getLastHeader("Location");
			if (hLocation != null) {
				Log.v("HttpManager", "Got : " + hLocation.getValue());
				return requestData(hLocation.getValue(), httpMethod,params, loop+1);
			}
			else throw new MustardException(statusCode,"Too many redirect: " + url);
		} else if (statusCode != 200) {
			throw new MustardException(999,"Unmanaged response code: " + statusCode);
		}

		return response.getEntity().getContent();
	}
	
	
	private static final String IMAGE_MIME_JPG = "image/jpeg";
	private static final String IMAGE_MIME_PNG = "image/png";
	
	private InputStream requestData(String url, ArrayList<NameValuePair> params, String attachmentParam, File attachment) 
			throws IOException,MustardException,AuthException {

		URI uri;

		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new IOException("Invalid URL.");
		}
		if (MustardApplication.DEBUG) Log.d("HTTPManager","Requesting " + uri);
		
		HttpPost post = new HttpPost(uri);
		
		HttpResponse response;

		// create the multipart request and add the parts to it 
		MultipartEntity requestContent = new MultipartEntity(); 
		long len = attachment.length();
		
		InputStream ins = new FileInputStream(attachment);
		InputStreamEntity ise = new InputStreamEntity(ins, -1L); 
		byte[] data = EntityUtils.toByteArray(ise);
		 
		String IMAGE_MIME = attachment.getName().toLowerCase().endsWith("png") ? IMAGE_MIME_PNG : IMAGE_MIME_JPG;
		requestContent.addPart(attachmentParam, new ByteArrayBody(data, IMAGE_MIME, attachment.getName()));

		if (params != null) {
			for (NameValuePair param : params) {
				len += param.getValue().getBytes().length;
				requestContent.addPart(param.getName(), new StringBody(param.getValue()));
			}
		}
		post.setEntity(requestContent); 
		
		Log.d("Mustard","Length: " + len);
		
		if (mHeaders != null) {
			Iterator<String> headKeys = mHeaders.keySet().iterator();
			while(headKeys.hasNext()) {
				String key = headKeys.next();
				post.setHeader(key, mHeaders.get(key));
			}
		}

		if (consumer != null) {
			try {
				consumer.sign(post);
			} catch (OAuthMessageSignerException e) {
				 
			}catch (OAuthExpectationFailedException e) {
				
			} catch (OAuthCommunicationException e) {
				
			}
		}
		
		try {
			mClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, DEFAULT_POST_REQUEST_TIMEOUT);
			mClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, DEFAULT_POST_REQUEST_TIMEOUT);
			response = mClient.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new IOException("HTTP protocol error.");
		}

		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode == 401) {
			throw new AuthException(401,"Unauthorized: " + url);
		} else if (statusCode == 400 || statusCode == 403 || statusCode == 406) {
			try {
				JSONObject json = null;
				try {
					json = new JSONObject(StreamUtil.toString(response.getEntity().getContent()));
				} catch (JSONException e) {
					throw new MustardException(998,"Non json response: " + e.toString());
				}
				throw new MustardException(statusCode, json.getString("error"));
			} catch (IllegalStateException e) {
				throw new IOException("Could not parse error response.");
			} catch (JSONException e) {
				throw new IOException("Could not parse error response.");
			}
		} else if (statusCode != 200) {
			Log.e("Mustard", response.getStatusLine().getReasonPhrase());
			throw new MustardException(999,"Unmanaged response code: " + statusCode);
		}

		return response.getEntity().getContent();
	}

	public String getResponseAsString(String url)
		throws IOException,MustardException,AuthException {
		return getResponseAsString(url,GET,null);
	}
	
	public String getResponseAsString(String url, String httpMethod)
		throws IOException,MustardException,AuthException {
		return getResponseAsString(url,httpMethod,null);
	}
	
	public String getResponseAsString(String url, String httpMethod,
			ArrayList<NameValuePair> params) throws IOException,MustardException,AuthException {
		return StreamUtil.toString(requestData(url,httpMethod,params)); 
	}
	
	public String getResponseAsString(String url, ArrayList<NameValuePair> params,String attachmentParam, File attachment) throws IOException,MustardException,AuthException {
		return StreamUtil.toString(requestData(url,params,attachmentParam,attachment));
	}
	
	
	public Document getDocument(String url, String httpMethod,ArrayList<NameValuePair> params) throws IOException,MustardException,AuthException {
		 Document  dom = null;
		InputStream is = null;
		try {
			is = requestData(url,httpMethod,params);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        dom = builder.parse(is);
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
			throw new MustardException(980,"Parser exception: " + e.getMessage());
		} catch(SAXException e) {
			e.printStackTrace();
			throw new MustardException(981,"Parser exception: " + e.getMessage());
		} finally {
			if(is != null) {
				try { is.close();} catch (Exception e){}
			}
		}
		return dom;
	}
	
	
	private HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
	    
	    public void process(
	            final HttpRequest request, 
	            final HttpContext context) throws HttpException, IOException {
	        
	        AuthState authState = (AuthState) context.getAttribute(
	                ClientContext.TARGET_AUTH_STATE);
	        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
	                ClientContext.CREDS_PROVIDER);
	        HttpHost targetHost = (HttpHost) context.getAttribute(
	                ExecutionContext.HTTP_TARGET_HOST);
	        
	        // If not auth scheme has been initialized yet
	        if (authState.getAuthScheme() == null) {
	            AuthScope authScope = new AuthScope(
	                    targetHost.getHostName(), 
	                    targetHost.getPort());
	            // Obtain credentials matching the target host
	            Credentials creds = credsProvider.getCredentials(authScope);
	            // If found, generate BasicScheme preemptively
	            if (creds != null) {
	                authState.setAuthScheme(new BasicScheme());
	                authState.setCredentials(creds);
	            }
	        }
	    }
	    
	};
	
	private  class ByteArrayBody extends AbstractContentBody {

		private final byte[] bytes;
		private final String fileName;

		public ByteArrayBody(byte[] bytes, String mimeType, String fileName) {
			super(mimeType);
			this.bytes = bytes;
			this.fileName = fileName;
		}

		public String getFilename() {
			return fileName;
		}

		@Override
		public void writeTo(OutputStream out) throws IOException {
			out.write(bytes);
		}

		public String getCharset() {
			return null;
		}

		public long getContentLength() {
			return bytes.length;
		}

		public String getTransferEncoding() {
			return MIME.ENC_BINARY;
		}

	}

}
