package org.melody.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.melody.bean.User;
import org.melody.dao.FavoriteDAO;
import org.melody.dao.MusicDAO;
import org.melody.dao.UserDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @see 收集用户输入的用户名、密码、上传的头像并做相关的验证，然后获得用户选择的音乐风格，并插入数据库
 * @author zhaozy
 */
public class RegisterAction extends ActionSupport{
	
	private static final long serialVersionUID = 321345L;
	private String username;
	private String password;
	private String[] style;//用户喜欢的音乐风格，可以通过用户收藏的歌曲间接得到他的风格
	
	//处理上传组件需要的参数
	private File portrait;//用户头像文件
	private String portraitContentType;//头像类型
	private String portraitFileName;//图片的名字
	//在struts.xml中配置的参数
	private String savePath;//保存所有头像的文件夹
	private String allowTypes;//允许存储的文件类型
	private String maxSize;//文件最大尺寸
	private String defaultImage;//默认头像
	
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
	
	public String[] getStyle() {
		return style;
	}
	public void setStyle(String[] style) {
		this.style = style;
	}
	public File getPortrait() {
		return portrait;
	}
	public void setPortrait(File portrait) {
		this.portrait = portrait;
	}
	public String getPortraitContentType() {
		return portraitContentType;
	}
	public void setPortraitContentType(String portraitContentType) {
		this.portraitContentType = portraitContentType;
	}
	public String getPortraitFileName() {
		return portraitFileName;
	}
	public void setPortraitFileName(String portraitFileName) {
		this.portraitFileName = portraitFileName;
	}
	public String getSavePath() {
		return ServletActionContext.getServletContext().getRealPath(savePath);
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getAllowTypes() {
		return allowTypes;
	}
	public void setAllowTypes(String allowTypes) {
		this.allowTypes = allowTypes;
	}
	public String getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}	
	public String getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
	}
	
	@Override
	public String execute() throws IOException
	{
		if(UserDAO.isNameValid(username)){
			System.out.println("用户名有效~");
			//判断是否上传了头像，则将默认头像覆盖
			if(portrait!=null&&portraitContentType!=null){
				//defaultImage=System.currentTimeMillis()+portraitFileName;
				defaultImage=portraitFileName;
				System.out.println("file name:"+defaultImage);
					//以服务器的文件保存地址和时间戳建立上传文件输出流
				FileOutputStream fos = new FileOutputStream(new File(getSavePath()+ "\\" + defaultImage));
				FileInputStream fis = new FileInputStream(portrait);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) > 0)
				{
					fos.write(buffer , 0 , len);
				}
				fos.close();
				fis.close();
			}
			
			User user=new User();
			user.setName(username);
			user.setPassword(password);
			user.setPortrait(defaultImage);//如果上传了头像，则defaultImage会改变
			if((user=UserDAO.insertUser(user))!=null){
				ActionContext.getContext().getSession().put("user", user);//注册成功将用户加入session中
				int user_id=user.getId();
				System.out.println("style: "+style);
				if(style!=null&&style.length>0){//将用户喜欢的风格存入数据库
					float weight=1.0f/style.length;//按照用户喜欢的所有风格，给每个风格一个平均分
					for(String temp : style){
						FavoriteDAO.addUserStyleAndWeight(user_id, Integer.parseInt(temp),weight);
					}
				}
			}else{
				addFieldError("error", "注册出现异常，请检查网络");
				return ERROR;
			}
			return SUCCESS;//返回主页听音乐，以后有时间就把用户推荐页面做了，让他可以添加几个好友 
		}else{
			addFieldError("invalid", "抱歉用户名已经存在");
			return ERROR;
		}
	}

	/**
	 * 过滤文件类型
	 * @param types 系统所有允许上传的文件类型
	 * @return 如果上传文件的文件类型允许上传，
	 *		 返回null，否则返回error字符串
	 */
	public String filterTypes(String[] types)
	{ 
		//获取希望上传的文件类型
		String fileType = getPortraitContentType();
		System.out.println("fileType: "+fileType);
		if(fileType!=null){
			for (String type : types)
			{
				if (type.equals(fileType))
				{
					return "valid_image";
				}
			}	
		}else{
			return "all_right";
		}
		return ERROR;
	}

	//执行输入校验
	public void validate()
	{
		//将允许上传文件类型的字符串以英文逗号（,）
		//分解成字符串数组从而判断当前文件类型是否允许上传
		String filterResult = filterTypes(getAllowTypes().split(","));
		//如果当前文件类型不允许上传
		if (filterResult.length()<6)
		{
			//添加FieldError
			addFieldError("upload" , "您要上传的文件类型不正确！");
		}else if(portrait!=null&& portrait.length()>Integer.parseInt(maxSize)){
			addFieldError("upload", "您上传的文件过大！");
		}
	}
	
}
