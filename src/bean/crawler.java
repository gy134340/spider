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
import java.util.HashMap;
import java.util.Iterator;


public class crawler {
	public static void main(String[] args) throws Exception {
		
		Connection conn = getDBConnection();
		bean(conn);
	}
	public static void bean(Connection conn){
		Bean bean = new Bean(conn);
		bean.startRun();
	}



	// connect to demo DB and return connection
	public static Connection getDBConnection() throws Exception, SQLException {

		// String url = "jdbc:mysql://localhost:3305/demoDB?"
		// + "user=root&password=&useUnicode=true&characterEncoding=UTF8";
		String url = "jdbc:mysql://127.0.0.1:3306/crawler?"
				+ "user=root&password=&useUnicode=true&characterEncoding=UTF8";
		
		Class.forName("com.mysql.jdbc.Driver");// 鍔ㄦ�佸姞杞絤ysql椹卞姩
		// or:
		// com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
		// or锛�
		// new com.mysql.jdbc.Driver();

	
		return DriverManager.getConnection(url);
	}
	
	
}
