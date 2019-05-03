package learn.java.servlet;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Learn_JavaServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取环境变量
//		Map<String, String> envMap = new HashMap<String, String>();
//		envMap = System.getenv();
		
		Map<String, String> envMap = System.getenv();
		ArrayList<String> nextServer = new ArrayList<String>();
		
		
		//获取本机服务名称
		String hostName = envMap.get("HOSTNAME").split("-")[0];
		String respString = "=>" + hostName;;
		
		//获取连线组件信息(连线的环境变量必须满足NEXTSERVER+数字样式)
		for(String key : envMap.keySet()) {
			if(key.contains("NEXTSERVER")) {
				nextServer.add(key.toUpperCase());
			}
		}
		
		//如果环境变量中不存在其他连线组件，则返回服务名称结果
		if(nextServer.isEmpty()) {
			response.getWriter().write(respString);
		}else {
			for(String next:nextServer) {
				String nextHost = envMap.get(next + "_SERVICE_HOST");
				String nextPort = envMap.get(next + "_SERVICE_PORT");
				String nextUrl = "http://" + nextHost + ":" + nextPort;
				
				respString = respString + nextUrl;
				
				String nexResp = sendGet(nextUrl);
				respString = respString + nexResp;
			}
			response.getWriter().write(respString);
		}
		
		
	}
	
	//使用httpclient发送get请求到指定url
	protected String sendGet(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);
		CloseableHttpResponse response = null;
		String resultString = null;
		try {
			response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				resultString = EntityUtils.toString(entity,"UTF-8").trim();
			}
			response.close();
			httpClient.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultString;
	}

}
