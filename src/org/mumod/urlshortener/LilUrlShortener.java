package org.mumod.urlshortener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.mumod.util.AuthException;
import org.mumod.util.HttpManager;
import org.mumod.util.MustardException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

public abstract class LilUrlShortener implements UrlShortener {

	protected String lilUrl;

	protected Context mContext;
	
	public LilUrlShortener(Context context) {
		mContext=context;
	}
	
	public String doShort(String longUrl, HashMap<String, String> params)
	throws MustardException {
		return doShort(longUrl);
	}

	public String doShort(String longUrl) throws MustardException {
		URL uri = null;
		try {
			uri =new URL(lilUrl);
		} catch (MalformedURLException e) {
			throw new MustardException(e.getMessage());
		}
		HttpManager hm = new HttpManager(mContext,uri.getHost());
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("longurl", longUrl));
		String ret = "";
		try {

			Document dom = hm.getDocument(lilUrl,HttpManager.POST,params);
			Element root = dom.getDocumentElement();
			NodeList feedNode = root.getElementsByTagName("body");
			if (feedNode == null) {
				//Log.i("mustard", "\"body\" is null");
				return null;
			}
			//Log.i("mustard", "body found");

			for (int i=0;i<feedNode.getLength();i++){

				Node item = feedNode.item(i);
				String itemName = item.getNodeName();
				//Log.i("mustard", "found 1 " + itemName);

				if (itemName.equals("body")) {
					NodeList pchilds = item.getChildNodes();

					for (int ii=0;ii<pchilds.getLength();ii++){

						Node aNode = pchilds.item(ii);
						if (aNode.getNodeType()!=Node.ELEMENT_NODE) 
							continue;
						String iitemName = aNode.getNodeName();
						//Log.i("mustard", "found 2 " + iitemName);


						if (iitemName.equals("p")) {
							NodeList ppchilds = aNode.getChildNodes();
							for (int iii=0;iii<ppchilds.getLength();iii++){
								Node aaNode = ppchilds.item(iii);
								if (aaNode.getNodeType()!=Node.ELEMENT_NODE) 
									continue;
								String iiitemName = aaNode.getNodeName();
								//Log.i("mustard", "found 3 " + iiitemName);
								if(iiitemName.equals("a")) {
									NamedNodeMap attr = aaNode.getAttributes();
									ret = attr.getNamedItem("href").getNodeValue();
									//Log.i("mustard", "href= " + ret);
									return ret.trim();
								}
							}
						}
					}
				}


			}
		} catch (AuthException e) {
			// Never thrown, no auth required
			throw new MustardException("Unauth");
		} catch (IOException e) {
			throw new MustardException(e.getMessage());
		}
		return ret;
	}

}
