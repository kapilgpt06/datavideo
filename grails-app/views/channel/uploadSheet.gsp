
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
        <h1 style="color: brown">Uploaded Sheet</h1>

        <table>
            <tr><th>File Name </th>
                <th>Channel Name</th>
                <th>UploadedBy</th>
                <th>Uploaded Date</th>
                <th>Status</th>
            </tr>
            <g:each in="${dataFileList}" var="dataFile" >
                <tr>
                    <td>
                        <g:link controller="channel" action="dataFile" params="[dataFileId: dataFile.id]" style="cursor: pointer">
                            ${dataFile.fileName}</td>
                        </g:link>
                    <td>${dataFile.channelName}</td>
                    <td>${dataFile.fileUploadBy}</td>
                    <td>${dataFile.dateCreated}</td>
                    <td>${dataFile.status}</td>
                </tr>
            </g:each>
        </table>
        <br><br>
        <h2 style="color: #761c19">Sheet Format should be Like this </h2>
        <h4 style="color: #761c19">Please check before upload sheet</h4>
        <h3>Electors table</h3><asset:image src="electors.png" style="height: 130px"></asset:image>
        <h3>Candidate table</h3><asset:image src="candidate.png" style="height: 130px"></asset:image>

        <br><br><br>


        <g:form controller="channel" action="upload" enctype="multipart/form-data" useToken="true">

            <span class="button">
                File Name:
                <input type="text" value="2014" name="year" >
                <input type="text" value=${params.channelId} name="channelId" hidden>
                <input type="text" value="loksabha" name="electionType" readonly><br>
                <input type="file" name="file"/>
                <input type="submit" class="upload" value="upload"/>

            </span>

        </g:form>
    </center>


</div>
</body>
</html>

