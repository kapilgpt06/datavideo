<!doctype html>
<html>
<head>
    <title>D2V</title>
    <meta name="layout" content="main">
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
</head>
<body>
<div>
    <center>
        <br><br>
        <g:link controller="login" action="index">
            <button type="button" class="btn btn-primary">LOGIN WITH GOOGLE</button>
        </g:link>
        <br>
        <g:if test='${flash.message}'>
            <div style="color: #4cae4c" class="login_message">${flash.message}</div>
        </g:if>
    </center>
</div>
</body>
</html>
