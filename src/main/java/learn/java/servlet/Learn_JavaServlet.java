package learn.java.servlet;

import java.io.IOException;
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
		Map<String, String> envMap = new HashMap<String, String>();
		envMap = System.getenv();
		//获取服务名称（组件需要自定义服务名称）,输出本服务名称
		String hostName = envMap.get("HOSTNAME").split("-")[0];
		String localResp = "=>" + hostName;
		response.getWriter().write(localResp);
		
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
