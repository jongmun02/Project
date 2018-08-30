<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>강의 신청 페이지</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- CSS
================================================== -->
<link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/bootstrap-responsive.css">
<!--<link rel="stylesheet" href="css/jquery.lightbox-0.5.css">-->
<link rel="stylesheet" href="css/custom-styles.css">

<!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <link rel="stylesheet" href="css/style-ie.css"/>
<![endif]--> 

<!-- Favicons
================================================== -->
<link rel="shortcut icon" href="img/favicon.ico">
<link rel="apple-touch-icon" href="img/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72" href="img/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114" href="img/apple-touch-icon-114x114.png">

<!-- JS
================================================== -->
<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js"></script>
<script src="js/bootstrap.js"></script>
<script src="js/jquery.custom.js"></script>
<script type="text/javascript" src="js/datePicker.js"></script>


<!-- For NaverEditor
================================================== --> 
<script src="resources/editor/js/HuskyEZCreator.js" charset="utf-8"></script>

<!-- 네이버 에디터 사용
================================================== -->
<script type="text/javascript" src="js/naverEditor.js"></script>

<!-- Thumbnail 
================================================== -->
<link rel="stylesheet" href="css/fileImage.css">

<!-- dataTables
================================================== -->
<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css"/>


<!--  권한 체크하여 강사 선택 버튼 숨기기 -->
<!-- ======================================================== -->
<script type="text/javascript">
$(document).ready(function() {
		
		if($("#checkIsCheck").val()==2)
			$("#modalButton").hide();
	});

</script>

<!-- Modal 실행문 -->
<!-- ======================================================== -->
<script>
    $(document).ready( function () {
    	$("#Tables").DataTable();
    } );
</script>

</head>

<body>
    <div class="color-bar-1"></div>
    <div class="color-bar-2 color-bg"></div>
    
   <div class="container main-container">
    
    <%@include file="header.jsp" %>  
     
    <!-- Page Content
    ================================================== --> 
    <div class="row"><!--Container row-->
        
        <div class="span12">
				<h2 class="title-bg">강의 개설 페이지</h2>
		</div>
        
        <div class="span4 contact">
				<!--Begin page content column-->
				<p class="lead">개설을 원하는 하는 강의를 직접 신청하세요.</p>			
				<div class="alert alert-block">정확한 커리큘럼이 정해진 강의만 신청해주세요.</div>
        
        
<!--         modal(모달) 사용하기!!! -->
        
        <input type="hidden" id="checkIsCheck" value="${isCheck}">
        <div id="modalButton">
        	<a href="#myModal" role="button" class="btn btn-inverse" data-toggle="modal">강사 선택</a>
        </div>
        </div>
        
        <div class="modal hide fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h5 id="myModalLabel">강사를 선택해주세요</h5>
        </div>
        <div class="modal-body">
            <table class="display" id="Tables">
            	<thead>
            		<tr>
            			<th style="background-color: #eeeeee; text-align: center;">강사명</th>
            		</tr>
            	</thead>
            	<tbody>
            		<c:forEach items="${artistList }" var="list">
            		<tr class="odd gradeX">
                    	<td>${list.name }</td>
                    </tr> 
                    </c:forEach>
            	</tbody>
            </table>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
            <button class="btn btn-inverse">Save changes</button>
        </div>
 		</div>   	
<!--     	모달 사용 끗!!! -->

		
		
        <div class="span4 contact"><!--Begin page content column-->

