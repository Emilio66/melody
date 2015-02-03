<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
 import="org.apache.struts2.ServletActionContext,org.melody.bean.*,com.opensymphony.xwork2.ActionContext,java.util.*"
  %>
<%@taglib prefix="s" uri="/struts-tags" %>

<% String path=request.getContextPath(); //云上的网站不能随便访问文件%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>未读消息</title>
<link rel="stylesheet" type="text/css" href=" <%=path%>/css/music.css" />
<link rel="stylesheet" type="text/css" href=" <%=path%>/css/bootstrap.min.css" />
<script type="text/javascript" src=" <%=path%>/js/jquery.min.js"></script>
<script type="text/javascript" src=" <%=path%>/js/bootstrap.min.js"></script>
<script type="text/javascript">

var friendId;
$(document).ready( function(){
	
	//发送消息并将信息标记为已读
	$("#send_message_btn").click(function(){
		var message=$("#response").val();
		if(message!=null&&message.length>0){
			//var time=$.formatCurrentDate("yyyy-MM-dd hh:mm:ss");
			var data = new Date();  
	        var year = data.getFullYear();  //获取年
	        var month = data.getMonth() + 1;    //获取月
	        var day = data.getDate(); //获取日
	        var hours = data.getHours(); 
	        var minutes = data.getMinutes();
	        var seconds=data.getSeconds();
	        var time = year + "-" + month + "-" + day + " " + hours + ":" + minutes+":"+seconds;
			$("#dialog").append(time+" 你说：\r\n"+message+"\r\n\r\n");
			$("#response").val("");
			$.ajax({
				url: "<%=path%>/friend_sendMessage",
				data:{friendId:friendId,message:message},
				success: function (data){						
					//	alert("消息发送成功");			
				},
				error: function(data){
					alert("发送消息失败，请检查网络再重试");
				}				
			});
		}else{
			alert("发送内容不能为空");
		}		
	});
	
	
});

//获得要聊天的好友的ID,加载未读消息
function chatWith(obj){
	var id=obj.name; //获得消息发送者的ID
	var name=obj.value; //发送者的姓名
	friendId=id;
	
	//获取某个用户给当前用户的留言
	$.ajax({
		url:"<%=path%>/ajax/message_unread",
		data:{senderId:id,format:"json"},
		success: function(data){
			var dialog=$("#dialog");
			dialog.empty();
			//遍历所有消息，正确输出消息
			$.each(data,function(){
				var sender_id=this.sender_id;
				if(sender_id==id){
					dialog.append(name+" "+this.time+"\r\n");
					dialog.append(this.content+"\r\n \r\n");
				}else{
					dialog.append("你  "+this.time+"\r\n");
					dialog.append(this.content+"\r\n\r\n");
				}				
			});
		},
		error:function(data){
			alert("获取消息失败，请检查网络再重试");
		}
	});
	
	//标记为已读
	$("#"+id).empty();
	$.ajax({
		url:"<%=path%>/ajax/message_read",
		data:{senderId:id}
	});
}


//背景图片切换 	
function bgchange(sel){
	var index=0;
	if( sel == "b1" ){ index = 1;	}
	else if	( sel == "b2" ){ index = 2;	}
	else if	( sel == "b3" ){ index = 3;	}
	else if	( sel == "b4" ){ index = 4;	}
	document.getElementById("page").style.background = "url( <%=path%>/image/bg"+index+".jpg)";
	
}
//注销
function logout(){
	$("#logoutForm").submit();
}
</script>

</head>
<body id="page"> 

	<!--  好友消息列表 -->
		<div class="right1_box2_2" style="margin-top: 100px;margin-left: 400px; align: center;">
			<div align="center">
				<p>好友列表</p>
			</div>
			<div class="right_main2" >
					<s:iterator value="#request.unreadMap">
						<div style="height: 50px">
							<img class="top_right_t" style="float: left;" 
								src=" <%=path%>/image/<s:property value='key.portrait'/>" />
							<input type="button" style="float:left; width: 50%;" 
								class="btn btn-primary btn-lg btn-block" id="chatfriend" 
								data-toggle="modal" data-target="#frienddialog" name="<s:property value='key.id'/>"
								onclick="chatWith(this)"
								value="<s:property value='key.name'/>"
							/>
							<span id="<s:property value='key.id'/>" class='badge' style='background-color: red;'>
								<s:property value='value'/>
							</span>										
						</div>
					</s:iterator>								
			</div>
		</div>


<!--  用户头像，注销 -->	    
    <div class="top_right">
    	<!-- 注销按钮 -->
        <img class="top_right_logout" src=" <%=path%>/image/qt_off.png" onmouseover="src=' <%=path%>/image/qt_on.png'"
        		onmouseout="src=' <%=path%>/image/qt_off.png'" onclick="logout()"/>
        <!-- 隐藏的注销表单 -->
        <form action="<%=path%>/logout.action" id="logoutForm" style="display:'none'"></form> 
        <!-- 用户头像和姓名  -->
        <div class="top_right_user">
			<img id="touxiang" class="top_right_t" src=" <%=path%>/image/<s:property value='#session.user.portrait'/>"  style="align: center;"/>
			<label id="username" class="top_right_y" style="padding-left: 0px;"><s:property value='#session.user.name'/></label> 			
		</div>
		<!-- 未读消息 -->
		<div  id="unread" class="top_right_message">
		<!-- 返回按钮-->
			<a href="<%=path%>/main!next" style="float: left;margin-top: 5px;; margin-left: 20px;">
				<span class="glyphicon glyphicon-circle-arrow-left"  style="font-size: 30px;"></span>
			</a>
		</div>
			
    </div>
    
    
<!-- 聊天对话框 -->
		<div class="modal fade" id="frienddialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  			<div class="modal-dialog">
    			<div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        				<h4 class="modal-title" id="myModalLabel">聊天</h4>
      				</div>
      				<div class="modal-body">
        				<textarea id="dialog" class="form-control" rows="10" readonly="readonly" style="resize: none"></textarea>
      				</div>
      				<div class="modal-footer">
      					<textarea id="response" class="form-control" rows="2" style="width: 60%; resize: none" ></textarea>
        				<button id="send_message_btn" type="button" class="btn btn-primary">发送</button>
        				<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>  				
      				</div>
    			</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
		
		
<!--  选择背景 -->	    
    <ul>
    		<li class="rBg" style="background:url( <%=path%>/image/bgdot_1.png); float: auto; margin-top: 10px;" id="b1" onclick="bgchange('b1')"></li>
    		<li class="rBg" style="background:url( <%=path%>/image/bgdot_2.png); float: auto; margin-top: 10px;" id="b2" onclick="bgchange('b2')"></li>
    		<li class="rBg" style="background:url( <%=path%>/image/bgdot_3.png); float: auto; margin-top: 10px;" id="b3" onclick="bgchange('b3')"></li>
    		<li class="rBg" style="background:url( <%=path%>/image/bgdot_4.png); float: auto; margin-top: 10px;" id="b4" onclick="bgchange('b4')"></li>
    </ul>
    
</body>
</html>