package org.melody.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

import org.melody.bean.Music;


public class MusicDAO {
	
	/**
	 * @see 未登录用户，从数据库中获取歌曲
	 * @return
	 */
	public static List<Music> getMusicList(){
		Connection con=null;
		List <Music> lm=new ArrayList<Music>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from music limit 0,29";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Music mu=new Music();
				mu.setArtist(rs.getString("artist"));
				mu.setId(rs.getInt("m_id"));
				mu.setName(rs.getString("name"));
				mu.setOwner(rs.getInt("owner"));
				mu.setPath(rs.getString("path"));
				mu.setS_id(rs.getInt("s_id"));
				lm.add(mu);
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
		return  lm;
	}
	
	/**
	 * @see 登录用户，从数据库中获取与他风格类似的歌曲
	 * @return 推荐歌曲列表
	 */
	public static List<Music> getMusicList(int user_id){
		Connection con=null;
		List <Music> lm=new ArrayList<Music>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from music join user_style on " +
					"music.s_id=user_style.s_id where u_id=? and weight>0.2";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			ResultSet rs=pstmt.executeQuery();			
			while(rs.next()){
				Music mu=new Music();
				mu.setArtist(rs.getString("artist"));
				mu.setId(rs.getInt("m_id"));
				mu.setName(rs.getString("name"));
				mu.setOwner(rs.getInt("owner"));
				mu.setPath(rs.getString("path"));
				mu.setS_id(rs.getInt("s_id"));
				lm.add(mu);
			}
			rs.close();
			pstmt.close();
			con.close();
			con=null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  lm;
	}
	/**
	 * @see 登录用户，从数据库中获取与他喜欢的歌曲
	 * @return
	 */
	public static List<Music> getFavoriteList(int user_id){
		Connection con=null;
		List <Music> lm=new ArrayList<Music>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from music join favorites on " +
					"music.m_id=favorites.m_id where u_id="+user_id;
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Music mu=new Music();
				mu.setArtist(rs.getString("artist"));
				mu.setId(rs.getInt("m_id"));
				mu.setName(rs.getString("name"));
				mu.setOwner(rs.getInt("owner"));
				mu.setPath(rs.getString("path"));
				mu.setS_id(rs.getInt("s_id"));
				lm.add(mu);
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
		return  lm;
	}
	/**
	 * @see 获得音乐by id
	 */
	public static Music getMusic(int music_id){
		Connection con=null;
		Music mu=null;
		try {
			con=DBHandler.getConnection();
			String sql="select * from music where m_id="+music_id;
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				mu=new Music();
				mu.setArtist(rs.getString("artist"));
				mu.setId(rs.getInt("m_id"));
				mu.setName(rs.getString("name"));
				mu.setOwner(rs.getInt("owner"));
				mu.setPath(rs.getString("path"));
				mu.setS_id(rs.getInt("s_id"));
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
		return mu;
	}
	
	/**
	 * @see 获得音乐by name
	 */
	public static List<Music> getMusicByName(String name){
		Connection con=null;
		Music mu=null;
		List<Music> searchList=new ArrayList<Music>();
		try {
			con=DBHandler.getConnection();
			String sql="select * from music where name like '%"+name+"%'";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			
			while(rs.next()){
				mu=new Music();
				mu.setArtist(rs.getString("artist"));
				mu.setId(rs.getInt("m_id"));
				mu.setName(rs.getString("name"));
				mu.setOwner(rs.getInt("owner"));
				mu.setPath(rs.getString("path"));
				mu.setS_id(rs.getInt("s_id"));
				searchList.add(mu);	//添加音乐
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
		return searchList;
	}
	
	/**
	 * 获得所有音乐的风格
	 */
	public static Map <Integer,String> getStyleMap(){
	    Map<Integer,String> styletable=new HashMap<Integer,String>();
	    Connection con=null;
		try {
			con=DBHandler.getConnection();
			String sql="select * from style";
			PreparedStatement pstmt=con.prepareStatement(sql);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				int id=rs.getInt("s_id");
				String name=rs.getString("name");
				styletable.put(id, name);
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
		return styletable;
	}
}
