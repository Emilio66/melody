package org.melody.controller;

import org.melody.bean.User;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import com.opensymphony.xwork2.interceptor.Interceptor;

public class SecurityFilter implements Interceptor{

	private static final long serialVersionUID = 3245621231L;

	public void destroy() {		
	}

	public void init() {		
	}

	public String intercept(ActionInvocation invocation) throws Exception {
				
        User user=(User)ActionContext.getContext().getSession().get("user");
        String namespace = invocation.getProxy().getNamespace();  
        String actionName = invocation.getProxy().getActionName();  
        
        //不能把登陆，注册，注销给屏蔽了
		if(user!=null||("/".equals(namespace) && ("login".equals(actionName)
				||"register".equals(actionName)||"logout".equals(actionName)))){
			System.out.println("默认命名空间或已登录");
            return invocation.invoke();  
        } else {	
        	System.out.println("没有登陆进行拦截");			
			return "login";
        }
	}
}
