<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Piccolo Theme</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- CSS
================================================== -->
<link href='http://fonts.googleapis.com/css?family=Oswald'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/bootstrap-responsive.css">
<link rel="stylesheet" href="css/jquery.lightbox-0.5.css">
<link rel="stylesheet" href="css/custom-styles.css">

<!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <link rel="stylesheet" href="css/style-ie.css"/>
<![endif]-->

<!-- Favicons
================================================== -->
<link rel="shortcut icon" href="img/favicon.ico">
<link rel="apple-touch-icon" href="img/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72"
	href="img/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114"
	href="img/apple-touch-icon-114x114.png">

<!-- JS
================================================== -->
<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="js/bootstrap.js"></script>
<script src="js/jquery.custom.js"></script>
<script type="text/javascript">
window.onload = function(){
myOrderFormG();	
}

var buyingPagingLog;

var myOrderFormG = function(){
	$.ajax({
		type: "post",
		url: "myOrderFormG.do",
		data: {"pageBuy": buyingPagingLog },
		dataType: "json",
		success: function(data){
			$('#buyingList').empty();
			$('#buyingPaging').empty();
			var str1 = "<tr>";
			$.each(data.buyingList, function(index, buyingList){ 	// 구매 목록
				var isCheck;
				if (buyingList.isCheck==1) {
					isCheck = '강의';
				}else{
					isCheck = '작품';
				}
				str1 += '<td>' + isCheck + '</td><td>' + buyingList.no + '</td><td>' + 
						buyingList.title + '</td><td>' + buyingList.id + '</td><td>' +  
						buyingList.totalPrice + '</td><td>' + buyingList.payMethod + '</td><td>' +
						buyingList.state + '</td><td>' + buyingList.payDate + '</td>';
				str1 += '</tr>';
			}); // each
			$('#buyingList').append(str1); // 구매 목록- 테이블에 붙이기

			var str1Paging = "";
			if(data.startBuy != 1){
				var startBuy = data.startBuy - 1;
				str1Paging += '<button onclick= "buyingPagingLog = 1; myOrderFormG();">첫 페이지</button>' +
							  '<button onclick= "buyingPagingLog =' + startBuy + '; myOrderFormG();">이전</button>';
			} // 처음, 이전 페이지 버튼
			if(data.endBuy < data.lastBuy){
			for(var i = data.startBuy; i <= data.endBuy; i++){
				if(i==data.currentBuy){
					str1Paging += '[ '+i+' ]';
				}else{
					str1Paging += '<button onclick= "buyingPagingLog =' + i + '; myOrderFormG();">' + i + '</button>';
				}
			} 
			var endBuy = data.endBuy + 1;
			str1Paging += '<button onclick= "buyingPagingLog =' + endBuy + '; myOrderFormG();">다음</button>' +
				   		  '<button onclick= "buyingPagingLog ='+ data.lastBuy +'; myOrderFormG();">끝 페이지</button>';  // 다음, 끝 페이지 버튼
			} // if(data.endBuy < data.lastBuy) 끝
			else{
			for(var i = data.startBuy; i <= data.lastBuy; i++){
				if(i==data.currentBuy){
					str1Paging += ' [ '+i+' ] ';
				}else{
					str1Paging += '<button onclick= "buyingPagingLog =' + i + '; myOrderFormG();">' + i + '</button>';
				}	
			}
			} // else 끝
			
			$('#buyingPaging').append(str1Paging); // 구매 목록 페이징 처리- 테이블에 붙이기
			
		} // success 끝
		}) // ajax 끝
	}	// myOrderFormG 함수의 끝
</script>

</head>

<body>
	<div class="color-bar-1"></div>
	<div class="color-bar-2 color-bg"></div>

	<div class="container main-container">

		<!--     Header section -->
		<%@include file="header.jsp"%>

		<!-- Page Content
    ================================================== -->

		<div class="span3 sidebar page-left-sidebar">
			<!-- Begin sidebar column -->

			<!--Navigation-->
			<h5 class="title-bg" style="margin-top: 0px;">마이페이지</h5>
			<ul class="post-category-list">
				<li><a href="myLectureFormG0.do"><i class="icon-plus-sign"></i>강의 내역 조회</a></li>
				<li><a onclick="myOrderFormG()"><i class="icon-plus-sign"></i>주문 내역 조회</a></li>
				<li><a href="#"><i class="icon-plus-sign"></i>개인 정보 수정</a></li>
				<li><a href="#"><i class="icon-plus-sign"></i>회원 탈퇴</a></li>
			</ul>

		</div>

		<div class="row">
			<!--Container row-->

			<div class="span8 contact">
				<!--Begin page content column-->

				<h2>주문 및 판매관리</h2>

				<div class="alert alert-success">Well done! You successfully
					read this important alert message.</div>

				<h4>주문 내역</h4>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th scope="col">구분</th>
							<th scope="col">강의/작품번호</th>
							<th scope="col">강의/작품명</th>
							<th scope="col">아티스트</th>
							<th scope="col">가격</th>
							<th scope="col">결제방식</th>
							<th scope="col">결제상태</th>
							<th scope="col">결제일</th>
						</tr>
					</thead>
					<tbody id="buyingList">
					</tbody>
					<tr>
						<td id="buyingPaging" colspan="8"></td>
					</tr>
				</table>


			</div>
			<!--End page content column-->


		</div>
		<!-- End container row -->

	</div>
	<!-- End Container -->

		<!-- Footer Area
        ================================================== -->
    <%@include file="footer.jsp"%>

</body>

</html>
