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
	private static String DOMAIN =  "https://m.douban.com";	
	private static String BASEURL = DOMAIN + "/group/shanghaizufang/";  		//the website entry
	Bean(Connection c) {
		conn = c;
	}
	
	public void startRun() {
//		getAllUrls(BASEURL);
		for(int i=0;i<100;i++){
			String str = getPageHtml(BASEURL);
			String cate = BASEURL+ "?start="+ String.valueOf(25*i);
			System.out.println(cate);
//			try {
//				Thread.currentThread().sleep(5000);
//			} catch (InterruptedException e) {
//				System.out.println("wrong thread "+i);
//				e.printStackTrace();
//			}
			//System.out.println(i+str);
		}
//		String str = getPageHtml(BASEURL);
//		System.out.println(str);
	}
	public void getAllUrls(String s){
		
		String url = s;
		if(!dianPingCheck(url)){
			
			execGrandParentNodes(url);

		}else{
			System.out.println("NULL page");
		}
	}
	
	/**
	 * Helper Function to get page Html
	 * 
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
			// read html into a string

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
	
	//第一层节�? grandparents @Todo trans2 iteration to be more elegant
	public void execGrandParentNodes(String url){
		String html = getPageHtml(url);
		Document doc = Jsoup.parse(html);
		Elements cates = doc.select("#classfy a");
		for(Element cate:cates){
			String href = cate.attr("href");
			href = DOMAIN + href;
		}
	}
	//第二层节�? parents
	public void execParentNodes(String url){

		String html = getPageHtml(url);
		Document doc = Jsoup.parse(html);
		Elements cates = doc.select("div.page a");
		for(Element cate:cates){
			String href = cate.attr("href");
			href = DOMAIN + href;
			execChilds(href);
		}
	}
	
	//第三层节�? childs获得数据
	public void execChilds(String url){
		String html = getPageHtml(url);
		
		Document doc = Jsoup.parse(html);
		Elements cates = doc.select("div.shop-list ul li");
		
		for(Element cate:cates){
			HashMap<String,String> obj = new HashMap();
			try {
			
				String name = cate.select("div.tit h4").text();
				String address = cate.select("div.tag-addr .addr").text();
				String link = DOMAIN + cate.select("div.tit a").attr("href");
				String sale = cate.select("a.mean-price").text();
				
				//@Todo tel�?要进�?步获取下�?级html
				//String tel = getTel(link);
				
				String rate = cate.select("div.comment span").attr("title")+
								cate.select("div.comment a").first().text()+
								cate.select("div.comment-list").text();
				String tags = cate.select("div.tag-addr span.tag").text();
				String comments = cate.select("div.comment a").first().text();
				//String comment_link = DOMAIN + cate.select("div.comment a").attr("href");
				String type =  cate.select("div.tag-addr span.tag").text();
				//团购及优惠信�?
				Elements promotionCates = cate.select("div.svr-info .si-deal");
				String promotions = "";
				for(Element promotionCate:promotionCates){
					if(promotionCate.attr("class").indexOf("more") != -1){
						continue;
					}
					promotions = promotions+promotionCate.outerHtml();
				}
				
				obj.put("name", name);
				obj.put("address", address);
				obj.put("link", link);
				obj.put("sale", sale);
				obj.put("rate", rate);
				obj.put("tags", tags);
				obj.put("comments", comments);
				obj.put("type", type);
				obj.put("promotions",promotions);
				System.out.println(obj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("DianPing miss one O_O");
				e.printStackTrace();
			}

			try {
				saveData(conn,obj,"dianping");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	@Todo maybe include
//	public String getTel(String link) {
//		String url = link;
//		String tel = null;
//		String html = getPageHtml(url);
//		Document doc = Jsoup.parse(html);
//		tel = doc.select("p.tel").text();
//		return tel;
//	}


	
	public boolean dianPingCheck(String url) {
		return getPageHtml(url).split("errorMessage").length > 1;
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
		url_con.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
