package org.melody.controller;

import java.util.ArrayList;
import java.util.List;

import org.melody.bean.Music;
import org.melody.bean.User;
import org.melody.dao.FavoriteDAO;
import org.melody.dao.MusicDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class FavoriteAction extends ActionSupport{
	
	private static final long serialVersionUID = 65231L;
	private List<Music> favoriteList;
	
	public List<Music> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(List<Music> favoriteList) {
		this.favoriteList = favoriteList;
	}

	/**
	 * @see 加载用户喜欢的歌曲列表
	 * @return
	 */
	public String init(){
		User user=(User)ActionContext.getContext().getSession().get("user");
		int user_id=user.getId();
		favoriteList=MusicDAO.getFavoriteList(user_id);
		if(favoriteList==null){
			this.addFieldError("noFavorite", "没有喜欢的歌曲");
			favoriteList=new ArrayList<Music>();
		}
		return SUCCESS;
	}
	
	/**
	 * @see 移除用户喜欢的歌曲
	 * @return
	 */
	public String delete(){
		ActionContext context=ActionContext.getContext();
		User user=(User)context.getSession().get("user");
		int user_id=user.getId();
		int music_id=(Integer) context.get("music_id");
		if(FavoriteDAO.removeFavorite(user_id, music_id)==false)
			this.addFieldError("deleteFailed", "删除收藏的歌曲");
		return SUCCESS;
	}
}
