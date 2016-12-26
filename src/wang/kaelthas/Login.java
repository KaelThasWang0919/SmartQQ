package wang.kaelthas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import wang.kaelthas.bean.BarcodeStateBean;

public class Login {
	private static Header[] headers = null;
	private static Header header = null;
	private static String ptwebqq = "";
	private static String vfwebqq = "";
	private static String psessionid = "";
	private static String uin = "";
	private static String clientid = "53999199";// 固定值

	public static CloseableHttpClient httpClient = null;
	public static HttpClientContext context = null;
	public static CookieStore cookieStore = null;
	public static RequestConfig requestConfig = null;

	public static void main(String[] args) {
		httpClient = HttpClients.createDefault();
		context = HttpClientContext.create();

		getImage();
		BarcodeStateBean barcode = getResult();
		boolean result = getPtwebqq(barcode.getUrl());
		if (result) {
			getVfwebqq();
		}
		if(getPsessionidAndUin())
			getFriendList(uin, ptwebqq, vfwebqq);
//		do {
//			getMessage();
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} while (true);

	}

	// step1:获取二维码
	public static void getImage() {
		String url = "https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.1";
		String root=System.getProperty("user.dir")+"\\";
		String imagePath = root+"test1.png";

		HttpGet httpGet = new HttpGet(url);
		FileOutputStream fos = null;
		InputStream inputStream = null;
		try {
			// 客户端开始向指定的网址发送请求
			HttpResponse response = httpClient.execute(httpGet, context);
			headers = response.getAllHeaders();
			String cookie = "Set-Cookie";
			for (Header header2 : headers) {
				if (cookie.equals(header2.getName()))
					header = header2;
			}

			inputStream = response.getEntity().getContent();
//			File file = new File("/Users/server/Desktop/");
//			if (!file.exists()) {
//				file.mkdirs();
//			}

			fos = new FileOutputStream(imagePath);
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

		try {
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Cookie", header.getValue());
			CloseableHttpResponse response = httpClient.execute(httpget, context);
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
					barcodeState.setUrl(resultArr[2]);
					barcodeState.setNickName(resultArr[5]);
					// 认证成功后
					// ptuiCB('0','0','http://ptlogin4.web2.qq.com/check_sig?pttype=1&uin=206479684&service=ptqrlogin&nodirect=0&ptsigx=d287c6735bfcba88686c429381ab803ffb41aab53660f7d28817a49c53265c24ffaab38d0ba228e8c47c7ed25e29ac4249c951b0d835ba7154a3a6a8493ffa7f&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&f_url=&ptlang=2052&ptredirect=100&aid=501004106&daid=164&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=3&pt_aid=0&pt_aaid=16&pt_light=0&pt_3rd_aid=0','0','登录成功！',
					// '珊瑚海');
					// 65二维码失效
					if (barcodeState.getCode() == 0) {
						// 获取session中的ptwebqq
						cookieStore = context.getCookieStore();

						for (Cookie cookie : cookieStore.getCookies()) {
							if (cookie.getName().equals("ptwebqq"))
								ptwebqq = cookie.getValue();
						}
						headers = response.getAllHeaders();
					}
					if (barcodeState.getCode() == 65)
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
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (barcodeState.getCode() != 0);

		return barcodeState;
	}

	public static boolean getPtwebqq(String url) {
		HttpGet get = new HttpGet(url);
		get.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
		get.setHeaders(headers);
		try {
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == 302) {
				headers = response.getAllHeaders();
				return true;
			}

		} catch (Exception e) {
			System.out.println("获取ptwebqq出错,\n错误信息:" + e.toString());

		}
		return false;
	}

	public static boolean getVfwebqq() {
		String url = "http://s.web2.qq.com/api/getvfwebqq?ptwebqq=" + ptwebqq
				+ "&clientid=53999199&psessionid=&t=1482456976302";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		try {
			HttpResponse response = httpClient.execute(get, context);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			JSONObject jsonObject = new JSONObject(result);
			vfwebqq = jsonObject.getJSONObject("result").getString("vfwebqq");

		} catch (Exception e) {
			System.out.println("获取ptwebqq出错,\n错误信息:" + e.toString());

		}
		return false;
	}

	public static boolean getPsessionidAndUin() {
		String url = "http://d1.web2.qq.com/channel/login2";
		HttpPost post = new HttpPost(url);
		post.addHeader("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
		post.addHeader("Origin", "http://d1.web2.qq.com");
		String parem = "{\"ptwebqq\":\"" + ptwebqq
				+ "\",\"clientid\":53999199,\"psessionid\": \"\",\"status\":\"online\"}";
		// {"ptwebqq":"23bf0d377cbb05636a4c9bd1105afcbe2686946cf98bd0ab7b6e4501d2e97618","clientid":53999199,"psessionid":"","status":"online"}

		// 设置参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("r", parem));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			HttpResponse response = httpClient.execute(post, context);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
//			{
//			    "result":{
//			        "port":47450,
//			        "f":0,
//			        "index":1075,
//			        "psessionid":"8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e383400001ad00000066b026e040015808a206d0000000a406172314338344a69526d0000002859185d94e66218548d1ecb1a12513c86126b3afb97a3c2955b1070324790733ddb059ab166de6857",
//			        "cip":23600812,
//			        "uin":206479684,
//			        "vfwebqq":"59185d94e66218548d1ecb1a12513c86126b3afb97a3c2955b1070324790733ddb059ab166de6857",
//			        "status":"online",
//			        "user_state":0
//			    },
//			    "retcode":0
//			}
			JSONObject jsonObject = new JSONObject(result);
			psessionid = jsonObject.getJSONObject("result").getString("psessionid");
			uin = jsonObject.getJSONObject("result").getString("uin");
			return true;

		} catch (Exception e) {
			System.out.println("getPsessionid出错,\n错误信息:" + e.toString());

		}
		return false;
	}

	// 获取消息消息
	public static void getMessage() {
		String url = "http://d1.web2.qq.com/channel/poll2";
		HttpPost post = new HttpPost(url);
		post.addHeader("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
		// post.addHeader("Origin", "http://d1.web2.qq.com");
		String parem = "{\"ptwebqq\":\"" + ptwebqq + "\",\"clientid\":53999199,\"psessionid\": \"" + psessionid+ "\",\"status\":\"online\"}";
		// {"ptwebqq":"23bf0d377cbb05636a4c9bd1105afcbe2686946cf98bd0ab7b6e4501d2e97618","clientid":53999199,"psessionid":"","status":"online"}

		// 设置参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("r", parem));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			HttpResponse response = httpClient.execute(post, context);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println(getCurrentTime() + result);

		} catch (Exception e) {
			System.out.println("getMessage出错,\n错误信息:" + e.toString());

		}
	}
	
	
	//获取好友列表
	public static void getFriendList(String uin,String ptWebqq,String vfWebqq) {
		String hash=getHash(uin, ptWebqq);
		String url="http://s.web2.qq.com/api/get_user_friends2";
		String referer="http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
	
		HttpPost post = new HttpPost(url);
		post.addHeader("Referer", referer);
		String parem = "{\"vfwebqq\":\"" + vfWebqq + "\",\"hash\":\""+hash+"\"}";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("r", parem));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			HttpResponse response = httpClient.execute(post, context);
			HttpEntity entity = response.getEntity();
			String entityArr= EntityUtils.toString(entity);
			JSONObject jsonObject=new JSONObject(entityArr);
			if("0".equals(jsonObject.getString("retcode"))){
				JSONObject result=jsonObject.getJSONObject("result");
				JSONArray friendJson=result.getJSONArray("friends");
				JSONArray marknamesJson=result.getJSONArray("marknames");
				JSONArray categoriesJson=result.getJSONArray("categories");
				JSONArray infoJson=result.getJSONArray("info");
				
				System.out.println("-------------"+friendJson.length()+"\n"+marknamesJson.length()+"\n"+categoriesJson.length()+"\n"+infoJson.length());

				
			}

		} catch (Exception e) {
			System.out.println("getFriendList出错,\n错误信息:" + e.toString());

		}
	}
	
	
	
	public static String getCurrentTime(){
		String currentTime="";
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		currentTime = dateFormat.format( now ); 
		return currentTime;
	}
	
	
	public static String getHash(String uin,String ptWebqq) {
		String result="";
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine en = manager.getEngineByName("javascript");
		try {
			en.eval(new FileReader(new File("hash.js")));
			Object t = en.eval("u("+uin+",\""+ptWebqq+"\")");
			result=t.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return result;
		
	}

}
