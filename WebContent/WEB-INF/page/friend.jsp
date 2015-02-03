<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
		import="org.melody.bean.*,com.opensymphony.xwork2.ActionContext,java.util.*"
%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%
	User user=(User)session.getAttribute("user");
	String portrait=user.getPortrait();
	String username=user.getName();
	//Music nowMusic =(Music)request.getAttribute("currentMusic");//get current music
	//List<User> friendList=(List<User>)request.getAttribute("friendList");
	//List<User> queryResultList=(List<User>)request.getAttribute("queryResultList");
	//User queryResult=(User)request.getAttribute("queryResult");
	//System.out.println("request list: "+queryResultList);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>好友</title>
<link rel="stylesheet" type="text/css" href="css/music.css"/>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"/>

<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
var friendId;
$(document).ready(function(){
	$("#chatBox").hide();
	$("#addFriendDialog").hide();
	
	$("#msgBtn").click(function(){
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
			$("#dialog").append(time+" 你说："+message);
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
	});
	
	$("#addById").click(function() {
		var friendInfo=$("#friendInfo").val();
		friendInfo=$.trim(friendInfo);
		//alert(friendInfo+"~");
		var regex=new RegExp("^[0-9]*$");
		if(regex.test(friendInfo)==false){
			alert("ID格式输入有误，只能是数字");
		}else{
			$.ajax({
				url: "/melody/ajax/friend_queryById",
				data:{friendId:friendInfo, format:"json"},
				success: function (data){						
						alert("获得查询结果成功");
						//组装查询到的用户
						$("#addFriendDialog").empty();
						$.each(data,function(){
							$("#addFriendDialog").append("<img class='top_right_t' alt='用户的照片' src='image/"+this.portrait+ "' >&nbsp;&nbsp;"
									+"<label >"+this.id+"</label>&nbsp;"
									+"<label >"+this.name+"</label>&nbsp;"
									+"<input type='checkbox' name='add' value='"+this.id+"'></br>");						
						});
						
						$("#addFriendDialog").append("<input type='button' id='addRequest' value='添加' onclick='add()'>");
						$("#addFriendDialog").show(200);
					},
				error: function(data){
					alert("获得查询结果失败，请检查网络再重试");
				}				
			});
		}	
	});
	
		
	$("#addByName").click(function() {
		var friendInfo=$("#friendInfo").val();
		friendInfo=$.trim(friendInfo);
		
			$.ajax({
				url: "/melody/ajax/friend_queryByName",
				data:{friendName:friendInfo, format:"json"},
				success: function (data){						
						alert("获得查询结果成功");
						//组装查询到的用户
						$.each(data,function(){
							$("#addFriendDialog").append("<img class='top_right_t' alt='用户的照片' src='image/"+this.portrait+ "' >&nbsp;&nbsp;"
									+"<label >"+this.id+"</label>&nbsp;"
									+"<label >"+this.name+"</label>&nbsp;"
									+"<input type='checkbox' name='add' value='"+this.id+"'/></br>");						
						});
						
						$("#addFriendDialog").append("<input type='button' id='addRequest' value='添加' onclick='add()'>");
						$("#addFriendDialog").show(200);
					},
				error: function(data){
					alert("获得查询结果失败，请检查网络再重试");
				}				
			});
		
	});
	
	//隔一段时间查询一下消息的数量
	checkUnread();
	setInterval("checkUnread()",20000);
});

function checkUnread(){	
	$.ajax({
		url:"/melody/ajax/message_unreadCount",
		success: function(data){
			$("#unread").empty();
			//alert(data);
			if(data>0){
				$("#unread").append("<font color='red'><a href='/melody/message_listUnread'>未读消息</a></font>");
			}
			$("#unread").append("<span class='badge'>"+data+"</span>");//获得消息数量		
		}
	});
}

function add(){
	var friendArray=$("input[type='checkbox'][name='add']").serialize();
	/* var content;
	$.each(friendArray,function(){
		content+=this.val()+"|";
	}); */
	alert(friendArray);
	
	$.ajax({
		url:"/melody/friend_addFriendRequest",
		data:friendArray,
		success:function(data){
			alert("添加好友请求已发出");
		},
		error:function(data){
			alert("添加好友失败，请检查网络再重试");
		}
	});
}

function deleteFriend(friend){	
	if(confirm("是否确认删除此好友？")){
		var id=friend.name;
		$.ajax({
			url: "/melody/friend_deleteFriend",
			data:{friendId:id},
			success: function (data){
					$("#"+id).attr("src","image/unuse.jpg");
					$("input[type='button'][name="+id+"]").attr("disabled","true");
					//alert("删除成功");			
				},
			error: function(data){
				alert("删除失败，请重试");
			}
		});		
	}	
}

function chat(obj){
	$("#chatBox").slideToggle(300);
	friendId=obj.name;
}


</script>
</head>

<body>
<!-- 未读消息提示 -->
<div id="unread" class="top_right_t">
</div>
<center>
<img alt="用户的照片" src="image/<%=portrait%>">
<s:label value="%{#session.user.name}"></s:label>
<table border="1" width="800">
<thead>
<tr>
<td>头像</td>
<td>昵称</td>
<td>消息数</td>
<td >删除</td>
<td>聊天</td> 
</tr>
</thead>

<s:iterator value="friendList" >
	<tr>
		<td> <img class="top_right_t" alt="用户的照片" src="image/<s:property value='portrait'/>" id="<s:property value='id'/>"> </td>
		<td> <s:property value="name"/> </td>
		<td> <s:property value="messages"/> </td>
		
		<td> <input type="button" name="<s:property value="id"/>" onclick="deleteFriend(this)" value="删除"></td>
		<td>
			<input name="<s:property value='id'/>" type="button" onclick="chat(this)" value="聊天"/>
		</td>
		<!-- 
		<td> 
			 <a href="/melody/main!favorite?music_id=<s:property value='id'/>">收藏</a> 
			<input type="button" name="<s:property value='id'/>" value="编辑" onclick="edit(this)">
		</td>编辑可以弹出js的prompt对话框，用以输入好友备注
		<td>
			<input id="<s:property value='id'/>" type="button" onclick="comment(this)" value="评价"/>
		</td>
		<td>
				<s:form action="main!remark" >
					<input type="hidden" value="<s:property value='id'/>" name="music_id">
					<s:textarea name="opinion"></s:textarea>
					<s:submit value="评价"></s:submit>
				</s:form>			
		</td> -->
	</tr>
</s:iterator>

</table>
<%-- 
<s:form 
        cssStyle="border: 1px solid black;"
        action='FriendAction!addQueryById'
        theme="ajax">
    <s:textfield id="friendInfo" name='friendInfo' />
    <s:submit value="查找" id="addById" targets="addFriendDialog"/>
</s:form> --%>

<input type="text" id="friendInfo"/>
<input type="button" value="按昵称查找" id="addByName"/>
<input type="button" value="按ID查找" id="addById"/>

<!-- 聊天对话框 -->
<div id="chatBox" style="width:500;height:400">
<textarea id="dialog" style="width:500;height:300" disabled></textarea>
<hr style="width:500;">
<input type="text" id="response" style="width:400;height:80" />
<input type="button" id="msgBtn" value="留言" />
</div>

<!-- 添加好友对话框 -->
<div id="addFriendDialog" style="width:500;height:400">

</div>
<%-- <s:form action="main!share">
<s:textarea id="share" name="content"></s:textarea>
<s:token/>
<s:submit value="分享"></s:submit>
</s:form> --%>
<s:fielderror></s:fielderror>
</center>
</body>
</html>