package org.melody.controller;

import org.melody.bean.User;
import org.melody.dao.LoginDAO;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @see 判断用户登录是否正确，并进行相应的跳转
 * @author zhaozy
 */
public class LoginAction extends ActionSupport{

	private static final long serialVersionUID = 65651L;
	private String username;
	private String password;
	


	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String execute(){
		User user=null;
		ActionContext context=ActionContext.getContext();
		user=(User) context.getSession().get("user");
		//当用户没有登陆时才判断的用户是否用户密码是否正确
		if(user==null&&username!=null&&password!=null){
			if((user=LoginDAO.loadUser(username, password))!=null){
				context.getSession().put("user", user);
				System.out.println("成功登陆");
				return SUCCESS;
			}else{
				System.out.println("用户名或密码错误");
				addFieldError("inputWrong", "用户名或密码错误");
				return LOGIN;
			}
		}else{
			if(user!=null){
				return SUCCESS;
			}
			return LOGIN;
		}
	}
}
