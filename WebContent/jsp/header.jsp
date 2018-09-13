<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style type="text/css">
[data-tooltip-text]:hover {
	position: relative;
}

[data-tooltip-text]:hover:after {
	background-color: #000000;
	background-color: rgba(0, 0, 0, 0.8);
	-webkit-box-shadow: 0px 0px 3px 1px rgba(50, 50, 50, 0.4);
	-moz-box-shadow: 0px 0px 3px 1px rgba(50, 50, 50, 0.4);
	box-shadow: 0px 0px 3px 1px rgba(50, 50, 50, 0.4);
	-webkit-border-radius: 10px;
	-moz-border-radius: 10px;
	border-radius: 10px;
	color: #FFFFFF;
	font-size: 12px;
	content: attr(data-tooltip-text);
	margin-bottom: 10px;
	top: 90%;
	left: 20px;
	padding: 7px 12px;
	position: absolute;
	width: auto;
	min-width: 20px;
	max-width: 300px;
	word-wrap: break-word;
	z-index: 9999;
}

.scrollable-menu {
    height : auto;
    max-height : 330px;
    width : 50px;
    overflow-x : hidden;
}
</style>

<!-- JS -->

<script type="text/javascript">
	function selectMessage() {
		$.ajax({
			type : "get",
			url : "messageList.do",
			dataType : "json",
			success : function(data) {
				$('#messageData').remove();
				var str = '<ul class="dropdown-menu scrollable-menu" role="menu" id="messageData">' +
					'<div><h6><a href="#" id="search" style="text-align:center; color:yellow">메시지 보내기</a></h6></div>';
				$.each(data.messageList, function(index, message) {
						if ($("#session").val() === message.isTo) {
							str += '<li><a href="#" class="isFrom">' + message.isFrom + '</a>'
								+ '&nbsp;&nbsp; <button class="delete">삭제</button></li>';
						} else if ($("#session").val() === message.isFrom) {
							$.each(data.messageList, function(index, msg) {
								if (message.isTo === msg.isFrom) {
									message.isTo = null;
									return false;
								}
							});
							if (message.isTo !== null) {
								str += '<li><a href="#" class="isFrom">' + message.isTo + '</a>'
									+ '&nbsp;&nbsp; <button class="delete">삭제</button></li>';
							}
						}
				}) // each의 끝
				str += '</ul>';
				$('#messageList').append(str);
			} // success의 끝
		}) // ajax의 끝
	} //selectMessage 함수의 끝 

	$(document).ready(function() {
		alarmCount();
		$('#message').click(function() {
			selectMessage();
			$('#messageList').removeAttr('data-tooltip-text');
		});
		$('#alarm').click(function() {
			selectAlarm();
			$('#alarmList').removeAttr('data-tooltip-text');
		});
		$('#alarm').mouseleave(function() {
			$('#alarmData').remove();
			$('#alarmList').attr('data-tooltip-text', '알림');
		});

		$("#messageList").on("click", ".isFrom", function() {
			var width = 400,
				height = 900;
			var left = (screen.availWidth - width) / 2;
			var top = (screen.availHeight - height) / 2;
			var specs = "width=" + width;
			specs += ",height=" + height;
			specs += ",left=" + left;
			specs += ",top=" + top;
			
			window.open("logMessagePage.do?isFrom=" + $(this).text(), "팝업", specs);
		});

		$("#messageList").on("click", ".delete", function() {
			var index = $('.delete').index(this);
			$.ajax({
				type : 'get',
				url : 'deleteMessage.do',
				data : {
					'isFrom' : $(".isFrom").eq(index).text()
				},
				dataType : 'json',
				success : function(data) {
					if (data.result) {
						$("#messageList li").eq(index).remove();
					}
				},
				error : function(xhr, status, error) {
					alert("삭제오류");
				}
				});
		});
		
		$("#messageList").on("click", "#search", function() {
			var width = 800,
				height = 500;
			var left = (screen.availWidth - width) / 2;
			var top = (screen.availHeight - height) / 2;
			var specs = "width=" + width;
			specs += ",height=" + height;
			specs += ",left=" + left;
			specs += ",top=" + top;

			window.open("followingList.do?following=" + $("#id").val(), "메시지 보내기", specs);
		});
		
		$("#delete").click(function() {
			var pw = prompt("비밀번호를 입력하세요.");
			
			location.href="deleteMember.do?pwd=" + pw;
		});
		
		$("#chat").click(function() {
			var width = 800,
			height = 900;
			var left = (screen.availWidth - width) / 2;
			var top = (screen.availHeight - height) / 2;
			var specs = "width=" + width;
			specs += ",height=" + height;
			specs += ",left=" + left;
			specs += ",top=" + top;

			window.open("http://10.10.10.71:3000/client?id=" + $("#id").val(), "공개 채팅방", specs);
		});
	});

	var selectAlarm = function(){
		$.ajax({
			type: "get",
			url: "selectAlarm.do",
			data: {"readCheck" : 1},
			dataType: "json",
			success: function(data){
				$('#alarmData').remove();
				var str = '<ul class="dropdown-menu" id="alarmData" style="height:380px; width:auto">'+
				'<div><h6 style="text-align:center; color:yellow">새로운 알림</h6></div>';
				$.each(data.alarmList, function(index, alarmList){
					var msg = '';
					if(alarmList.type=="writeArt"){msg='님이 새 게시물을 작성했습니다.';}
					if(alarmList.type=="writeLecture"){msg='님이 새 강의를 개설했습니다.';}
					if(alarmList.type=="artComment"){msg='님이 댓글을 달았습니다.';}
					if(alarmList.type=="maxPeople"){msg='의 인원 모집이 완료됐습니다.';}
					if(index < 7){
						str += '<li><a style="color:white;">' + alarmList.isFrom + msg + '&emsp;&emsp;<span onmouseup="updateAlarm('+
								alarmList.no + ')">✔&ensp;읽음</span></a></li>';
					}
					else return false;
				}) // each의 끝
				alarmCount();
				str += '<div><a style="text-align:center; color:white" href="alarmPage.do">더 보기</a></div>'+'</ul>';
				$('#alarmList').append(str);
			}	// success의 끝
		}) // ajax의 끝
	} //selectAlarm 함수의 끝 

	var alarmCount = function(){
		$.ajax({
			type: "get",
			url: "alarmCount.do",
			dataType: "json",
			success: function(data){
				$('#alarmCount').html(data.alarmCount);
			} // success의 끝
		}) // ajax의 끝
	} //alarmCount 함수의 끝

	var updateAlarm = function(no){
		$.ajax({
			type: "get",
			url: "updateAlarm.do",
			data: {"no": no },
			success: function(){
				alarmCount();
				selectAlarm();
			} //success의 끝
		}) //ajax의 끝
	}	//updateAlarm 함수의 끝
	
