package wang.kaelthas.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTool {

	public static String doGet(String url, String queryString) {
		return doGet(url, queryString, new HashMap<>());
	}

	public static String doGet(String url, String queryString, Map<String, String> prop) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			if (!prop.isEmpty())
				for (String key : prop.keySet()) {
					connection.addRequestProperty(key, prop.get(key));
				}
			connection.connect();
			// 获取响应头
			Map<String, List<String>> map = connection.getHeaderFields();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
			}
		}

		return result;
	}

	void get(String url) {
		

	}
}
