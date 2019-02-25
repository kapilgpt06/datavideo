<g:if test='${flash.message}'>
    <div class="login_message">${flash.message}</div>
</g:if>
<g:link controller="channel" action="index">
    <button type="button" class="btn btn-primary">Add Channel</button>
</g:link>
