package cn.net.ecode.skieer.conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.net.ecode.skieer.entity.Token;
import cn.net.ecode.skieer.exceptions.SkieerHttpResponseException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.*;
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
	public static String submitForm(String url, NameValuePair[] args) throws IOException {
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

	public String getData(String url, Token token) throws IOException, SkieerHttpResponseException {
		String resultStr="";
		CloseableHttpClient httpclient = HttpClients.custom().build();
		try {
			HttpGet httpget = new HttpGet(url);
            buildRequestHeader(httpget);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				System.out.println("get: " + response.getStatusLine()+Thread.currentThread().getName());
				if(response.getStatusLine().getStatusCode()!=200){
					resultStr=EntityUtils.toString(entity);
					throw new SkieerHttpResponseException(url+token);
				}

                resultStr=EntityUtils.toString(entity);
			} finally {
				System.out.println(resultStr);
				response.close();
			}
		} finally {
			System.out.println(url);
			httpclient.close();
		}
		return resultStr;
	}
	protected void buildRequestHeader(HttpRequestBase httpRequestBase){

    }

	public static void main(String[] args) {

	}


}
