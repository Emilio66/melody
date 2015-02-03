package org.melody.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.melody.bean.User;
import org.melody.dao.MessageDAO;
import org.melody.dao.UserDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class FriendAction extends ActionSupport {

	private static final long serialVersionUID = 43516341L;
	private static List<User> friendList;
	private User user=(User)ActionContext.getContext().getSession().get("user");
	private List<User> result;
	/*private int friendId;
	private String friendName;
	private String nickname;
	*/
	
	
	public List<User> getFriendList() {
		return friendList;
	}

	public List<User> getResult() {
		return result;
	}

	public void setResult(List<User> result) {
		this.result = result;
	}

	public void setFriendList(List<User> friendList) {
		this.friendList = friendList;
	}

	/*public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
*/
	/**
	 * @see 初始化，获得好友列表
	 * @return
	 */
	public String init(){
		int user_id=user.getId();
		if((friendList=UserDAO.getFriendList(user_id))==null){
			this.addFieldError("noFriend", "您还没添加朋友");
			friendList=new ArrayList<User>();
		}
		HttpServletRequest request= ServletActionContext.getRequest();
		request.setAttribute("friendList", friendList);
		return SUCCESS;
	}
	
	/**
	 * @see 通过ID查找好友 
	 * @return
	 */
	public String queryById(){
		User user=null;
		HttpServletRequest request= ServletActionContext.getRequest();
		String  friendString=request.getParameter("friendId");
		int friendId=Integer.parseInt(friendString);
		if((user=UserDAO.getUserById(friendId))==null){
			this.addFieldError("noData", "没有此用户，请检查ID是否正确");
			System.out.println("no such user");
		}else{
			List<User> userList=new ArrayList<User>();
			userList.add(user);
			result=userList;//json 串返回
			request.setAttribute("queryResultList", userList);
			System.out.println("we get : "+result);
		}
		return SUCCESS;
	}
	/**
	 * @see 通过姓名查找好友 
	 * @return
	 */
	public String queryByName(){
		List<User> userList=new ArrayList<User>();
		HttpServletRequest request= ServletActionContext.getRequest();
		String friendName="";
		try {
			friendName = new String(request.getParameter("friendName").getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("不支持编码");
			e.printStackTrace();
		}

		if((userList=UserDAO.getUserByName(friendName))==null){
			this.addFieldError("noData", "没有此用户，请检查名字是否正确");
			System.out.println("no such user");
		}else{
			request.setAttribute("queryResultList", userList);
			result=userList;//json 串返回
			System.out.println("we get result: "+result);
		}
		return SUCCESS;
	}
	
	/**
	 * @see 请求添加对方为好友，好友状态设置为0，发出添加请求，等待好友同意，若同意则好友状态设置为1
	 * @return
	 */
	public String addFriendRequest() {
		int user_id = user.getId();
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] friendString = request.getParameterValues("add");
		System.out.println("选择了" + friendString.length + "个好友");
		for (int i = 0; i < friendString.length; i++) {
			int friendId = Integer.parseInt(friendString[i]);

			if (UserDAO.addFriendRequest(user_id, friendId)) {// 添加好友成功，向双方都发送消息
				User friend = UserDAO.getUserById(friendId);// 得到该用户的信息

				String addRequest = "你好，咱俩已经成为了好友了";
				MessageDAO.sendMessage(user_id, friendId, addRequest);
				String addResponse =  "你好，咱俩已经成为了好友，现在开始聊天吧";
				MessageDAO.sendMessage(friendId, user_id, addResponse);
				System.out.println(addRequest);

			} else {
				this.addFieldError("unknown", "添加失败");
			}
		}
		return SUCCESS;
	}
	
	/**
	 * @see 接受好友添加请求，将好友关系设置为1
	 * @return
	 */
	public String addFriendConfirm(){
		int user_id=user.getId();
		HttpServletRequest request= ServletActionContext.getRequest();
		String  friendString=request.getParameter("friendId");
		int friendId=Integer.parseInt(friendString);
		if(UserDAO.addFriendConfirmed(user_id, friendId)){
			User friend=UserDAO.getUserById(friendId);//得到该用户的信息
			String message1="恭喜！您和 [ "+friend.getName()+" ] 已经成为好友，可以开始聊天了。";
			String message2="恭喜！您和 [ "+user.getName()+" ] 已经成为好友，可以开始聊天了。";
			MessageDAO.sendMessage(user_id, friendId, message1);
			MessageDAO.sendMessage(friendId, user_id, message2);
		}else{
			this.addFieldError("unknown","添加失败");
		}
		return SUCCESS;
	}
	
	/**
	 * @see 解除好友关系
	 * @return
	 */
	public String deleteFriend(){
		int user_id=user.getId();
		HttpServletRequest request= ServletActionContext.getRequest();
		String  friendString=request.getParameter("friendId");
		System.out.print("friend to be delete: "+friendString);
		int friendId=Integer.parseInt(friendString);
		if(UserDAO.deleteFriend(user_id, friendId)){
			User friend=UserDAO.getUserById(friendId);
			addActionMessage("您已经和 [ "+friend.getName()+" ] 解除了好友关系");
			System.out.println("您已经和 [ "+friend.getName()+" ] 解除了好友关系");
			//刷新页面
			List<User> friendList=UserDAO.getFriendList(user_id);		
			request.setAttribute("friendList", friendList);
		}else{
			this.addFieldError("unknown","删除失败");
		}
		return SUCCESS;
	}
	
	/**
	 * @see 给好友发送消息
	 * @return
	 */
	public String sendMessage(){
		int user_id=user.getId();
		HttpServletRequest request= ServletActionContext.getRequest();
		String  friendString=request.getParameter("friendId");
		int friendId=Integer.parseInt(friendString);
		String content="";
		try {
			content = new String(request.getParameter("message").getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("不支持这种类型");
			e.printStackTrace();
		}
		System.out.println(user_id+" send to "+friendId+" :"+content);
		if(MessageDAO.sendMessage(user_id, friendId, content)){
			addFieldError("unknown", "发送消息失败");
		}
		return SUCCESS;
	}
	
	/**
	 * @see 更新好友的昵称
	 * @return
	 */
	public String updateNickname(){
		int user_id=user.getId();
		HttpServletRequest request= ServletActionContext.getRequest();
		String  friendString=request.getParameter("friendId");
		String nickname=request.getParameter("nickname");
		int friendId=Integer.parseInt(friendString);
		if(UserDAO.updateNickname(user_id, friendId, nickname)==false){
			this.addFieldError("unknown", "更新失败");
		}
		return SUCCESS;
	}
}
