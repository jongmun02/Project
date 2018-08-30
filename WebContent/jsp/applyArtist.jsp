<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>��Ƽ��Ʈ ����������</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- CSS
================================================== -->
    <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
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
    <link rel="apple-touch-icon" sizes="72x72" href="img/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="114x114" href="img/apple-touch-icon-114x114.png">

    <!-- JS
================================================== -->
    <script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
    <script src="js/bootstrap.js"></script>
    <script src="js/jquery.custom.js"></script>

</head>

<body>
    <div class="color-bar-1"></div>
    <div class="color-bar-2 color-bg"></div>

    <div class="container main-container">

        <!-- 	header section -->
        <%@include file="header.jsp"%>


            <!-- Page Content
    ================================================== -->
            <div class="row">
                <!--Container row-->

                <div class="span12">
                    <h2 class="title-bg">��Ƽ��Ʈ ������ ������</h2>
                </div>

                <div class="span3 sidebar page-left-sidebar">
                    <!-- Begin sidebar column -->

                    <!--Navigation-->

                    <ul class="post-category-list">
                        <li><a href="modifyGuest.jsp"><i class="icon-plus-sign"></i>�Ϲ�ȸ�� ������</a></li>
                        <li><a href="applyArtist.jsp"><i class="icon-plus-sign"></i>��Ƽ��Ʈ ������</a></li>
                    </ul>
                    <br>
                    <div class="alert alert-block">������ ��Ƽ��Ʈ ������ �������Դϴ�.</div>

                </div>

                <div class="span4 contact">
                    <!--Begin page content column-->

                    <!-- 			���ǰ��� ���� �Է� �κ� -->
                    <div class="span8 container">
                        <form action="addLecture.do" id="contact-form">

                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-pencil"></i></span>
                                <input class="span6" id="id" name="id" value="${member.id }" type="text" readonly="readonly" size="16">
                            </div>

                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-pencil"></i></span>
                                <input class="span6" id="name" name="name" value="${member.name }" type="text" readonly="readonly" size="16">
                            </div>

                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-signal"></i></span>
                                <input class="span6" id="phone" name="phone" value="${member.phone }" type="text" readonly="readonly" size="16">
                            </div>

                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-home"></i></span> 
                                <input class="span6" id="state" name="state" value="${member.state }" type="text" readonly="readonly" size="16" >
                            </div>

                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-envelope"></i></span> 
                                <input class="span6" id="email" name="email" value="${member.email }" type="email" readonly="readonly" size="16"> 
                            </div>

                            <div class="input-prepend">
                                <div class="input-group date">
                                    <span class="add-on"><i class="icon-calendar"></i></span> 
                                    <input class="span6" id="birth" name="birth" value="${member.birth }" type="text" readonly="readonly" size="16">
                                </div>
                            </div>

                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-user"></i></span> 
                                <input class="span6" id="prependedInput" name="nickname" value="${member.nickname }" type="text" readonly="readonly" size="16" >
                            </div>

                            <div class="input-append">
                                <span class="add-on"><i class="icon-upload"></i>��Ʈ������</span>
                                <input class="upload-name" value="��Ƽ��Ʈ ���� Ŭ������" id="fileName" name="fileName" style="text-align: center; margin: 0 auto;">
                            </div>
                            <div class="filebox preview-image">
                                <input class="span6" id="prependedInput" name="nickname" value="${member.file }" type="text" readonly="readonly" size="16" >
                            </div>
                            <br>
                           
                            <div class="input-prepend">
                                <span class="add-on"><i class=" icon-edit"></i>�Ұ���</span><br>
                            </div>
                            <textarea class="span7" id="content" name="${member.content }" readonly="readonly"></textarea>
                            <div class="row">
                                <div class="span7">
                                    <input type="button" class="btn btn-warning pull-right" value="�������"> 
                                    <input type="submit" class="btn btn-success pull-right" value="�������">
                                </div>
                            </div>
                        </form>


                    </div>
                    <!--End page content column-->

                </div>
                <!-- End container row -->

            </div>
            <!-- End Container -->
			</div>
            <!-- 	Footer section -->
            <%@include file="footer.jsp"%>

</body>

</html>