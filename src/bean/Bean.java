package bean;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Bean{

	private Connection conn;
	private static Integer number = 0; 
	private static String DOMAIN =  "https://m.douban.com";	
	private static String BASEURL = DOMAIN + "/group/shanghaizufang/";  		//the website entry
	
	
	private static String SUBWAY = "13号线";		//地铁线路
	private static String[] AREAS = {"金运路","金沙江西路","丰庄"};
	
	Bean(Connection c) {
		conn = c;
	}
	
	public void startRun() {
		
		execParents(BASEURL);
	}
	
	public void execParents(String s){
		
		String str = getPageHtml(s);
		
		//暂停1s
		try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }
		
		//判断页面是否存在
		if(str.indexOf("这个页面不在了") != -1){
			return;
		}
		
		//找到当前的25条数据进行初步的过滤
		Document doc = Jsoup.parse(str);
		Elements eles = doc.select("ul.list.topic-list li");
		for(Element ele:eles){
			HashMap<String, String> obj =  new HashMap();
			String text = ele.select("h3").text();
			String imgSumUrl = ele.select("div.cover img").attr("src");
			String url = DOMAIN + ele.select("a").attr("href");
			String date = ele.select("div.info").text();
			if(isDest(text) == true){
				System.out.println(text);
				obj.put("name", text);
				obj.put("url", url);
				obj.put("date", date);
				try {
					saveData(conn,obj,"bean");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
		}
		
		//并获取下一个父页面的链接
		String nextUrl = BASEURL + doc.select("section.pagination a.next").attr("href");  //get nextUrl
		
		if(nextUrl != null && nextUrl.length() > 0 ){
			execParents(nextUrl);
		}
		
	}
	
	//判断是否有13号线相关信息
	//@param 传入a便签内html进行初步过滤
	public boolean isDest(String tt){
		String text = tt;
		Boolean flag = false;
		if(text.indexOf(SUBWAY) != -1){
			flag = true;
		}else{
			for(String area:AREAS){
				if(text.indexOf(area) != -1 ){
					flag = true;
				}else{
					flag = false;
				}
			}
		}	
		return flag;
	}

	
	
//	public void execChilds(String url){
//		String html = getPageHtml(url);
//		
//		Document doc = Jsoup.parse(html);
//		Elements cates = doc.select("div.shop-list ul li");
//		
//		for(Element cate:cates){
//			HashMap<String,String> obj = new HashMap();
//			try {
//				String name = cate.select("div.tit h4").text();
//				String address = cate.select("div.tag-addr .addr").text();
//				obj.put("name", name);
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				System.out.println("DianPing miss one O_O");
//				e.printStackTrace();
//			}
//
//			try {
//				saveData(conn,obj,"dianping");
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	/**
	 * Helper Function to get page Html
	 * @param url
	 * @return
	 */
	public String getPageHtml(String url) {
		String inputLine;
		String input = "";
		try {
			InputStream inStream = httpRequest(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inStream));

			if ((inputLine = in.readLine()) != null) {
				input = inputLine;
				while ((inputLine = in.readLine()) != null) {
					input = input + inputLine;
				}
				// start collect Information
			} else {
				// A complete blank page, almost never happen
				System.out.print(url + " empty");
			}
			in.close();
		} catch (Exception e) {
			// Auto-generated catch block
			// e.printStackTrace();
		}
		return input;
	}
	// Helper Method: save data in DB
	public static void saveData(Connection conn, HashMap<String, String> data,
			String table) throws SQLException {
		Statement stmt = conn.createStatement();
		String attrs = "";
		String values = "";
		// loop through hashMap
		Iterator it = data.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (it.hasNext()) {
				attrs = attrs + "`" + key + "`,";
				values = values + "'" + data.get(key) + "',";
			} else {
				attrs = attrs + "`" + key + "`";
				values = values + "'" + data.get(key) + "'";
			}

			// System.out.println("key:" + key);
			// System.out.println("value:" + data.get(key));
		}
		String sql = "INSERT INTO `" + table + "`(" + attrs + ") values("
				+ values + ")";
		// System.out.println(data.get("link"));
		stmt.executeUpdate(sql);
	}

	// Http Request to get raw html data
	public static InputStream httpRequest(String url) throws Exception {
		// System.out.println("Start making http request");

		URL sampleURL = new URL(url);
		HttpURLConnection url_con;
		url_con = (HttpURLConnection) sampleURL.openConnection();
//		url_con.setRequestProperty(
//				"User-Agent",
//				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		url_con.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.366");
		url_con.setDoOutput(true);
		url_con.setRequestMethod("GET");

		InputStream is = null;
		try {
			is = url_con.getInputStream();
		} catch (Exception e1) {
			System.out.println("invalid URL:" + url);
		}
		return is;
	}

}
