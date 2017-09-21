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
    <h1>Manage Self-sign-up</h1>
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
    <g:if test="${!currentUser.selectedRealm.joinPassword}">
        <div style="margin: 10px 40px 60px 40px;">
            <p>
                Self-sign-up is currently disabled.
            </p>
            <p>
                To enable self-signup, please enter a join password for your organization.
            </p>
            <p>
                Users who sign-up with self-sign-up will need to be verified by an administrator
                before they will be allowed to vote or access sensitive areas of the system.
            </p>
            <br/>
            <g:form controller="realm" action="selfSignupSubmit">
                <g:hiddenField name="id" value="${currentUser.selectedRealm.id}"></g:hiddenField>
                <div class="fieldcontain ${hasErrors(bean: realmInstance, field: 'joinPassword', 'error')} ">
                    <label class="label" for="joinPassword">Join Password</label>
                    <g:textField id="joinPassword" name="joinPassword"/>
                    <g:submitButton name="Submit"></g:submitButton>
                    <br/>
                    <span class="field-desc" style="margin-left: 240px;">(Minimum 10 characters)</span>
                </div>
            </g:form>
        </div>
    </g:if>
    <g:else>
        <div style="margin: 10px 40px 60px 40px;">
            <p>
                Self-sign-up is currently enabled.
            </p>
            <p>
                The join password is: ${realmInstance.joinPassword}
            </p>
            <p>
                The organization ID is: ${realmInstance.id}
            </p>
            <g:form controller="realm" action="selfSignupSubmit">
                <g:hiddenField name="id" value="${currentUser.selectedRealm.id}"></g:hiddenField>
                <g:hiddenField name="joinPassword" value=""/>
                <g:submitButton name="Disable self-sign-up"></g:submitButton>
            </g:form>
        </div>
    </g:else>


</div>
</body>
</html>
