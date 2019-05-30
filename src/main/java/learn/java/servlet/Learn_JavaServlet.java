package learn.java.servlet;

import java.awt.List;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
	
	public static final String HTTPPORT = "8080";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取环境变量
		Map<String, String> envMap = System.getenv();
		ArrayList<String> nextHttpServer = new ArrayList<String>();
		ArrayList<String> nextTcpServer = new ArrayList<String>();
		
		//获取本机服务名称
//		String hostName = envMap.get("HOSTNAME").split("-")[0];
		String hostName = envMap.get("HOSTNAME");
		String respString = "==>" + hostName;
		
		//获取HTTP连线组件信息(连线的环境变量必须满足HTTPSERVER+数字样式)
		for(String key : envMap.keySet()) {
			if(key.contains("HTTPSERVER")) {
				nextHttpServer.add(envMap.get(key).toUpperCase());
			}
		}
		
		//获取TCP连线组件信息(连线的环境变量必须满足TCPSERVER+数字样式)
		for(String key : envMap.keySet()) {
			if(key.contains("TCPSERVER")) {
				nextTcpServer.add(envMap.get(key).toUpperCase());
			}
		}
		
		//如果有连接的HTTP服务则发送get请求
		if(!nextHttpServer.isEmpty()) {
			for(String nextHttp : nextHttpServer) {
//				String nextHttpHost = envMap.get(nextHttp + "_SERVICE_HOST");
//				String nextHttpPort = envMap.get(nextHttp + "_SERVICE_PORT");
				//如果组件同集群则从环境变量中取IP和端口，如果跨集群则使用service名称和默认8080端口
				String nextHttpHost = (null == envMap.get(nextHttp + "_SERVICE_HOST")?nextHttp.toLowerCase():envMap.get(nextHttp + "_SERVICE_HOST"));
				System.out.println(nextHttpHost);
				String nextHttpPort = (null == envMap.get(nextHttp + "_SERVICE_PORT")?HTTPPORT:envMap.get(nextHttp + "_SERVICE_PORT"));
				System.out.println(nextHttpPort);
				String nextHttpUrl = "http://" + nextHttpHost + ":" + nextHttpPort;
				System.out.println(nextHttpUrl);
				String nextHttpResp = httpGetRequest(nextHttpUrl, nextHttp.toLowerCase());
				respString = respString + nextHttpResp;
			}
		}
		
		//如果有连接的TCP服务则发送TCP请求
		if (!nextTcpServer.isEmpty()) {
			for(String nextTcp:nextTcpServer) {
				String nextTcpHost = envMap.get(nextTcp + "_SERVICE_HOST");
				int nextTcpPort = Integer.valueOf(envMap.get(nextTcp + "_SERVICE_PORT"));
				String nextTcpResp = TcpRequest(nextTcpHost, nextTcpPort, nextTcp.toLowerCase());
				respString = respString + nextTcpResp;
			}
		}
		
	    response.getWriter().write(respString);
	    
	}
	
	
	//使用httpclient发送get请求到指定url
	protected String httpGetRequest(String url, String serverName) {
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
			resultString = serverName + " Exception";
		} catch (IOException e) {
			e.printStackTrace();
			resultString = serverName + " Exception";
		}
		return resultString;
	}
	
	
	//发送TCP请求
	protected String TcpRequest(String host, int port, String serverName) {
		String resultString = null;
		try {
			Socket s = new Socket(host,port);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			resultString = "==>" + dis.readUTF();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			resultString = "==>" + serverName + "Exception";
		} catch (IOException e) {
			e.printStackTrace();
			resultString = "==>" + serverName + "Exception";
		}
		return resultString;
	}

}
