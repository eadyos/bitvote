<%@ page import="org.eady.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>
<a href="#edit-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="list" href="${createLink(controller: 'issue', action: 'index')}">Issue List</a></li>
        %{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
        %{--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
    </ul>
</div>
<div id="edit-user" class="content scaffold-edit" role="main">
    <g:if test="${activeOnly}">
        <h1>Select Organization
        <g:link action="changeRealm" params="['inactive': 'yes']">Show Inactive</g:link>
        </h1>
    </g:if>
    <g:else>
        <h1>Select Inactive Organization
        <g:link action="changeRealm">Show Active</g:link>
        </h1>
    </g:else>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${userInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${userInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <fieldset class="form">
        <g:render template="changeRealmForm"/>
    </fieldset>
    <fieldset class="buttons">
        <g:actionSubmit class="save" action="changeRealmSave" value="${message(code: 'default.button.update.label', default: 'Update')}" />
    </fieldset>
</div>
</body>
</html>
