<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
 import="org.melody.bean.*,com.opensymphony.xwork2.ActionContext,java.util.*"
  %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%
	User user=(User)session.getAttribute("user");
	String portrait=user.getPortrait();
	String username=user.getName();
	//Music nowMusic =(Music)request.getAttribute("currentMusic");//get current music
	Map<User,Integer> unreadMap=(Map<User,Integer>)request.getAttribute("unreadMap");
	System.out.println("unread map's list: "+unreadMap.keySet());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>消息</title>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
var friendId;
function chatWith(obj){
	var id=obj.name; //获得消息发送者的ID
	friendId=id;
	var name=$("#"+id).text(); //发送者的姓名
	//alert(id+":"+name);
	//获取这个用户与当前用户的聊天信息，标记为已读
	$.ajax({
		url:"/melody/ajax/message_chat",
		data:{senderId:id,format:"json"},
		success: function(data){
			var dialog=$("#dialog");
			dialog.empty();
			//遍历所有消息，正确输出消息
			$.each(data,function(){
				var sender_id=this.sender_id;
				if(sender_id==id){
					dialog.append(name+" "+this.time+"<br>");
					dialog.append(this.content+"<br><br>");
				}else{
					dialog.append("你 "+this.time+"<br>");
					dialog.append(this.content+"<br><br>");
				}
				$("#chatBox").show();
			});
		},
		error:function(data){
			alert("获取消息失败，请检查网络再重试");
		}
	});
}

function sendMessage(){
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
		$("#dialog").append("你 " +time+" <br>"+message+"<br><br>");
		$("#response").val("");
		$.ajax({
			url: "/melody/friend_sendMessage",
			data:{friendId:friendId,message:message},
			success: function (data){						
					alert("消息发送成功");			
				},
			error: function(data){
				alert("发送消息失败，请检查网络再重试");
			}				
		});
	}else{
		alert("发送内容不能为空");
	}		
}

</script>
</head>
<body>
<table border="1" width="800">
<thead>
<tr>
<td>头像</td>
<td>昵称</td>
<td>消息数</td>

</tr>
</thead>
<s:iterator value="#request.unreadMap">
	<tr>
		<td> <img class="top_right_t" alt="用户的照片" src="image/<s:property value='key.portrait'/>"/></td>
	    <td> <label id="<s:property value='key.id'/>"> <s:property value="key.name" /></label>	</td>
		<td>
			<input type="button" name="<s:property value='key.id'/>"
				 value="未读消息<s:property value='value'/>" onclick="chatWith(this)"/>
		</td>
	</tr>
</s:iterator>
</table>
<!-- 聊天对话框 -->
<div id="chatBox" style="width:800;height:600;display:none">
	<textarea id="dialog"  disabled></textarea>
	<hr style="width:500;">
	<input type="text" id="response" style="width:800;height:180" />
	<input type="button" id="msgBtn" value="留言" onclick="sendMessage()"/>
</div>

</body>
</html>