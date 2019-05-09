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
				<a class="navbar-brand" href="/">Главная</a>
			</div>
			<div class="navbar-header">
            	<a class="navbar-brand" href="/swagger-ui.html">Swagger Rest-api</a>
            </div>
		</div>
	</nav>

    <div class="container">
		<div class="starter-template">
			<h1>Температурные датчики</h1>
		</div>
	</div>

	<div class="container">
		<div class="row text-center"><strong>Доступные получатели</strong></div>
        <div class="row" style="border:1px solid green;padding:10px">
                <div class="col-md-4 text-center" style="background:#C0C0C0;">uid</div>
                <div class="col-md-3 text-center" style="background:#C0C0C0;">Наименование</div>
                <div class="col-md-3 text-center" style="background:#C0C0C0;" >Дата-время созд</div>
            </div>
        <c:forEach var="r" items="${recipients}">
            <div class="row" style="border:1px solid green;padding:10px">
                <div class="col-md-4 text-left">${r.uid}</div>
                <div class="col-md-3 text-left" >${r.name}</div>
                <div class="col-md-3 text-left" >${r.createdDatetime}</div>
            </div>
        </c:forEach>
	</div>
	<br/>

	<div class="container">
        <div class="row text-center"><strong>Доступные контроллеры</strong></div>
        <div class="row" style="border:1px solid sienna;padding:10px">
                <div class="col-md-4 text-center" style="background:#C0C0C0;">uid</div>
                <div class="col-md-3 text-center" style="background:#C0C0C0;">Наименование</div>
                <div class="col-md-3 text-center" style="background:#C0C0C0;" >Дата-время созд</div>
            </div>
        <c:forEach var="c" items="${controllers}">
            <div class="row" style="border:1px solid sienna;padding:10px">
                <div class="col-md-4 text-left">${c.uid}</div>
                <div class="col-md-3 text-left" >${c.name}</div>
                <div class="col-md-3 text-left" >${c.createdDatetime}</div>
            </div>
        </c:forEach>
    </div>
    <br/>

    <div class="container">
        <div class="row text-center"><strong>Доступные датчики</strong></div>
        <div class="row" style="border:1px solid orange;padding:10px">
            <div class="col-md-4 text-center" style="background:#C0C0C0;">uid</div>
            <div class="col-md-2 text-center" style="background:#C0C0C0;">Наименование</div>
            <div class="col-md-2 text-center" style="background:#C0C0C0;">Контроллер</div>
            <div class="col-md-3 text-center" style="background:#C0C0C0;" >Дата-время созд</div>
        </div>
        <c:forEach var="s" items="${sensors}">
            <div class="row" style="border:1px solid orange;padding:10px">
                <div class="col-md-4 text-left">${s.uid}</div>
                <div class="col-md-2 text-left" >${s.name}</div>
                <div class="col-md-2 text-left" >${s.controller.name}</div>
                <div class="col-md-3 text-left" >${s.createdDatetime}</div>
            </div>
        </c:forEach>
    </div>
    <br/>

    <div class="container">
        <div class="row text-center"><strong>Подписки</strong></div>
        <div class="row" style="border:1px solid blue;padding:10px">
            <div class="col-md-4 text-center" style="background:#C0C0C0;">uid</div>
            <div class="col-md-3 text-center" style="background:#C0C0C0;">Получатель</div>
            <div class="col-md-2 text-center" style="background:#C0C0C0;" >Контроллер</div>
            <div class="col-md-2 text-center" style="background:#C0C0C0;" >Превышение?</div>
            <div class="col-md-1 text-center" style="background:#C0C0C0;" >Ошибка?</div>
        </div>
        <c:forEach var="s" items="${subscriptions}">
            <div class="row" style="border:1px solid blue;padding:10px">
                <div class="col-md-4 text-left">${s.uid}</div>
                <div class="col-md-3 text-left" >${s.recipient.name}</div>
                <div class="col-md-2 text-left" >${s.controller.name}</div>
                <div class="col-md-2 text-left" >${s.notifyOver}</div>
                <div class="col-md-1 text-left" >${s.notifyError}</div>
            </div>
        </c:forEach>
    </div>
</body>

</html>