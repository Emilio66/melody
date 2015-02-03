package org.melody.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {
	public static String username;
	public static String password;
	public static String url;
	
	static {
		initialize();	//初始化一些配置信息
	}
	
	public static void initialize(){
		if(username==null){
			Properties prop = new Properties();
			InputStream in = DBHandler.class.getResourceAsStream("/jdbc.properties");
			String driver="";
			try {
				prop.load(in);//加载配置文件
				username=prop.getProperty("jdbc.username");
				password=prop.getProperty("jdbc.password");
				url=prop.getProperty("jdbc.url");
				driver=prop.getProperty("jdbc.driver");
				System.out.print(username+"\n"+password+"\n"+url+"\n"+driver);
				
				Class.forName(driver).newInstance();//注册JDBC驱动
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			return;	//当配置文件加载过一次之后就不用重复再加载了
		}
	}
	
	/*public static DataSource ds=null;
	static {
		init();
	}
	
	public static void init(){	
		try {
			Context context=new InitialContext();//get context instance and initialize it
			ds=(DataSource)context.lookup("java:comp/env/jdbc/melody");//get data source			
		} catch (NamingException e) {
			System.out.println("Can not find datasource!!!");
			e.printStackTrace();
		}
	}
	public static Connection getConnection() throws SQLException{
		return ds.getConnection();
	}*/
	
	public static Connection getConnection() throws SQLException{		
		return DriverManager.getConnection(url,username,password);//根据配置信息获得连接	
	}
	public static void main(String[]args) {
		try {
			Connection connection=getConnection();
			System.out.println("Get connection !");
		} catch (SQLException e) {
			System.out.println("Can not get connection from database");
			e.printStackTrace();
		}
	}
}
