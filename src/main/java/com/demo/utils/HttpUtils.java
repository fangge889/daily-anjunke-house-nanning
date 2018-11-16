package com.demo.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	public static String CreatHttpGet(String url) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
		httpget.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		httpget.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");

		HttpResponse response;
		String result = null;
		try {
			response = httpclient.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				String newUri = response.getLastHeader("Location").getValue();
				httpclient = HttpClients.createDefault();
				httpget = new HttpGet(newUri);
				response = httpclient.execute(httpget);
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 将源码流保存在一个byte数组当中，因为可能需要两次用到该流，
				byte[] bytes = EntityUtils.toByteArray(entity);
				String charSet = "";
				charSet = EntityUtils.getContentCharSet(entity);
				result = new String(bytes, charSet);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		return result;
	}
}
