package org.mustard.android.provider;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mustard.rsd.Api;
import org.mustard.rsd.Service;
import org.mustard.rsd.Setting;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StatusNetXMLUtil {
	
	public static Service getRsd(InputStream is) {
		
		Service rsd = new Service();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document  dom = builder.parse(is);
            Element root = dom.getDocumentElement();
            Node rsdNode = root.getFirstChild();
            if (!rsdNode.getNodeName().equals("rsd")) {
            	return null;
            }
            Node serviceNode = rsdNode.getFirstChild();

            
            
            NodeList items = serviceNode.getChildNodes();
            for (int i=0;i<items.getLength();i++){
                Service service = new Service();
                Node item = items.item(i);
                String itemName = item.getNodeName();
                ArrayList<Api> al_apis = new ArrayList<Api>();
                
                if (itemName.equals("engineName")) {
                	service.setEngineName(item.getNodeValue());
                } else if (itemName.equals("engineLink")) {
                	service.setEngineLink(item.getNodeValue());
                } else if (itemName.equals("apis")) {
                	NodeList apis = item.getChildNodes();
                	for (int j=0;j<apis.getLength();j++){
                		Api api = new Api();
                		Node apiNode = apis.item(j);
                		NamedNodeMap attr = apiNode.getAttributes();
                		api.setName(attr.getNamedItem("name").getNodeValue());
                		api.setPreferred(attr.getNamedItem("preferred").getNodeValue());
                		api.setBlogId(attr.getNamedItem("blogID").getNodeValue());
                		api.setApiLink(attr.getNamedItem("apiLink").getNodeValue());
                		
                		NodeList nl_settings = apiNode.getChildNodes();
                		ArrayList<Setting> al_settings = new ArrayList<Setting>();
                		for (int k=0;k<nl_settings.getLength();k++) {
                			Node n = nl_settings.item(k);
                			String nApiNode = n.getNodeName();
                			if (nApiNode.equals("setting")) {
                				Setting setting = new Setting();
                				setting.setValue(n.getNodeValue());
                				al_settings.add(setting);
                			}
                		}
                		api.setSettings(al_settings);
                		al_apis.add(api);
                	}
                	service.setApis(al_apis);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
		
		return rsd;
	}

}
