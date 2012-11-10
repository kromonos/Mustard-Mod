package org.mustard.rsd;

import java.util.ArrayList;

public class Service {

	private String engineName;
	private String engineLink;
	private ArrayList<Api> apis;
	public String getEngineName() {
		return engineName;
	}
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	public String getEngineLink() {
		return engineLink;
	}
	public void setEngineLink(String engineLink) {
		this.engineLink = engineLink;
	}
	public ArrayList<Api> getApis() {
		return apis;
	}
	public void setApis(ArrayList<Api> apis) {
		this.apis = apis;
	}
	
}
