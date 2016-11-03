package com.tab.tdroid.net;

public class HttpRsq {

	public String errormsg = "";
	public Object data;
	public int httpcode = -1;
	public boolean result = false;
	public String hash = "";

	@Override
	public String toString() {
		return "HttpRsq [errormsg=" + errormsg + ", data=" + data
				+ ", httpcode=" + httpcode + ", result=" + result + ", hash="
				+ hash + "]";
	}

	public HttpRsq() {
	}

	public HttpRsq(String error) {
		errormsg = error;
	}
}
