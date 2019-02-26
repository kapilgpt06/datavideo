
<!doctype html>
<html>
<head>
    <title>D2V</title>
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
</head>
<body>
<div>
    <g:form controller="channel" action="upload" enctype="multipart/form-data" useToken="true">

        <span class="button">
            File Name
            <input type="text" value="2014" name="year" >
            <input type="text" value=${params.channelId} name="channelId" hidden>
            <input type="text" value="loksabha" name="electionType" readonly><br>
            <input type="file" name="file"/>
            <input type="submit" class="upload" value="upload"/>

        </span>

    </g:form>
</div>
</body>
</html>

