package org.melody.dao;
import org.melody.bean.*;
import java.util.*;
import java.sql.*;
public class LoginDAO {
	/**
	 * @param id
	 * @param password
	 * @return 登陆成功与否
	 */
		public static User loadUser(String name,String password){
			User newuser=null;
			Connection con = null;
			String sql="select * from user where name='"+name+"' and password='"+password+"'";
			try {
				con = DBHandler.getConnection();
				PreparedStatement pstmt = con
						.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					System.out.println("有这个用户");
					newuser=new User();
					int user_id = rs.getInt("u_id");
					String user_name = rs.getString("name");
					String pwd = rs.getString("password");
					String portrait = rs.getString("portrait");
					newuser.setId(user_id);
					newuser.setName(user_name);
					newuser.setPassword(pwd);
					newuser.setPortrait(portrait);
				}
				rs.close();
				pstmt.close();
				con.close();
				con = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					
					if (con != null) {
						con.close();
					}
				} catch (Exception e2) {
				}
			}
			return newuser;
		}
		
}
