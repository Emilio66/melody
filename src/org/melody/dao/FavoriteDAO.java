package org.melody.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoriteDAO {
	/**
	 * 添加喜欢的歌曲
	 * @param user_id
	 * @param music_id
	 * @return
	 */
	public static void addFavorite(int user_id,int music_id){
		Connection con = null;
		try {
			con=DBHandler.getConnection();
			String sql="insert into favorites values ("+user_id+","+music_id+")";
			PreparedStatement pstmt = con.prepareStatement(sql);
			int rs=pstmt.executeUpdate(sql);//用户ID和音乐ID为联合主键，不能重复插入，省去检查的麻烦
			if(rs==1){//如果喜欢的是新歌
				int sid = 0;
				sql="select s_id from music where m_id="+music_id;	//找出歌曲风格
				ResultSet rs1=pstmt.executeQuery(sql);
				if(rs1.next()){
	               sid=rs1.getInt(1);	//插入新歌曲之后，更新用户的风格			
				}			
				addUserStyle(user_id, sid);//每次添加收藏之后计算一下用户的风格
				//update_user_style(user_id,con);
			}				
			
			pstmt.close();
			con.close();
			con = null;
		} catch (SQLException e) {
			System.out.println("数据重复了，不再重复插入");
		}finally {
			try {
				
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
			}
		}
	}
	
	
	/**
	 *  添加用户喜爱歌曲的风格
	 * @param user_id
	 * @param s_id
	 */
	public static void addUserStyle(int user_id,int s_id){
		String sql="insert into user_style values (?,?,0.1)";//user_id , style_id 是主键，当记录不存在时，无法插入
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			con=DBHandler.getConnection();
			pstmt=con.prepareStatement(sql);//预编译sql语句
		    pstmt.setInt(1, user_id);
		    pstmt.setInt(2, s_id);
			pstmt.executeUpdate();
		    /*float f=count_weight(user_id,s_id,con);
		    sql="update user_style set weight="+f+" where u_id="+user_id+
		    		" and s_id="+s_id;
		    pstmt.executeUpdate(sql);*/
		   
		} catch (SQLException e) {
			System.out.println("用户风格已存在，新加歌曲会增加用户喜欢改风格的权重");
			String sql2="select weight from user_style where u_id="+user_id+" and s_id="+s_id;
			try {
				ResultSet rSet=pstmt.executeQuery(sql2);
				float weight=0.0f;
				if(rSet.next()){
					weight=rSet.getFloat(1);
					weight+=0.1f;
					String sql3="update user_style set weight="+weight+" where u_id="+user_id+" and s_id="+s_id;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			
		}finally {
			try {				
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
			}
		}
	}
	
	/**
	 * 插入音乐风格及其比重，在注册时用到，会据此推荐歌曲
	 * @param user_id
	 * @param s_id
	 * @param weight
	 */
	public static void addUserStyleAndWeight(int user_id,int s_id,float weight){
		String sql="insert into user_style values (?,?,?)";//user_id , style_id 是主键，当记录不存在时，无法插入
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			con=DBHandler.getConnection();
			pstmt=con.prepareStatement(sql);//预编译sql语句
		    pstmt.setInt(1, user_id);
		    pstmt.setInt(2, s_id);
		    pstmt.setFloat(3, weight);
			pstmt.executeUpdate();
		    /*float f=count_weight(user_id,s_id,con);
		    sql="update user_style set weight="+f+" where u_id="+user_id+
		    		" and s_id="+s_id;
		    pstmt.executeUpdate(sql);*/
		   
		} catch (SQLException e) {
			System.out.println("用户风格已存在，新加歌曲会增加用户喜欢改风格的权重");
			String sql2="select weight from user_style where u_id="+user_id+" and s_id="+s_id;
			try {
				ResultSet rSet=pstmt.executeQuery(sql2);
				float weight2=0.0f;
				if(rSet.next()){
					weight2=rSet.getFloat(1);
					weight2+=0.1f;
					String sql3="update user_style set weight="+weight2+" where u_id="+user_id+" and s_id="+s_id;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}						
		}finally {
			try {				
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
			}
		}
	}
	
	
	/**
	 * @see 逐条更新用户喜欢的歌曲风格所占比重
	 * @param user_id,s_id
	 */
	public static void update_single_style(int user_id,int s_id,Connection con){
		float f=count_weight(user_id,s_id, con);//计算出这个风格的歌曲在所有歌曲中的比例，即这个风格歌曲的所占的比重
		String sql="update user_style set weight="+f+" where u_id="+user_id+
				" and s_id="+s_id;
		try {
			PreparedStatement pstmt=con.prepareStatement(sql);
		    pstmt.executeUpdate(sql);
		    pstmt.close();
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				System.out.println("无法关闭连接");
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * @see 重新计算用户的风格,根据用户收藏的歌曲的类型和数量，再考虑用户注册时选的风格，计算用户喜欢的音乐类型的比重
	 * @param user_id
	 */
	public static void update_user_style(int user_id,Connection con){
		try {
			String sql="select distinct s_id from favorites as f1 join music as m1 " +
					"on f1.m_id=m1.m_id group by u_id,f1.m_id having f1.m_id=?";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setInt(1,user_id);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				update_single_style(user_id,rs.getInt("s_id"),con);//逐条更新
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 计算用户喜欢的音乐类型所占比重
	 * @param user_id,s_id
	 */
	public static float count_weight(int user_id,int s_id,Connection con){
		//Connection con=null;
		int a=1,b=1;
		float f=0.0f;
		String sql="select count(*) as sum from favorites where u_id="+user_id;
		try {
			con=DBHandler.getConnection();
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs1=pstmt.executeQuery(sql);
			if(rs1.next()){
				b=rs1.getInt(1);
			}
			String sql1="select count(*) as sum1 from favorites join music on " +
					"favorites.m_id=music.m_id where u_id="+user_id+
					" and s_id="+s_id;
			pstmt=con.prepareStatement(sql1);
			rs1=pstmt.executeQuery(sql1);
			if(rs1.next()){
				a=rs1.getInt(1);
			}
			f=a/b*1.0f;	//两个整数除完之后是整数
			return f;
		} catch (SQLException e) {
			e.printStackTrace();
			return f;
		}
	}
	
	/**
	 * 删除喜欢的歌曲
	 * @param user_id
	 * @param music_id
	 * @return
	 */
	public static boolean removeFavorite(int user_id,int music_id){
		Connection con = null;
		boolean flag=false;
		try {
			con=DBHandler.getConnection();
			String sql="delete from favorites where u_id="+user_id+" and m_id="+music_id;
			PreparedStatement pstmt = con.prepareStatement(sql);
			int rs = pstmt.executeUpdate(sql);
			if(rs==1){
				flag=true;
			}
			/*int sid=0;
			sql="select s_id from music where m_id="+music_id;
			ResultSet rs1=pstmt.executeQuery(sql);
			if(rs1.next()){
               sid=rs1.getInt("s_id");				
			}*/
			update_user_style(user_id,con);//更新用户的风格
			pstmt.close();
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
			}
		}
		return flag;
	}
}