<!-- 			강의관련 내용 입력 부분 -->
           <div class="span8 container">
            <form action="addLecture.do" id="contact-form">
               
                <div class="input-prepend">
				    <span class="add-on"><i class="icon-pencil"></i></span>
				    <input class="span6" id="prependedInput" name="title" type="text" size="16" style="height: 20px;"
							placeholder="강의명을 입력하세요.">
				</div>                 
                  
                <div class="input-prepend">
				    <span class="add-on"><i class="icon-map-marker"></i></span>					
							<input class="span5" type="text" id="sample5_address" style="height: 20px;" placeholder="강의 장소를 입력하세요.">
                            <input class="btn btn-small btn-inverse" type="button" onclick="sample5_execDaumPostcode()" value="주소 검색" style="text-align: center; margin: 0 auto; height: 30px;"><br>
                            <div id="map" style="width:580px;height:250px;margin-top:10px;display:none"></div>

                            <script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
                            <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=df63ab88c6f092a4b29b7f555f1a82dc&libraries=services"></script>
                            <script>
                                var mapContainer = document.getElementById('map'), // 지도를 표시할 div
                                    mapOption = {
                                        center: new daum.maps.LatLng(37.537187, 127.005476), // 지도의 중심좌표
                                        level: 5 // 지도의 확대 레벨
                                    };

                                //지도를 미리 생성
                                var map = new daum.maps.Map(mapContainer, mapOption);
                                //주소-좌표 변환 객체를 생성
                                var geocoder = new daum.maps.services.Geocoder();
                                //마커를 미리 생성
                                var marker = new daum.maps.Marker({
                                    position: new daum.maps.LatLng(37.537187, 127.005476),
                                    map: map
                                });


                                function sample5_execDaumPostcode() {
                                    new daum.Postcode({
                                        oncomplete: function(data) {
                                            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                                            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                                            var fullAddr = data.address; // 최종 주소 변수
                                            var extraAddr = ''; // 조합형 주소 변수

                                            // 기본 주소가 도로명 타입일때 조합한다.
                                            if(data.addressType === 'R'){
                                                //법정동명이 있을 경우 추가한다.
                                                if(data.bname !== ''){
                                                    extraAddr += data.bname;
                                                }
                                                // 건물명이 있을 경우 추가한다.
                                                if(data.buildingName !== ''){
                                                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                                                }
                                                // 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
                                                fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
                                            }

                                            // 주소 정보를 해당 필드에 넣는다.
                                            document.getElementById("sample5_address").value = fullAddr;
                                            // 주소로 상세 정보를 검색
                                            geocoder.addressSearch(data.address, function(results, status) {
                                                // 정상적으로 검색이 완료됐으면
                                                if (status === daum.maps.services.Status.OK) {

                                                    var result = results[0]; //첫번째 결과의 값을 활용

                                                    // 해당 주소에 대한 좌표를 받아서
                                                    var coords = new daum.maps.LatLng(result.y, result.x);
                                                    // 지도를 보여준다.
                                                    mapContainer.style.display = "block";
                                                    map.relayout();
                                                    // 지도 중심을 변경한다.
                                                    map.setCenter(coords);
                                                    // 마커를 결과값으로 받은 위치로 옮긴다.
                                                    marker.setPosition(coords)
                                                }
                                            });
                                        }
                                    }).open();
                                }
                            </script>
				</div>
                                  
                
                <div class="input-prepend">
						<span class="add-on"><i class="icon-th-list"></i></span> 
						<select	class="form-control; span6" id="select" name="genre">
							<option selected="">장르를 선택하세요.</option>
                                <option value="Music">Music</option>
                                <option value="Picture">Picture</option>
                                <option value="Feature">Feature</option>
						</select>
				</div><br>
                
                <div class="input-prepend">
                    <div class="input-group date">
                        <span class="add-on"><i class="icon-calendar"></i></span>
                        <input type="text" class="form-control" name="datepicker1" id="datepicker1" name="startDate" style="height: 20px;" placeholder="개강일 선택">
                        
                        <span class="add-on"><i class="icon-calendar"></i></span>
                        <input type="text" class="form-control" name="datepicker2" id="datepicker2" name="endDate" style="height: 20px;" placeholder="종강일 선택">                         
                    </div>                    
                </div>                        
                                
                <div class="input-prepend">
                	<span class="add-on"><i class="icon-user"></i></span>
                    <input class="span6" id="prependedInput" name="maxPeople" size="16" type="text" style="height: 20px;" placeholder="모집할 인원을 입력하세요.">
                </div>                                                   
                
                <div class="input-prepend">
					<span class="add-on"><i class="icon-pencil"></i>강의내용</span><br>						
               		<textarea class="span7" id="content" name="content"	placeholder="작품내용을 입력하세요."></textarea>
                </div>
				
				<!-- 썸네일 이미지 업로드 부분 -->
				<div class="filebox bs3-primary preview-image">
                   <input class="upload-name" value="대표이미지를 설정하세요!!" disabled="disabled" style="width: 200px;">
                    <label for="input_file">업로드</label> 
                    <input type="file" id="input_file" class="upload-hidden" name="ufile"> 
                </div>
				<!-- ====================================================== -->
				
				<div class="row">
					<div class="span7">
						<input type="button" class="btn btn-warning pull-right" value="취소">
						<input type="submit" class="btn btn-success pull-right" value="전송">
					</div>					
				</div>
				
            </form>
            
        </div> <!--End page content column-->
        
    </div><!-- End container row -->
    </div>
    </div> <!-- End Container -->

    <!-- 	Footer section -->
	<%@include file="footer.jsp" %>
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
</body>
</html>