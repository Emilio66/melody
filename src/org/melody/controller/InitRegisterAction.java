package org.melody.controller;

import java.util.Map;

import org.melody.dao.MusicDAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @see 初始化注册的时候的风格列表
 * @author zhaozy
 */
public class InitRegisterAction extends ActionSupport {

	private static final long serialVersionUID = 4378231L;
	private Map<Integer, String> styleMap;
	private String result;
	public Map<Integer, String> getStyleMap() {
		return styleMap;
	}
	public void setStyleMap(Map<Integer, String> styleMap) {
		this.styleMap = styleMap;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String execute() throws Exception {
		styleMap=MusicDAO.getStyleMap();
		if(styleMap!=null){
			System.out.println("获得音乐风格成功");
			ActionContext.getContext().getSession().put("styleMap", styleMap);
			return SUCCESS;
		}else {
			this.addFieldError("unknown", "无法加载页面，请检查网络连接");
			return ERROR;
		}
	}
}
