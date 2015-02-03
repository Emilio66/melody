package org.melody.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.melody.bean.Comment;
import org.melody.bean.Music;
import org.melody.bean.User;
import org.melody.dao.FavoriteDAO;
import org.melody.dao.MessageDAO;
import org.melody.dao.MusicDAO;
import org.melody.dao.RatingDAO;
import org.melody.dao.UserDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @see 1.进入页面，需要初始化，先获得音乐列表（随便听听），下一曲由javascript实现
	    2.开始听歌，页面有3个动作用户可以使用（收藏、分享、评价）全都是异步请求，使用AJAX
	    3.下一步：推荐听听（修改音乐列表即可）
 * @author zhaozy
 */

public class MainAction extends ActionSupport{

	private static final long serialVersionUID = 1178653L;
	
	private static List<Music> playlist;//最重要的播放列表，每次取出30首
	//private static List<Music> hotList;//热门排行
	private static List<Music> recommandList;//推荐用户喜欢的音乐
	private static List<Music> favoriteList;//用户收藏的音乐
	private static List<User> friendList;
	private static int index=0; 
	
	private static Music currentMusic;	
	private  List<Comment>commentList;
	private  List<Music> searchList;
	private Integer star=5;			//评价的星级，在页面中必须写上默认值5
	private String opinion;		//用户完整评价
	private String content;
	private String isFavorite;
	
	/**
	 * @see 页面初始化
	 * @return main.jsp
	 */
	public String init(){
		System.out.println("开始初始化..");
		if((playlist=MusicDAO.getMusicList())==null){//get play list from database
			addFieldError("noMusic", "抱歉，没有可以播放的音乐");
			System.out.println("抱歉，没有可以播放的音乐");
		}
		
		int user_id=((User)ActionContext.getContext().getSession().get("user")).getId();	
		
		if((recommandList=MusicDAO.getMusicList(user_id))==null){//获得推荐的音乐列表
			addFieldError("noMusic", "没有推荐的音乐");
			System.out.println("抱歉，没有推荐的音乐");
		}
		
		this.next();	//下一曲，刷新评价列表和收藏列表！！！
		
		return SUCCESS;
	} 
	/**
	 * @see next music
	 * @return 负责播放下一曲，并刷新
	 */
	public String next(){
		if(index==playlist.size()){//已经是最后一曲了，下一曲就从头播放
			index=0;
		}
		currentMusic=playlist.get(index++);	//第一曲从0开始	
		
		//刷新页面中会变的部分
		refresh();
		
		return SUCCESS;
	}
	
