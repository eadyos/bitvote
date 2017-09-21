<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
    <title>Rob's Rules: Manage Self-sign-up</title>
</head>
<body>
<a href="#create-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="list" href="${createLink(controller: 'user', action: 'index')}">Issue List</a></li>
        <li><a class="list" href="${createLink(controller: 'user', action: 'index')}">Members</a></li>
    </ul>
</div>
<div id="create-user" class="content scaffold-create" role="main">
    <h1>Join Your Organization</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${realmInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${realmInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <div style="margin: 10px 40px 60px 40px;">
        <p>
            Please enter the organization identifier and join-password that was given to you
            by your organization's administrator.
        </p>
        <p>
            If you do not have these credentials please contact your organization's administrator
            or have the administrator create your account for you.
        </p>
        <br/>
        <g:form controller="realm" action="joinRealmSubmit">

            <div class="fieldcontain ${hasErrors(bean: realmInstance, field: 'id', 'error')} required">
                <label for="id">
                    Organization ID
                    <span class="required-indicator">*</span>
                </label>
                <g:textField id="id" name="id" required=""/>
            </div>
            <div class="fieldcontain ${hasErrors(bean: realmInstance, field: 'joinPassword', 'error')} required">
                <label for="id">
                    Join Password
                    <span class="required-indicator">*</span>
                </label>
                <g:textField id="id" name="password" required="" value=""/>
            </div>
            <div class="fieldcontain ${hasErrors(bean: userInstance, field: 'title', 'error')}">
                <label for="username">
                    Title
                </label>
                <g:textField name="title" value="${userInstance?.title}"/>
                <span class="field-desc">(Position, Address, Lot#, Title, etc.)</span>
            </div>
            <div class="fieldcontain" style="margin-left: 330px;">
                <g:submitButton name="Submit"></g:submitButton>
            </div>
        </g:form>
    </div>

</div>
</body>
</html>
