package org.melody.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.melody.bean.Message;
import org.melody.bean.User;
import org.melody.dao.MessageDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class MessageAction extends ActionSupport{

	private static final long serialVersionUID = 464312141L;
	private List<Message> messageList;
	private User user=(User)ActionContext.getContext().getSession().get("user");
	private String count;
/*	private int friendId;
	private String content;*/
	
	public List<Message> getMessageList() {
		return messageList;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setMessageList(List<Message> messageList) {
		this.messageList = messageList;
	}

	/**
	 * @see 初始化页面，获得未读消息
	 * @return
	 */
/*	public String init(){
		int user_id=user.getId();
		if((messageList=MessageDAO.getUnreadMessage(user_id))==null){
			addActionMessage("没有未读消息");
		}
		return SUCCESS;
	}*/
	
	/**
	 * @see 给好友发送消息
	 * @return
	 */
	public String sendMessage(int friendId,String content){
		int user_id=user.getId();
		if(MessageDAO.sendMessage(user_id, friendId, content)){
			addFieldError("unknown", "发送消息失败");
		}
		return SUCCESS;
	}
	
	/**
	 * 
	 * @return 计算总共未读信息的条数
	 */
	public String unreadCount(){
		int user_id=user.getId();
		this.count=MessageDAO.countUnread(user_id)+"";
		//System.out.println("count="+count);
		return SUCCESS;
	} 
	
	/**
	 *  获取发送者与之对应的未读消息条数
	 * @return
	 */
	public String listUnread(){
		int user_id=user.getId();
		Map<User, Integer> messageMap=MessageDAO.userMessageCount(user_id);
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response=ServletActionContext.getResponse();
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		request.setAttribute("unreadMap", messageMap);
		return SUCCESS;
	}
	
	/**
	 * 
	 * @param senderId
	 * @return 获得某个用户发送的未读消息内容
	 */
	public String getUnreadMessage(){
		int user_id=user.getId();
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response=ServletActionContext.getResponse();
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int sender_id=Integer.parseInt(request.getParameter("senderId"));
		System.out.println(sender_id+" send unread message to "+user_id);
		
		if((messageList=MessageDAO.obtainUnreadMessage(sender_id, user_id))==null){
			addActionMessage("没有历史消息");
		}
		System.out.println("obtain message: "+messageList);
		
		return SUCCESS;
	}
	
	/**
	 * @see 获得某个用户与当前用户聊天的历史消息
	 * @return
	 */
	public String getHistory(){
		int user_id=user.getId();
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response=ServletActionContext.getResponse();
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int sender_id=Integer.parseInt(request.getParameter("senderId"));
		System.out.println(sender_id+" chatting with "+user_id);
		if((messageList=MessageDAO.getHistroyMessage(sender_id,user_id))==null){
			addActionMessage("没有历史消息");
		}
		System.out.println(messageList);
		return SUCCESS;  
	}
	
	/**
	 * 
	 * @return 标记为已读
	 */
	public String markAsRead(){
		int user_id=user.getId();
		HttpServletRequest request=ServletActionContext.getRequest();
		int sender_id=Integer.parseInt(request.getParameter("senderId"));
		MessageDAO.markAsRead(sender_id, user_id);
		System.out.println("标记为已读"+sender_id+" send to "+user_id);
		return SUCCESS;
	}
	
	
}
