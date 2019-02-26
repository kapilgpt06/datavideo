
<!doctype html>
<html>
<head>
    <title>D2V</title>
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
</head>
<body>
<div>
    <h1>Channels</h1>
    <div class="padding-top-30">

        <table>
            <tr><th>Id </th>
                <th>Name</th>
                <th>owner</th>
            </tr>
            <g:each in="${channelList}" var="channel" >
                    <tr>
                        <td>${channel.channelId}</td>
                        <td>
                            <g:link controller="channel" action="uploadSheet" params="[channelId:channel.channelId]" style="cursor: pointer">
                                ${channel.channelName}
                            </g:link>
                        </td>
                        <td>${channel.ownerEmail}</td>
                    </tr>
            </g:each>
        </table>
    </div>

    <g:if test='${flash.message}'>
        <div class="login_message">${flash.message}</div>
    </g:if>
    <g:link controller="channel" action="index">
        <button type="button" class="btn btn-primary">Add Channel</button>
    </g:link>
</div>
</body>
</html>

