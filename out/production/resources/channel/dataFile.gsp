
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
            <tr><th>Video Name </th>
                <th>Channel Name</th>
                <th>UploadedBy</th>
                <th>Video Created</th>
                <th>Video Uploaded</th>
                <th>video Created Time</th>
                <th>Video uploaded time</th>
                <th>video Link</th>
            </tr>
            <g:each in="${videoList}" var="video" >
                <tr>
                    <td>${video.videoName}</td>
                    <td>${video.channelName}</td>
                    <td>${video.uploadBy}</td>
                    <td>${video.videocreated}</td>
                    <td>${video.videoUploaded}</td>
                    <td>${video.videoCreatedDate}</td>
                    <td>${video.videoUploadDate}</td>
                    <td><a href="${video.videoURL}" style="cursor: pointer">${video.videoName}</a></td>
                </tr>
            </g:each>
        </table>

    </center>


</div>
</body>
</html>

