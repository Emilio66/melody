package org.melody.dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.melody.bean.Comment;
public class RatingDAO {
	
	/**
	 * 添加评价
	 * @param user_id
	 * @param music_id
	 * @param star
	 * @param opinion
	 * @return
	 */
	public static boolean addRating(int user_id,int music_id,int star, String opinion){
		boolean flag=false;
		Connection con=null;
		try {
			 con=DBHandler.getConnection();
			String sql="insert into rating(m_id,u_id,star,opinion) values(?,?,?,?)";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, music_id);
			pstmt.setInt(2, user_id);
			pstmt.setInt(3, star);
			pstmt.setString(4, opinion);
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
	 * 返回歌曲的star
	 * @param music_id
	 * @return
	 */
	public static double getAverageStar(int music_id){
		double avg = 0;
		Connection con=null;
		try {
			con=DBHandler.getConnection();
			String sql="select avg(star) as avg_start" +
					"from rating group by m_id having m_id=?";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, music_id);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				avg=rs.getDouble("avg_start");
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
		return avg;
	}

	/**
	 * 
	 * @param music_id
	 * @return 返回用户评价de集合
	 */
	public static List<Comment> getComments(int music_id){
		List<Comment> resultList= new ArrayList<Comment>();
		Connection connection=null;
		try{
			connection=DBHandler.getConnection();
			String sql="select m_id,name,star,opinion,time from user join rating on user.u_id=rating.u_id where m_id=?";
			PreparedStatement pstmt=connection.prepareStatement(sql);
			pstmt.setInt(1, music_id);
			ResultSet rs=pstmt.executeQuery();
			Comment comment = null;
			while(rs.next()){
				comment=new Comment();
				comment.setUsername(rs.getString("name"));
				comment.setContent(rs.getString("opinion"));
				comment.setTime(rs.getString("time"));
				resultList.add(comment);
			}
			rs.close();
			pstmt.close();
			connection.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	} 
}