</script>

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- header menu var -->
<header id="header" class="header">
	<div class="top-navigation full-w">
		<div class="inner-w clf">

			<nav class="menu">
				<c:if test="${id == null }">
					<a href="loginForm.do" class="before">로그인</a>
					<a href="joinForm.do" class="before">회원가입</a>
				</c:if>
				<c:if test="${id != null }">
					<a href="logout.do" class="after">로그아웃</a>
					<a href="myPage.do" class="after">마이페이지</a>
				</c:if>

				<!--                 <div class="nav-btn ui-dropdown"> -->
				<!--                     <button type="button" class="btn-dropdown">고객센터</button> -->
				<!--                     <ul class="menu-dropdown"> -->
				<!--                         <li><a href="/w/board/notice">공지사항</a></li> -->
				<!--                         <li><a href="/w/board/faq">자주 묻는 질문</a></li> -->
				<!--                         <li><a href="mailto:support@idus.me">메일로 문의</a></li> -->
				<!--                     </ul> -->
				<!--                 </div> -->
			</nav>
		</div>
	</div>
</header>
</head>
<body>
	<div class="row header">
		<!-- Begin Header -->

		<!-- Logo
        ================================================== -->
		<div class="span5 logo">
			<a href="main.do"><img src="img/workspace-logo.png" alt="" /></a>
			<h5>ARTIST WEB GALLERY</h5>
		</div>

		<!-- Main Navigation
        ================================================== -->
		<div class="span7 navigation">
			<div class="navbar hidden-phone">
					<ul class="nav">
						<li class="dropdown active">
							<a class="dropdown-toggle" href="#" onclick="location.href='main.do'">Home</a>
						</li>
						<li class="dropdown">
			                <a class="dropdown-toggle" href="#" onclick="location.href='artistForm.do'">Artist</a>
			            </li>
						<li class="dropdown">
			                <a class="dropdown-toggle" href="#" onclick="location.href='artForm.do'">Art</a>
			            </li>
						<li class="dropdown">
			                <a class="dropdown-toggle" href="#" onclick="location.href='lectureForm.do'">Lecture</a>
			            </li>
		                <li><a href="feed.do">Feed</a></li>
			 			<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#">Board <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="boardForm.do">자유게시판</a></li>
							<li><a href="qnaForm.do">문의게시판</a></li>
						</ul>
						</li>
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"
						onclick="location.href='myPage.do'"> MyPage <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="myLectureFormA0.do">강의 내역 조회</a></li>
							<li><a href="mySellFormA0.do">주문 내역 조회</a></li>
							<li><a href="javascript:;" id="delete">회원 탈퇴</a></li>
						</ul></li>
					<li class="dropdown" id="message"><a href="#"
						data-tooltip-text="메시지" id="messageList" style="font-weight: bold">메시지
					</a> <input type="hidden" id="session"
						value="<%=session.getAttribute("id")%>"></li>
					<li class="dropdown" id="alarm"><a href="#"
						data-tooltip-text="알림" id="alarmList" style="font-weight: bold">알림
							<span id="alarmCount" style="color: Fuchsia; font-weight: bold">0</span>
							개
					</a></li>
					<li><a
						href="javascript:;" id="chat">공개 채팅방</a></li>
				   </ul>
			 </div>
				<!-- Mobile Nav  ================================================== -->
				<form action="#" id="mobile-nav" class="visible-phone">
					<div class="mobile-nav-select">					
					<select onchange="window.open(this.options[this.selectedIndex].value,'_top')">
	                    <option value="">Navigate...</option>
	                    <option value="main.do">Home</option>
	                    <option value="main.do">- Main Page</option>
	                    <option value="artistForm.do">Artist</option>	                        	                        
	                    <option value="artistMyPage.do">- 아티스트 개인 페이지</option>
	                    <option value="artForm.do">Art</option>
	                    <option value="lectureForm.do">Lecture</option>
                        <option value="addLectureForm.do">- 강의 신청 페이지</option>
	                    <option value="boardForm.do">Board</option>
                        <option value="boardForm.do">- 자유게시판</option>
                        <option value="qnaForm.do">- 문의게시판</option>
	                    <option value="feed.do">Feed</option>
	                    <option value="myLectureFormG.jsp">MyPage</option>
	                    <option value="myLectureFormG.jsp">- 강의 내역 조회</option>
	                    <option value="mySellFormA0.do">- 주문 내역 조회</option>
	                    <option value="updateMemberForm.do">- 개인 정보 수정</option>
	                    <option value="blog-style4.htm">- 회원 탈퇴</option>
	                </select>
					</div>
				</form>
			</div>
		</div>
	<!-- End Header -->
	<input type="hidden" id="id" value="${id }">
</body>
</html>