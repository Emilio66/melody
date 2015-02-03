package org.melody.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.melody.bean.User;

public class UserDAO {
	
	/**
	 * @see 要注意朋友状态 status !='0' ,如果朋友的昵称nickname不为空，则将该用户的名字设置为nickname,方便列表查看
	 * @param user_id
	 * @return 返回朋友列表
	 */
	public static List<User> getFriendList(int user_id){
		Connection con=null;
		List<User> list_user=new ArrayList<User>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from user where u_id in " +
					"(select u_id from friendship where status>0 and u2_id= "+
					user_id+") or u_id in " +
					"(select u2_id from friendship where status>0 and u_id="+user_id+")";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				User newuser=new User();
				newuser.setId(rs.getInt("u_id"));
				newuser.setName(rs.getString("name"));
				newuser.setPassword(rs.getString("password"));
				newuser.setPortrait(rs.getString("portrait"));
				list_user.add(newuser);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return list_user;
	}
	/**
	 * 返回用户所有朋友的id
	 * @param user_id
	 * @return
	 */
	public static List<Integer> getFriendIds(int user_id){
		Connection con=null;
		List<Integer> list_user=new ArrayList<Integer>();
		try {
			con=DBHandler.getConnection();
			String sql="select u_id from user where u_id in " +
					"(select u_id from friendship where u2_id= "+
					user_id+") or u_id in " +
					"(select u2_id from friendship where u_id="+user_id+")";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Integer u_id=new Integer(rs.getInt("u_id"));
				
				list_user.add(u_id);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return list_user;
	}
	public static boolean isNameValid(String username){
		boolean flag=true;
		Connection con=null;
		try {
			con=DBHandler.getConnection();
			String sql="select * from user where name='"+username+"'";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				flag=false;
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return flag;
	}
	public static User insertUser(User newUser){
		String user_name=newUser.getName();
		String password=newUser.getPassword();
		String portrait=newUser.getPortrait();
		Connection con=null;
		try {
			con=DBHandler.getConnection();
			String sql="insert into user(name,password,portrait) values(?,?,?)";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setString(1, user_name);
			pstmt.setString(2, password);
			pstmt.setString(3, portrait);
			int a=pstmt.executeUpdate();
			sql="select * from user where name='"+user_name+"'";
			ResultSet rs= pstmt.executeQuery(sql);
			if(a>0&&rs.next())
				newUser.setId(rs.getInt(1));//最重要的就是获得id
			else {
				newUser=null;
			}
			rs.close();
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return newUser;
	}
	/**
	 * @see 添加朋友，先判定是否是已经删除的好友，即status='0'
	 * @param user_id
	 * @param friend_id
	 * @return 添加是否成功
	 */
	public static boolean addFriendRequest(int user_id,int friend_id){
		Connection con=null;
		boolean flag=false;
		try {
			con=DBHandler.getConnection();
			String selectString ="select * from friendship where (u_id=? and u2_id=?) or (u_id=? and u2_id=?)";
			PreparedStatement pstmt=con.prepareStatement(selectString);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, friend_id);
			pstmt.setInt(3, friend_id);
			pstmt.setInt(4, user_id);
			ResultSet rSet=pstmt.executeQuery();
			//当且仅当两个用户之间没有好友关系时才添加这俩的关系
			if(rSet.next()){
				String udpateString="update friendship set status='1' where (u_id=? and u2_id=?) or (u_id=? and u2_id=?)";
				pstmt=con.prepareStatement(udpateString);
				pstmt.setInt(1, user_id);
				pstmt.setInt(2, friend_id);
				pstmt.setInt(3, friend_id);
				pstmt.setInt(4, user_id);
				if(pstmt.executeUpdate()>0){
					flag=true;
					System.out.println("删除的好友又复活啦");
				}
			}else{
				String insertSql="insert into friendship (u_id,u2_id,status) values ("+user_id+","+friend_id+",'1')";
				int a=pstmt.executeUpdate(insertSql);
		        if(a==1){
		        	flag=true;
		        }			
			}
			rSet.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		
		return flag;
	}
	
	/**
	 * @see 添加朋友，对方同意了，更新
	 * @param user_id
	 * @param friend_id
	 * @return 添加是否成功
	 */
	public static boolean addFriendConfirmed(int user_id,int friend_id){
		Connection con=null;
		boolean flag=false;
		try {
			con=DBHandler.getConnection();
			String sql="update friendship set status='1' where u_id=? " +
					"and u2_id=? or u_id=? and u2_id=?";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, user_id);pstmt.setInt(2, friend_id);
			pstmt.setInt(3, friend_id);pstmt.setInt(4, user_id);
			int a=pstmt.executeUpdate();
	        if(a==1){
	        	flag=true;
	        }
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		
		return flag;
	}
	/**
	 * @see 删除朋友，需要查找friendship这个表，如果u_id=user_id&u2_id=friend_id OR u_id=friend_id&u2_id=user_id
	 * @param user_id
	 * @param friend_id
	 * @return 删除是否成功
	 */
	public static boolean deleteFriend(int user_id,int friend_id){
		Connection con=null;
		boolean flag=false;
		try {
			con=DBHandler.getConnection();
			String sql="update friendship set status='0' where (u_id="+user_id+" and u2_id="+friend_id+
					" ) or ( u_id="+ friend_id+" and u2_id="+user_id+" )";
			PreparedStatement pstmt=con.prepareStatement(sql);
			int a=pstmt.executeUpdate();
	        if(a==1){
	        	flag=true;
	        }
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		
		return flag;
	}
	
	/**
	 * @param user_id
	 * @return t通过ID查找的这个用户
	 */
	public static User getUserById(int user_id){
		Connection con=null;
		User user=new User();
		try {
			con=DBHandler.getConnection();
			String sql="select * from user where u_id="+user_id;
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				user.setId(rs.getInt("u_id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
				user.setPortrait(rs.getString("portrait"));
			}
			rs.close();
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return user;
	}
	
	/**
	 * @see 通过用户名模糊查询
	 * @param name
	 * @return 具有相同关键字的用户结合
	 */
	public static List<User> getUserByName(String name){
		Connection con=null;
		List<User> list_user=new ArrayList<User>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from user where name like '%"+name+"%'";
			//System.out.println(sql);
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				User newuser=new User();
				newuser.setId(rs.getInt("u_id"));
				newuser.setName(rs.getString("name"));
				newuser.setPassword(rs.getString("password"));
				newuser.setPortrait(rs.getString("portrait"));
				list_user.add(newuser);
			}
			rs.close();
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return list_user;
	}
	

	/**
	 * @see 修改好友的昵称
	 * @param user_id
	 * @param friend_id
	 * @param nickname
	 * @return
	 */
	public static boolean updateNickname(int user_id,int friend_id,String nickname){
		Connection con=null;
		boolean flag=false;int a=0;
		try {
			con=DBHandler.getConnection();
			String sql="select * from friendship where u_id="+user_id+" and u2_id="+friend_id;
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				String sql1="update friendship set u2_nickname = '"+nickname+"' where u_id="+user_id;
				a=pstmt.executeUpdate(sql1);
			}else{
				String sql1="select * from friendship where u2_id="+user_id+" and u_id="+friend_id;
				 rs=pstmt.executeQuery(sql1);
				 if(rs.next()){
					 String sql2="update friendship set u_nickname = '"+nickname+"' where u2_id="+user_id;
						a=pstmt.executeUpdate(sql1);
				 }
			}
	        if(a==1){
	        	flag=true;
	        }
	        rs.close();
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		
		return flag;
	}

	/**
	 * @see 推荐好友，查找相似度高的好友 List<User> 取出5个
	 * @param user_id
	 * @return
	 */
	public static List<User> similar_friends(int user_id){
		Connection con=null;
		List<User> list_user=new ArrayList<User>();
		try {
			con=DBHandler.getConnection();
			String sql="select u2.u_id" +
					"from user_style as u1 join user_style as u2 on u1.s_id=u2.s_id and u1.u_id!=u2.u_id " +
					" group by u1.u_id having u1.u_id="+user_id+
					" order by ABS(u1.weight-u2.weight) desc limit 0,4";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				User newuser=getUserById(rs.getInt("u2.u_id"));
				list_user.add(newuser);
			}
			rs.close();
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
				con=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con=null;
		}
		return list_user;
	}
}