	/**
	 * @see 专门负责刷新页面
	 */
	public void refresh(){
		int user_id=((User)ActionContext.getContext().getSession().get("user")).getId();		
		
		System.out.println("currentMusic: "+currentMusic);
		
		commentList=RatingDAO.getComments(currentMusic.getId());//获得正在播放的歌曲的评价				
		favoriteList=MusicDAO.getFavoriteList(user_id);
		friendList=UserDAO.getFriendList(user_id);
		
		if(favoriteList.contains(currentMusic)){
			System.out.println("current music is favorited by user");
			isFavorite="true";
		}else {
			System.out.println("no such favorite music");
			isFavorite="false";
		}
		
		HttpServletRequest request= ServletActionContext.getRequest();
		HttpServletResponse response=ServletActionContext.getResponse();
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("isFavorite", isFavorite);
		request.setAttribute("currentMusic",currentMusic);
		request.setAttribute("commentList",commentList);
		request.setAttribute("recommandList", recommandList);
		request.setAttribute("favoriteList", favoriteList);
		request.setAttribute("friendList", friendList);	
	}
	
	
	/**
	 * @see play choosed music
	 */
	public String play(){
		HttpServletRequest request= ServletActionContext.getRequest();
		String musicString=request.getParameter("music_id");
		System.out.println("music_id="+musicString);
		if(musicString!=null&&musicString.length()>0){
			int music_id=Integer.parseInt(musicString);//当前播放音乐
			System.out.println("music_id="+music_id);
			if((currentMusic=MusicDAO.getMusic(music_id))==null){
				addFieldError("unknown", "未找到播放的资源");
				System.out.println("未找到播放的资源");
			}
		}		
		////刷新页面中会变的部分
		refresh();
		
		return SUCCESS;
	}
	/**
	 * @see 用户评价
	 * @return 主页
	 */
	public String remark(){
		ActionContext context=ActionContext.getContext();
		User user=(User)context.getSession().get("user");
		int user_id=user.getId();	
		int music_id=currentMusic.getId();//当前播放音乐	
		System.out.println("music_id="+music_id);
		try {
			opinion=new String(opinion.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(RatingDAO.addRating(user_id, music_id, star, opinion))
			return SUCCESS;
		else {
			addFieldError("ratingFailed", "评价音乐失败");
			System.out.println("评价音乐失败");
			return ERROR;
		}
	}
	
	/**
	 * @see 用户分享歌曲
	 * @return
	 */
	public String share(){
		ActionContext context=ActionContext.getContext();
		User user=(User)context.getSession().get("user");
		int user_id=user.getId();
		
		try {
			content=new String(content.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("message: "+content);
		ArrayList<Integer> receivers=(ArrayList<Integer>) UserDAO.getFriendIds(user_id);//get friends id
		if(receivers!=null&&MessageDAO.sendMassMessage(user_id, receivers, content)){//给每个好友都发分享消息
			System.out.println("发送成功~");		
		}
		else {
			addFieldError("shareFailed", "您没有好友，去查找好友或让系统推荐吧");
			System.out.println("无好友可以分享");
		}
		return SUCCESS;
	}
	
	/**
	 * @see 用户收藏音乐
	 * @return
	 */
	public String favorite(){
		ActionContext context=ActionContext.getContext();
		User user=(User)context.getSession().get("user");
		int user_id=user.getId();
		int music_id=currentMusic.getId();//当前播放音乐
		System.out.println("music_id="+music_id);
		FavoriteDAO.addFavorite(user_id, music_id);
		isFavorite="true";
		
		return SUCCESS;
	}
	
	/**
	 * 
	 * @return 搜索音乐列表
	 */
	public String search(){
		//获得搜索内容
		try {
			content=new String(content.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		searchList=MusicDAO.getMusicByName(content);
		System.out.println(content+" 对应的搜索结果： "+searchList);
		return SUCCESS;
	}
	
	//getter and setter 将会在自动映射的时候用到
	public List<Music> getSearchList() {
		return searchList;
	}
	public void setSearchList(List<Music> searchList) {
		this.searchList = searchList;
	}
	public String getContent() {
		return content;
	}	
	
	public static List<Music> getFavoriteList() {
		return favoriteList;
	}
	public static void setFavoriteList(List<Music> favoriteList) {
		MainAction.favoriteList = favoriteList;
	}
	public static List<User> getFriendList() {
		return friendList;
	}
	public static void setFriendList(List<User> friendList) {
		MainAction.friendList = friendList;
	}
	public static List<Music> getRecommandList() {
		return recommandList;
	}
	public static void setRecommandList(List<Music> recommandList) {
		MainAction.recommandList = recommandList;
	}
	public List<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	public  Music getCurrentMusic() {
		return currentMusic;
	}
	public  void setCurrentMusic(Music currentMusic) {
		this.currentMusic = currentMusic;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Music> getPlaylist() {
		return playlist;
	}
	public void setPlaylist(List<Music> playlist) {
		MainAction.playlist = playlist;
	}
	public int getStar() {
		return star;
	}
	public void setStar(String star) {
		this.star = Integer.parseInt(star);
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public String getIsFavorite() {
		return isFavorite;
	}
	public void setIsFavorite(String isFavorite) {
		this.isFavorite = isFavorite;
	}
	
	
}
