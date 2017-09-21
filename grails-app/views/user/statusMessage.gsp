
<%@ page import="org.eady.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Status Message</title>
</head>
<body>
<a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        %{--<li><a class="home" href="${createLink(controller: 'issue', action: 'index')}">Issue List</a></li>--}%
        %{--<li><a class="list" href="${createLink(controller: 'user', action: 'index')}">Members</a></li>--}%
        %{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
        %{--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
    </ul>
</div>
<div id="show-user" class="content scaffold-show" role="main">
    <h1>Status Message</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

</div>
</body>
</html>
