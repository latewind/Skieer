package cn.net.ecode.skieer.conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpConn {
	public static final String ENCODING_UTF_8 = "UTF-8";

	/**
	 * 提交表单
	 * 
	 * @param url
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String submitForm(String url, NameValuePair[] args) throws ClientProtocolException, IOException {
		String resultStr = "";
		CloseableHttpClient httpclient = HttpClients.custom().build();
		try {
			HttpUriRequest tokenReq = RequestBuilder.post().setUri(url).addParameters(args).build();
			CloseableHttpResponse response = httpclient.execute(tokenReq);
			try {
				resultStr = EntityUtils.toString(response.getEntity());
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return resultStr;
	}

	public String get(String url, String token) throws IOException {

		String resultStr="";
		CloseableHttpClient httpclient = HttpClients.custom().build();
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Authorization", "bearer " + token);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				System.out.println("Login form get: " + response.getStatusLine());
				resultStr=EntityUtils.toString(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return resultStr;
	}

	public static void main(String[] args) {

	}
}
