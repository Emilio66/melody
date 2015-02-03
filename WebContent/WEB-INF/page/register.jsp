<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%
	String basePath=request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册</title>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
function add(item){
	alert(item.value);
}
function checkPassword(obj){
	var rePwd=obj.value;
	var pwd=$("#pwd").val();
	if(!rePwd==pwd){
		$("#remind").html("<font color='red'>密码不正确请重新输入</font>");
	}else{
		$("#remind").html("");
	}
}
</script>
</head>
<body>
<form action="/melody/register.action" method="post" enctype="multipart/form-data">
用户名：<input type="text" id="name" name="username">
密码：	<input type="password"  id="pwd" name="password"> 
重新输入密码：<input type="password" id="rePwd" onblur="checkPassword(this)">
<label id="remind"></label>
<s:file name="portrait" label="上传头像"></s:file>
选择您喜欢的音乐风格：<br>
<%-- <s:iterator value="#session.styleMap">
	<s:property value="key"/>
	<s:property value="value"/>
</s:iterator> --%>
<s:checkboxlist name="style" list="#session.styleMap" listKey="key" listValue="value" onclick="add(this)">
</s:checkboxlist>  
<s:debug></s:debug>
<s:fielderror></s:fielderror>
<input type="submit" value="确定"/>
</form>
</body>
</html>