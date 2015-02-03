package org.melody.controller;

import java.util.Map;

import org.melody.bean.User;
import org.melody.dao.FavoriteDAO;
import org.melody.dao.MusicDAO;
import org.melody.dao.UserDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport{

	private static final long serialVersionUID = 11328479L;

	@Override
	public String execute() throws Exception {
		Map<String, Object> sessionMap=ActionContext.getContext().getSession();
		sessionMap.put("user", null);
		return SUCCESS;
	}
	
}
