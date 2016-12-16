package wang.kaelthas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import wang.kaelthas.bean.BarcodeStateBean;

public class Login {
	private static String qqNum = "850459198";
	private static Header[] headers = null;
	private static Header header = null;
	private static String ptwebqq="";

	public static void main(String[] args) {
		getImage();
		BarcodeStateBean barcode=getResult();
		getPtwebqq( barcode.getUrl());
	}

	// step1:获取二维码
	public static void getImage() {
		String url = "https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.1";
		String image = "/Users/server/Desktop/123/test1.png";
		HttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		FileOutputStream fos = null;
		InputStream inputStream = null;
		try {
			// 客户端开始向指定的网址发送请求
			HttpResponse response = client.execute(httpGet);
			headers = response.getAllHeaders();
			String cookie = "Set-Cookie";
			for (Header header2 : headers) {
				if (cookie.equals(header2.getName()))
					header = header2;
			}

			inputStream = response.getEntity().getContent();
			File file = new File("/Users/server/Desktop/123");
			if (!file.exists()) {
				file.mkdirs();
			}

			fos = new FileOutputStream(image);
			byte[] data = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(data)) != -1) {
				fos.write(data, 0, len);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (inputStream != null)
					inputStream.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("已生成二维码");

	}

	// step2:获取二维码状态
	public static BarcodeStateBean getImageState() {
		System.out.println("-------开始查询状态-----");
		BarcodeStateBean barcodeState = new BarcodeStateBean();
		String url = "https://ssl.ptlogin2.qq.com/ptqrlogin?webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-157510&mibao_css=m_webqq&t=1&g=1&js_type=0&js_ver=10143&login_sig=&pt_randsalt=0";
		BufferedReader in = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Cookie", header.getValue());
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					String result = EntityUtils.toString(entity);
					// ptuiCB('66','0','','0','二维码未失效。(2208317380)', '');
					System.out.println(result);
					result = result.replaceAll("'", "");
					result = result.substring(7, result.length() - 1);
					String resultArr[] = result.split(",");
					barcodeState.setCode(Integer.parseInt(resultArr[0]));
					barcodeState.setDescription(resultArr[4]);
					barcodeState.setUrl(resultArr[3]);
					barcodeState.setNickName(resultArr[5]);
					// 认证成功后
					// ptuiCB('0','0','http://ptlogin4.web2.qq.com/check_sig?pttype=1&uin=206479684&service=ptqrlogin&nodirect=0&ptsigx=d287c6735bfcba88686c429381ab803ffb41aab53660f7d28817a49c53265c24ffaab38d0ba228e8c47c7ed25e29ac4249c951b0d835ba7154a3a6a8493ffa7f&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&f_url=&ptlang=2052&ptredirect=100&aid=501004106&daid=164&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=3&pt_aid=0&pt_aaid=16&pt_light=0&pt_3rd_aid=0','0','登录成功！',
					// '珊瑚海');
					//65二维码失效
					if(barcodeState.getCode()==65)
						getImage();

				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return barcodeState;

	}

	// step3:轮询二维码状态
	public static BarcodeStateBean getResult() {
		BarcodeStateBean barcodeState = new BarcodeStateBean();
		do {
			barcodeState = getImageState();
			// System.out.println("-------"+barcodeState.getDescription()+"--------");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (barcodeState.getCode() != 0);
		
		System.out.println("登录成功"+barcodeState.toString());
		return barcodeState;
	}
	
	public static void getPtwebqq(String url){
		HttpClient httpClient=HttpClients.createDefault();
		HttpGet get=new HttpGet(url);
		try {
			HttpResponse response=httpClient.execute(get);
			HttpEntity entity=response.getEntity();
			System.out.println(entity);
			
		} catch (Exception e) {
			System.out.println("获取ptwebqq出错,\n错误信息:"+e.toString());
		}
	}
}
