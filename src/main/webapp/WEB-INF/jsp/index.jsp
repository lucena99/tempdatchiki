<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<html lang="en">
<head>
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.7/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap-datepicker/1.0.1/css/datepicker.css" />

    <link href="css/main.css" rel="stylesheet" />
    <link href="css/custom.css" rel="stylesheet" />

    <script type="text/javascript" src="js/custom.js"></script>
    <script type="text/javascript" src="webjars/jquery/1.9.1/jquery.min.js"></script>
    <script type="text/javascript" src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="webjars/bootstrap-datepicker/1.0.1/js/bootstrap-datepicker.js"></script>
</head>
<body>
	<nav class="navbar navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Главная</a>
			</div>
			<div class="navbar-header">
            	<a class="navbar-brand" href="/restapi/test">Rest-api</a>
            </div>
		</div>
	</nav>

	<div class="container">
		<div class="starter-template">
			<h1>Температурные датчики</h1>
		</div>

		<div class="row text-center"><strong> Доступные датчики</strong></div>
            <c:forEach var="s" items="${sensors}">
                <div class="row" style="border:1px solid green;padding:10px">
                    <div class="col-md-4 text-center">${s.uid}</div>
                    <div class="col-md-4 text-center" >${s.name}</div>
                    <div class="col-md-4 text-center" >${s.createdDatetime}</div>
                </div>
            </c:forEach>
	    </div>
	</div>
</body>

</html>