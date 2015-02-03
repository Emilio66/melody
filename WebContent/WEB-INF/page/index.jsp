
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
 import="org.apache.struts2.ServletActionContext,org.melody.bean.*,com.opensymphony.xwork2.ActionContext,java.util.*"
  %>
<%@taglib prefix="s" uri="/struts-tags" %>

<% String path=request.getContextPath(); //云上的网站不能随便访问文件%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>主页</title>
<link rel="stylesheet" type="text/css" href=" <%=path%>/css/music.css" />
<link rel="stylesheet" type="text/css" href=" <%=path%>/css/bootstrap.min.css" />
<script type="text/javascript" src="<%=path%>/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=path%>/js/bootstrap.min.js"></script>
<script type="text/javascript">
	var friendId;
  	$(document).ready( function(){
		$("#page1").click(function() {
			$(".box1").animate({left : 8}, 1000); //页面之间滑动效果 
			$(".box2").animate({right : -1200}, 1000);
			$(".box3").animate({left : 2400}, 1000);
		});
		$("#page2").click(function() {
			$(".box1").animate({left : -1366}, 1000);
			$(".box2").animate({right : 0}, 1000);
			$(".box3").animate({left : 1400}, 1000);
		});
		$("#page3").click(function() {
			$(".box1").animate({left : -2400}, 1000);
			$(".box2").animate({right : 1200}, 1000);
			$(".box3").animate({left : 0}, 1000);
		});
		
		//判断当前的歌曲是否已经被收藏,若被收藏则变化颜色
		var isFavorite=$("#isFavorite").val();
	//	alert("isFavorite: "+isFavorite);
		if(isFavorite=="true"){
			var icon = document.getElementById("likeit");
			icon.src='<%=path%>/image/like_on.png';
			icon.name = "1" ;
		}
		
		//搜索框的艺术
		$("#search").mouseover(function(){
			$("#search").animate({width : 250}, 400);
		});
		$("#search").mouseout(function(){
			$("#search").animate({width : 190}, 400);
		});
		
		//播放下一首歌曲
		$("#next").click(function(){
			$("#nextForm").submit();
		});
		
		//评价这首歌曲
		$("#remark").click(function(){
			var rate=$("#rate").val();
			var point=$("#opinion").val();
			var username=$("#username").text();//获得用户名
			 //获得当前时间
			var data = new Date();  
	        var year = data.getFullYear();  //获取年
	        var month = data.getMonth() + 1;    //获取月
	        var day = data.getDate(); //获取日
	        var hours = data.getHours(); 
	        var minutes = data.getMinutes();
	        var seconds=data.getSeconds();
	        var time = year + "-" + month + "-" + day + " " + hours + ":" + minutes+":"+seconds;
			//alert("rate:"+rate+"   opinion:"+opinion);
			$.ajax({
				url: "<%=path%>/ajax/main_remark",
				data: {star:rate,opinion:point},
				success: function (data){
					alert("谢谢您的评价~");
					//先在评价框上直接加上信息，实际上数据已经插入数据库，只是没有刷新而已
					$("#commentTable").append("<tr><td colspan='2'>"+point+"</td></tr>"+
							"<tr><td align='right'>"+username+"</td><td align='right'>"+time+"</td></tr>");
				},
				error: function(data){
					alert("网络连接失败");
				}
			});
		});
		
		//搜索时按回车即可响应
		$('#search').keydown(function(event) {
			if (event.keyCode == 13) {
				//alert("seraching");
				search();//调用搜索音乐的函数
			}
		});
		
		//发送消息
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
				$("#dialog").append(time+" 你说："+message+"\r\n");
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
		
		//如果播放完成，则自动下一曲
		var player=document.getElementById("player");
		player.addEventListener('ended', function () { 
			//alert("播放完成");
			$("#nextForm").submit();
		}, false);
		
		//通过ID查找好友
		$("#addById").click(function() {
			$("#addFriendDialog").empty();
			var friendInfo=$("#friendInfo").val();
			friendInfo=$.trim(friendInfo);
			//alert(friendInfo+"~");
			var regex=new RegExp("^[0-9]*$");
			if(regex.test(friendInfo)==false){
				alert("ID格式输入有误，只能是数字");
			}else{
				$.ajax({
					url: "<%=path%>/ajax/friend_queryById",
					data:{friendId:friendInfo, format:"json"},
					success: function (data){						
							//alert("获得查询结果成功");
							//组装查询到的用户
							if(data[0].name != null){
								$.each(data,function(){
									$("#addFriendDialog").append("<span><input type='checkbox' class='checkbox' name='add' value='"+this.id+"'>"
											+"<img class='top_right_t' alt='用户的照片' src='<%=path%>/image/"
											+this.portrait+ "' >&nbsp;&nbsp;"
											+"<label >"+this.id+"</label>&nbsp;"
											+"<label >"+this.name+"</label>&nbsp;</span>"
											);						
								});	
							}
							//$("#addFriendDialog").append("<input type='button' id='addRequest' value='添加' onclick='add()'>");
							//$("#addFriendDialog").show(200);
						},
					error: function(data){
						alert("获得查询结果失败，请检查网络再重试");
					}				
				});
			}	
		});
		
		//通过昵称查找好友	
		$("#addByName").click(function() {
			$("#addFriendDialog").empty();
			var friendInfo=$("#friendInfo").val();
			friendInfo=$.trim(friendInfo);
			
				$.ajax({
					url: "<%=path%>/ajax/friend_queryByName",
					data:{friendName:friendInfo, format:"json"},
					success: function (data){						
							//alert("获得查询结果成功");
							//组装查询到的用户
							if(data[0].name!=null){
								$.each(data,function(){
									$("#addFriendDialog").append("<span><input type='checkbox' class='checkbox' name='add' value='"+this.id+"'>"
											+"<img class='top_right_t' alt='用户的照片' <%=path%>/src=' image/"
											+this.portrait+ "' >&nbsp;&nbsp;"
											+"<label >"+this.id+"</label>&nbsp;"
											+"<label >"+this.name+"</label>&nbsp;</span>"
										);						
								});	
							}
							//$("#addFriendDialog").append("<input type='button' id='addRequest' value='添加' onclick='add()'>");
							//$("#addFriendDialog").show(200);
						},
					error: function(data){
						alert("获得查询结果失败，请检查网络再重试");
					}				
				});
			
		});
		
		//设置一个timer，隔一段时间查询一下未读消息的数量
		checkUnread();
		setInterval("checkUnread()",121000);
		
	});//文档加载结束

	//收藏歌曲
	function likeIt(){
		var icon = document.getElementById("likeit");
		if(icon.name=="0"){
			icon.src=' <%=path%>/image/like_on.png';
			icon.name = "1" ;
			var musicName=$("#music_name").text();
			var musicId=$("#curMusicId").val();
			$.ajax({
				url: "<%=path%>/ajax/main_favorite",
				success: function (data){
						alert("收藏成功");
						//在收藏列表中加入这一项
						$("#favoriteList").append("<li><a href='<%=path%>/main!play?music_id="+musicId+"'>"+musicName+"</a></li>");
					},
				error: function(data){
						alert("您已经收藏了哦");
					}
				});
		}else{
			alert("您已经收藏了哦~");				
		}			
		
	}
	
	//分享歌曲
	function share(item){
		var name=item.name;
		//alert("name="+name);
		var content="#"+name+"# 这首歌非常好听哦~ 可以听一下";
		$.ajax({
			url:"<%=path%>/ajax/main!share",
			data:{content:content},
			success: function (data){
					alert("分享成功");
				},
			error: function(data){
				alert("分享失败");
			}		
		});
	}
	
	//获得要聊天的好友的ID
	function chat(obj){
		//$("#dialog").text("");
		//获取这两个用户的历史聊天消息
		friendId=obj.name;
		$.ajax({
			url:"<%=path%>/ajax/message_chat",
			data:{senderId:friendId,format:"json"},
			success: function(data){
				var dialog=$("#dialog");
				dialog.empty();
				//遍历所有消息，正确输出消息
				$.each(data,function(){
					var sender_id=this.sender_id;
					if(sender_id==friendId){
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
		
		var friendName=obj.value;
		$("#myModalLabel").text("与"+friendName+" 聊天中 ");
	}
	
	//检查未读消息的数目
	function checkUnread(){	
		$.ajax({
			url:"<%=path%>/ajax/message_unreadCount",
			success: function(data){
				$("#unread").empty();				
				//获得消息数量	
				if(data>0){		//动态添加右上角元素
					$("#unread").append("<a href='<%=path%>/message_listUnread'>"+
						"<span class='glyphicon glyphicon-envelope' style='font-size: 30px;'></span>"+
						"<span class='badge' style='background-color: red;'>"+data+"</span></a>");
				}else{
					$("#unread").append("<span class='badge'>0</span>");//没有消息
				}
			}
		});
	}
	
	//添加好友
	function add(){
		var friendArray=$("input[type='checkbox'][name='add']").serialize();//获得所有check的好友
		
		$.ajax({
			url:"<%=path%>/friend_addFriendRequest",
			data:friendArray,
			success:function(data){
				alert("添加好友请求已经发出,等待对方确认");
			},
			error:function(data){
				alert("添加好友失败，请检查网络再重试");
			}
		});
}
	
	//删除好友
	function deleteFriend(friend){	
		if(confirm("是否确认删除此好友？")){
			var id=friend.name;
			$.ajax({
				url: "<%=path%>/friend_deleteFriend",
				data:{friendId:id},
				success: function (data){
						$("#"+id).attr("src"," <%=path%>/image/unuse.jpg"); 
						$("input[type='button'][name="+id+"]").attr("disabled","true");
						//alert("删除成功");			
					},
				error: function(data){
					alert("删除失败，请检查网络再重试");
				}
			});		
		}	
	}
	
	//搜索音乐
	function search(){
		var name=$("#search").val();
		$.ajax({
			url:"<%=path%>/ajax/main_search",
			data:{content:name,format:"json"},
			success: function (data){
				var resultTable=$("#search_result");
				
				if(data.searchList==null){
					alert("抱歉，该资源不存在！");
				}else{				
					resultTable.empty();
					resultTable.append("<ol>");
					$.each(data.searchList,function(){
							resultTable.append("<li><a href='<%=path%>/main!play?music_id="+
								this.id+"'>"+this.name+"</a>"+"</li>");
						});
					resultTable.append("</ol>");
				}
			},
			error: function(data){
				alert("查询失败，请检查网络再重试");
			}
		});		
	}
	
	//星级评价效果 	
	function rating(rate){
		var index=0;
		if( rate == "star1" ){ index = 1;	}
		else if	( rate == "star2" ){ index = 2;	}
		else if	( rate == "star3" ){ index = 3;	}
		else if	( rate == "star4" ){ index = 4;	}
		else if	( rate == "star5" ){ index = 5;	}		
		
		for (var i = 1; i <= 5; i++) {
			document.getElementById("star" + i).value = 0;
		}
		document.getElementById("star" + index).value = 1;

		for (var i = 1; i <= 5; i++) {
			if (i <= index) {
				document.getElementById("star" + i).style.background = "url( <%=path%>/image/star-4.png)";
			} else {
				document.getElementById("star" + i).style.background = "url( <%=path%>/image/star-0.png)";
			}
		}
		$("#rate").val(index);//获得评星的个数，放入隐藏域
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
	<div class="box1">
<!--  左边音乐播放器 -->	    
    	<div class="left">
        	<div class="record"></div>
               <div class="cont">
                <audio id="player" controls="controls" src=" <%=path%>/music/<s:property value='#request.currentMusic.path'/>" 
                	autoplay="autoplay" style="width:86%;float:left;">
                </audio> 
                <!-- 播放下一曲 -->          
                <img class="bt" id="next" src=" <%=path%>/image/foward_off.png"/>
                <s:form action="main!next" id="nextForm"></s:form>
            </div>
        </div>
<!--  正在收听+音乐评价 -->	       
        <div class="middle">
        	<div class="nowplay">
            	<label class="fontsty">正在收听：</label>
                <label class="fontsty" id="music_name"><s:property value='#request.currentMusic.name'/></label>
                <img id="likeit" name="0" src=" <%=path%>/image/like_off.png" style="cursor:pointer;"width="25" height="25" onclick="likeIt()"/>
            	<input type="hidden" id="isFavorite" value="<s:property value='#request.isFavorite'/>"/>
            	<input type="hidden" id="curMusicId" value="<s:property value='#request.currentMusic.id'/>"/>
            </div>        	
            <div class="my_point_box">
            	<form id="mypoint" method="post" action="<%=path%>/ajax/main_remark">
                	<label class="fontsty">我来评价：</label>
                    <textarea class="my_point_txt" id="opinion" style="resize: none">oh,my god! It's amazing!!! </textarea>
                    <div class="starbox">
                        <ul class="starbox_list">
                            <li class="star" id="star1" value="0" onclick="rating('star1')"></li>
                            <li class="star" id="star2" value="0" onclick="rating('star2')"></li>
                            <li class="star" id="star3" value="0" onclick="rating('star3')"></li>
                            <li class="star" id="star4" value="0" onclick="rating('star4')"></li>
                            <li class="star" id="star5" value="1" onclick="rating('star5')"></li>
                        </ul>
                    </div>
                   
                     <div class="starcheck" style="width: 320px;">
                    	<input type="hidden" id="rate" name="star" value="5"/>
                        <input id="remark" class="btn" type="button" value="评分" />
                        <button type="button" name="<s:property value='#request.currentMusic.name'/>"
                        class="btn btn-info" onclick="share(this)">分享</button>
                    </div>
                    
                </form>
            </div>
        </div>
<!-- 其他用户的乐评 -->	        
        <div class="right2">
        	<div align="center">
              	<p>乐评</p>
            </div>
            <div class="right_main">	<!-- 用户对这首歌评价 -->
            	<table id="commentTable">
            		<s:iterator value="#request.commentList">
	            		<tr><td colspan="2"><s:property value="content"/></td></tr>
	            		<tr>
	            			<td align="right"><s:property value="username"/></td>
	            			<td align="right"><s:property value="time"/></td>	
	            		</tr>
	            		<tr><td colspan="2">---------------------------------</td></tr>
 					</s:iterator>
            	</table>
            </div>
        </div>
    </div>
    
	<!--  第二屏幕 -->	   
    <div class="box2">
		<!-- 推荐与收藏音乐折叠框-->
		<div id="list" class="right1">
			<div class="panel-group" id="accordion">
			
			<!-- 推荐音乐列表-->			
				<div class="panel panel-default" style="max-height: 375px; border-radius: 6px;">
					<div class="panel-heading">
						<h4 class="panel-title">
							<a data-toggle="collapse" data-toggle="collapse"
								data-parent="#accordion" href="#collapseTwo"> 音乐推荐 </a>
						</h4>
					</div>
					<div id="collapseTwo" class="panel-collapse collapse in" style="max-height: 375px; overflow: auto;">
						<div class="panel-body">
						<ol>
            			 <s:iterator value="#request.recommandList">
            			  <li>
            				<a href="<%=path%>/main!play?music_id=<s:property value='id'/>"><s:property value="name"/></a></li>
            			</s:iterator>
            		   	</ol>
						</div>
					</div>
				</div>
			
			<!-- 收藏音乐列表-->
				<div class="panel panel-default" style="max-height: 375px; border-radius: 6px;">
					<div class="panel-heading">
						<h4 class="panel-title">
							<a data-toggle="collapse" data-toggle="collapse" data-parent="#accordion"
							 href="#collapseOne">我的收藏 </a>
						</h4>
					</div>
					<div id="collapseOne" class="panel-collapse collapse" style="max-height: 375px; overflow: auto;">
						<div class="panel-body">
						<ol id="favoriteList">
            			  <s:iterator value="#request.favoriteList">
            				<li>
            				 <a href="<%=path%>/main!play?music_id=<s:property value='id'/>"><s:property value="name"/></a>
            				</li>
            			  </s:iterator>
            			</ol>						
						</div>
					</div>
				</div>
				
			</div>
		</div>
		
		<!--  好友列表 -->
		<div class="right1_box2_2" style="height: 450px;">
			<div align="center">
				<p>好友列表</p>
			</div>
			<div class="right_main2" style="height: 360px;">
					<s:iterator value="#request.friendList">
						<div style="height: 50px">
							<img  id="<s:property value='id'/>" class="top_right_t" style="float: left;" 
								src=" <%=path%>/image/<s:property value='portrait'/>" />
							<input type="button" style="float:left; width: 50%;" 
								class="btn btn-primary btn-lg btn-block" id="chatfriend" 
								data-toggle="modal" data-target="#frienddialog" name="<s:property value='id'/>"
								onclick="chat(this)"
								value="<s:property value='name'/>"
							/>
							<input type="button" style="margin-right:5px;" 
								class="btn btn-default" id="delfriend" name="<s:property value="id"/>" 
								onclick="deleteFriend(this)"
								value="删除"
							/>						
						</div>
					</s:iterator>								
			</div>
			<!-- 查找好友 -->
			<div style="margin-top: 380px;">
				<button id="search_friends_btn" type="button" class="btn btn-default btn-lg btn-block" style="width: 80%;" 
						data-toggle="modal" data-target="#searchfrienddialog" >添加好友
				<!-- <span class="glyphicon glyphicon-search"></span> -->
				</button>
			</div>
		</div>
		
    </div>
    
    <!-- 第三屏幕 -->
	<div class="box3">
		<div class="top_middle">
			<input class="search1" type="text" id="search" style="width: 300px" />
			<input class="serach_btn" type="button" id="search_btn" value="搜索"
				onclick="search()" />
		</div>
		<div class="right1 box3_1" style="height: 320px; width: 550px;margin-left: 350px;">
			<div align="center">
				<p>搜索列表</p>
			</div>
			<div id="search_result" class="search_main"></div>
		</div>
	</div>
    
<!--  页面切换按钮 -->	    
    <div class="dot_box">
		<a href="#" class="rBtns" id="page1">正在播放</a> 
		<a href="#" class="rBtns" id="page2">音乐交友</a>
		<a href="#" class="rBtns" id="page3">搜索音乐</a>
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
			<!-- <a href="#" id="unReadMessage">
  				<span class="glyphicon glyphicon-envelope" style="font-size: 30px;"></span>
  				<span class="badge" style="background-color: red;">5</span>
			</a> 
			-->
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
		
		<!-- 删除好友对话框 -->
		<div class="modal fade" id="delFriend" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  			<div class="modal-dialog">
    			<div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        				<h4 class="modal-title" id="myModalLabel">删除好友</h4>
      				</div>
      				<div class="modal-body">
        				确定删除?
      				</div>
      				<div class="modal-footer">
        				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        				<button id="del_friend_btn" type="button" class="btn btn-primary">确定</button>
      				</div>
    			</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
    
    	<!-- 添加好友列表 -->
		<div class="modal fade" id="searchfrienddialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  			<div class="modal-dialog">
    			<div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        				<h4 class="modal-title" id="myModalLabel">查找用户</h4>
        				<div class="input-group" style="width: 100%;">
        					<input type="text" class="form-control" style="height:36px;" id="friendInfo">
        					<span class="input-group-btn">
       							<button id="addById" class="btn btn-default" style="width:90px;" type="button">按ID查找</button>       					
       							<button id="addByName" class="btn btn-primary" style="width:110px;"  type="button">按昵称查找</button>
      						</span>      					
						</div>
      				</div>
      				<div class="modal-body">        				
        				<div id="addFriendDialog">
        												
						</div>
      				</div>
      				<div class="modal-footer">     				
        				<button id="add_friends_btn" style="width: 70px;" type="button"
        					onclick="add()" class="btn btn-primary">添加好友</button>
        				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      				</div>
    			</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
    
    
<!--  选择背景 -->	    
    <ul>
    	<li class="rBg" style="background:url( <%=path%>/image/bgdot_1.png)" id="b1" onclick="bgchange('b1')"></li>
    	<li class="rBg" style="background:url( <%=path%>/image/bgdot_2.png)" id="b2" onclick="bgchange('b2')"></li>
    	<li class="rBg" style="background:url( <%=path%>/image/bgdot_3.png)" id="b3" onclick="bgchange('b3')"></li>
    	<li class="rBg" style="background:url( <%=path%>/image/bgdot_4.png)" id="b4" onclick="bgchange('b4')"></li>
    </ul>
</body>
<%-- 
<center>
	<s:form action="main!share">
	<s:textarea id="share" name="content"></s:textarea>
	<s:token/>
	<s:submit value="分享"></s:submit>
	</s:form>
</center>
--%>
</html>