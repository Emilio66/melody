<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<% String path=request.getContextPath(); //云上的网站不能随便访问文件，直接找到根目录  %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登陆</title>

<link rel="stylesheet" type="text/css" href=" <%=path%>/css/welcome.css"/>
<script type="text/javascript" src="<%=path%>/js/jquery.min.js"> </script>
<script type="text/javascript">

	var message;	
	var isMatch=true;
	$(document).ready(function(){
		
		//输入密码完成按回车即可响应
		$('#password').keydown(function(event) {
			if (event.keyCode == 13) {
				submitLogin();//调用submitLogin函数
			}
		});
		//登陆与注册是滑动效果
		$("#log").click(function(){
			$(".box_l").animate({bottom:300},600);
			$(".box_r").animate({bottom:-400},600);
		});
		$("#reg").click(function(){
			$(".box_l").animate({bottom:-300},600);
			$(".box_r").animate({bottom:240},600);			
		});
		
		
	});
	
	//登陆验证
	function submitLogin(){
		var name=$.trim($("#username").val());//取出前后空格
		var pwd=$.trim($("#password").val());
		
		if(name!=null&&name.length>0&&pwd!=null&&name.length>0){
			$("#username").val(name);
			$("#password").val(pwd);
			$("#loginForm").submit();
		}else{
			alert("用户名或密码不能为空");
		}
	}
	
	//检查注册的时候是否正确
	function checkPwd(){
		var pwd1=$("#pwd1").val();
		var pwd2=$("#pwd2").val();
		if(pwd1!=pwd2){
			isMatch=false;
			message="两次密码不一致，请修改";
			alert(message);
			return;
		}else{
			isMatch=true;
		}
	}
	
	function check(){
		if(isMatch==false){
			alert(message);
			return;
		}
		$("#registerForm").submit();
	}
	
	//登陆与注册按钮图片切换 	
	function changeToLog( obj ){		
		document.getElementById("log").src = ' <%=path%>/image/login_on.png';
		document.getElementById("reg").src = ' <%=path%>/image/regis_off.png';
	}
	function changeToReg( obj ){		
		document.getElementById("log").src = ' <%=path%>/image/login_off.png';
		document.getElementById("reg").src = ' <%=path%>/image/regis_on.png';
	}
	</script>
</head>
<body class="bg">
<!--  登陆！ -->
	<div class="box box_l" >
         
     <form action="<%=path%>/login" id="loginForm" method="post"><br />
		<input type="text" name="username" id="username" class="txt txt_l" class="width:220px" />
		<input type="password" name="password" id="password" class="txt txt_l" class="width:220px"/>
		<%-- <s:token></s:token> --%> <!-- 防止重复提交 -->
		<input type="button" class="btn btn_l"  value="登陆" onclick="submitLogin()"/>
	</form>
	<s:fielderror fieldName="inputWrong"> <!-- 输出错误信息 -->
     </s:fielderror>
     <s:fielderror>
        <s:param>unknown</s:param>
        <s:param>upload</s:param>
        <s:param>invalid</s:param>
        <s:param>error</s:param>
       </s:fielderror>
    </div>
<!--  注册 -->    
    <div class="box box_r">
        <form action="<%=path%>/register" id="registerForm" method="post" enctype="multipart/form-data">   	
            <div class="item">
            	<label class="item_1 font">用户名</label>
                <input type="text" class="txt item_2" id="username" 
                	name="username" maxlength="10"/>
            </div>
            <div class="item">
            	<label class="item_1 font">密码</label>
                <input type="password" class="txt item_2" name="password" id="pwd1"/>
            </div>
            <div class="item">
            	<label class="item_1 font">确认密码</label>
                <input type="password" class="txt item_2" id="pwd2" onblur="checkPwd()"/>
            </div>
            <div class="item">
			<label class="item_1 font">上传头像</label>
                <s:file cssClass="item2" cssStyle="width:200px;padding-top:8px" name="portrait" label=""></s:file>
            </div>
            <div class="item">
            	<label class="item_1 font">音乐风格&nbsp;&nbsp;</label>
              <s:checkboxlist cssStyle="width:25px" name="style" list="#{1:'Pop',2:'Rock',3:'Folk ',4:'Country',5:'Light',6:'R&B'}" listKey="key" listValue="value">
			 </s:checkboxlist>
            </div>
            <br />
            <div class="item">            	
            	<input id="login" class="btn btn_l" type="submit" onclick="check()" value="立即注册" />
            </div>
        </form>
        <s:fielderror>
        		<s:param>unknown</s:param>
        		<s:param>upload</s:param>
        		<s:param>invalid</s:param>
        		<s:param>error</s:param>
        </s:fielderror>
    </div>
<!--  登陆与注册按钮 -->
    <div style="text-align:center;position:absolute;left:46%;bottom:20%;">
    	<img id="log" class="ch" style="filter:alpha(opacity=50,Style=0);" src=" <%=path%>/image/login_on.png" onclick="changeToLog(this)"/>
        <img id="reg" class="ch" src=" <%=path%>/image/regis_off.png" onclick="changeToReg(this)"/>
    </div>
</body>

</html>