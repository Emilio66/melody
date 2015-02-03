package org.melody.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melody.bean.Message;
import org.melody.bean.User;



public class MessageDAO {
	/** 
	 * @param user_id
	 * @return 获得所有未读信息
	 */
	public static List<Message> getUnreadMessage(int user_id){
		Connection con=null;
		List<Message> list_mes=new ArrayList<Message>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from message where receiver_id="+user_id+
					" and status='0'";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Message me=new Message();
				me.setContent(rs.getString("message"));
				me.setSender_id(rs.getInt("sender_id"));
				me.setStatus('0');
				me.setTime(rs.getTimestamp("time").toString());
				list_mes.add(me);
			}
			rs.close();
			pstmt.close();
			con.close();
			con =null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list_mes;
	}
	
	/**
	 * 
	 * @param sender_id
	 * @param receiver_id
	 * @return 获得两个用户聊天的消息
	 */
	public static List<Message> getHistroyMessage(int s_id,int r_id){
		List <Message> mes=new ArrayList<Message>();
		Connection con =null;
		try {
			con=DBHandler.getConnection();
			String sql =   "select * from message where (sender_id="
					+s_id+" and receiver_id="+r_id+") or (sender_id="
					+r_id+" and receiver_id="+s_id+") order by time";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery(sql);
			while(rs.next()){
				Message me=new Message();
				me.setContent(rs.getString("message"));
				me.setSender_id(rs.getInt("sender_id"));
				me.setStatus(rs.getString("status").charAt(0));//返回状态
				me.setTime(rs.getTimestamp("time").toString());
				mes.add(me);
			}
			rs.close();
			pstmt.close();
			con.close();
			con =null;
		} catch (SQLException  e) {
			e.printStackTrace();
		}
		return mes;
	}
	
	/**
	 * 
	 * @param senderId
	 * @return 获得单个用户给当前用户发送的未读消息
	 */
	public static List<Message> obtainUnreadMessage(int senderId,int receiverId){
		List <Message> mes=new ArrayList<Message>();
		Connection con =null;
		try {
			con=DBHandler.getConnection();
			String sql =   "select * from message where (sender_id="
					+senderId+" and receiver_id="+receiverId+") and status='0' order by time";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery(sql);
			while(rs.next()){
				Message me=new Message();
				me.setContent(rs.getString("message"));
				me.setSender_id(rs.getInt("sender_id"));
				me.setStatus(rs.getString("status").charAt(0));//返回状态
				me.setTime(rs.getTimestamp("time").toString());
				mes.add(me);
			}
			rs.close();
			pstmt.close();
			con.close();
			con =null;
		} catch (SQLException  e) {
			e.printStackTrace();
		}
		return mes;
	}
	
	
	/**
	 * 
	 * @param user_id
	 * @return 返回每个好友的信息和他发送的消息
	 */
	public static Map<User, Integer> userMessageCount(int user_id){
		Map<User, Integer> messageMap=new HashMap<User, Integer>();
		Connection con=null;
		try {
			con=DBHandler.getConnection();
			String sql="select u_id,name,portrait,count(*) as num from message join user on sender_id=u_id where receiver_id="
						+user_id+" and status='0' group by sender_id";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				User user=new User();
				user.setId(rs.getInt("u_id"));
				user.setName(rs.getString("name"));
				user.setPortrait(rs.getString("portrait"));
				messageMap.put(user, rs.getInt("num"));
			}
			rs.close();
			pstmt.close();
			con.close();
			con =null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return messageMap;
	}
	
	/**
	 * 
	 * @param sender_id
	 * @param receiver_id
	 * @return 标记为已读消息
	 */
	public static boolean markAsRead(int sender_id,int receiver_id){
		Connection connection=null;
		boolean flag=false;
		
		try {
			connection=DBHandler.getConnection();
			String sql="update message set status='1' where sender_id="+sender_id+" and receiver_id="+receiver_id;
			Statement stmt=connection.createStatement();
			int count=stmt.executeUpdate(sql);
			if(count>0)
				flag=true;
			stmt.close();
			connection.close();
			connection =null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	public static int countUnread(int user_id){
		int count=0;
		Connection connection=null;
		
		try {
			connection=DBHandler.getConnection();
			String sql="select count(*) from message where receiver_id="+user_id+" and status='0'";
			Statement stmt=connection.createStatement();
			ResultSet rSet=stmt.executeQuery(sql);
			if(rSet.next()){
				count=rSet.getInt(1);//获得数目
			}
			System.out.println("message unread: "+count);
			rSet.close();
			stmt.close();
			connection.close();
			connection =null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * 
	 * @param sender_id
	 * @param receiver_id
	 * @param content
	 * @return 发送信息
	 */
	public static boolean sendMessage(int sender_id,int receiver_id,String content){
		Connection con =null;
		boolean flag=false;
		try {
			int a=0;
			con=DBHandler.getConnection();			
			String sql="insert into message(sender_id,receiver_id,message) values("+sender_id+","+receiver_id+",'"+content+"')";
			PreparedStatement pstmt=con.prepareStatement(sql);
			//pstmt.setInt(1, sender_id);
			//pstmt.setInt(2, receiver_id);
			//pstmt.setString(3, content);
			a=pstmt.executeUpdate(sql);
			if(a==1){
				flag=true;
			}
			pstmt.close();
			con.close();
			con =null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 
	 * @param sender_id
	 * @param receiver_id
	 * @param content
	 * @param pstmt
	 * @return 群发信息的基础
	 */
	public static boolean sendMessage2(int sender_id,int receiver_id,String content,PreparedStatement pstmt){
		boolean flag=false;
		try {
			int a=0;		
			pstmt.setInt(1, sender_id);
			pstmt.setInt(2, receiver_id);
			pstmt.setString(3, content);
			a=pstmt.executeUpdate();
			if(a==1){
				flag=true;
			}
		} catch (SQLException e) {
			System.out.println("发送消息2出现异常");
			e.printStackTrace();
		}
		
		return flag;
		}
	
	/**
	 * 群发消息，也可用于好歌分享给好友
	 * @param sender_id
	 * @param receivers
	 * @param content
	 * @return
	 */
	public static boolean sendMassMessage(int sender_id,ArrayList<Integer> receivers,String content){
		int i=0;
		boolean flag=true;
		Connection connection=null;
		try {
			connection=DBHandler.getConnection();
			String sql="insert into message(sender_id,receiver_id,message) values (?,?,?)";
			PreparedStatement pstmt=connection.prepareStatement(sql);
			for(i=0;i<receivers.size();i++){
				if(sendMessage2(sender_id,receivers.get(i).intValue(),content, pstmt)==false){
					flag=false;
					break;
				}
			}
			pstmt.close();
			connection.close();
			connection=null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
}
