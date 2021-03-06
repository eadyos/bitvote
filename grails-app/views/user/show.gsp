
<%@ page import="org.eady.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'Member')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                <li><a class="list" href="${createLink(controller: 'issue', action: 'index')}">Issue List</a></li>
                <li><a class="list" href="${createLink(controller: 'user', action: 'index')}">Members</a></li>
				%{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-user" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="['Member']" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list user">
			
				<g:if test="${userInstance?.id == currentUser.id || currentUser.admin}">
				<li class="fieldcontain">
					<span id="username-label" class="property-label"><g:message code="user.username.label" default="Username" /></span>
					
						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${userInstance}" field="username"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.accountExpired}">
				<li class="fieldcontain">
					<span id="accountExpired-label" class="property-label"><g:message code="user.accountExpired.label" default="Account Expired" /></span>
					
						<span class="property-value" aria-labelledby="accountExpired-label"><g:formatBoolean boolean="${userInstance?.accountExpired}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.accountLocked}">
				<li class="fieldcontain">
					<span id="accountLocked-label" class="property-label"><g:message code="user.accountLocked.label" default="Account Locked" /></span>
					
						<span class="property-value" aria-labelledby="accountLocked-label"><g:formatBoolean boolean="${userInstance?.accountLocked}" /></span>
					
				</li>
				</g:if>
			
				<li class="fieldcontain">
					<span id="firstName-label" class="property-label"><g:message code="user.firstName.label" default="First Name" /></span>
					
						<span class="property-value" aria-labelledby="firstName-label"><g:fieldValue bean="${userInstance}" field="firstName"/></span>
					
				</li>

                <li class="fieldcontain">
                    <span id="lastName-label" class="property-label"><g:message code="user.lastName.label" default="Last Name" /></span>

                    <span class="property-value" aria-labelledby="lastName-label"><g:fieldValue bean="${userInstance}" field="lastName"/></span>

                </li>

                <li class="fieldcontain">
                    <span id="title-label" class="property-label"><g:message code="user.title.label" default="Title" /></span>

                    <span class="property-value" aria-labelledby="lastName-label"><g:fieldValue bean="${userInstance}" field="userRealm.title"/>&nbsp;</span>

                    <span class="property-desc">(Position, Address, Lot#, Title, etc.)</span>
                </li>

                <li class="fieldcontain">
                    <span id="votes-label" class="property-label"><g:message code="user.votes.label" default="Number of votes" /></span>

                    <span class="property-value" aria-labelledby="votes-label"><g:fieldValue bean="${userInstance}" field="userRealm.numberOfVotes"/></span>

                    <span class="property-desc">(May Represent numerous people, properties, etc.)
                        <br/>(Zero if a non-voting observer)
                    </span>
                </li>

                <li class="fieldcontain">
                    <span id="isAdmin-label" class="property-label"><g:message code="user.admin.label" default="Administrator" /></span>

                    <span class="property-value" aria-labelledby="isAdmin-label">
                        ${userInstance?.isAdmin() ? 'Yes' : 'No'}
                    </span>
                </li>

                <li class="fieldcontain">
                    <span id="isChairman-label" class="property-label"><g:message code="user.admin.label" default="Chairman" /></span>

                    <span class="property-value" aria-labelledby="isChairman-label">
                        ${userInstance?.isChairman() ? 'Yes' : 'No'}
                    </span>
                    <span class="property-desc">(Can enable voting and table issues.)</span>
                </li>

                <g:if test="${userInstance?.id == currentUser.id || currentUser.admin}">
                    <li class="fieldcontain">
                        <span id="issueNotification-label" class="property-label"><g:message code="user.issueNotification.label" default="Issue Notification" /></span>

                        <span class="property-value" aria-labelledby="issueNotification-label">
                            ${userInstance?.userRealm?.issueNotificationEnabled ? 'Yes' : 'No'}
                        </span>
                    </li>

                    <li class="fieldcontain">
                        <span id="commentNotification-label" class="property-label"><g:message code="user.commentNotification.label" default="Comment Notification" /></span>

                        <span class="property-value" aria-labelledby="commentNotification-label">
                            ${userInstance?.userRealm?.commentNotificationEnabled ? 'Yes' : 'No'}
                        </span>
                    </li>

                </g:if>

                <li class="fieldcontain">
                    <span id="lastName-label" class="property-label"><g:message code="user.lastName.label" default="Active" /></span>

                    <span class="property-value" aria-labelledby="lastName-label">
                        ${userInstance?.isActive() ? 'Yes' : 'No'}
                    </span>
                </li>

                <g:if test="${userInstance?.passwordExpired}">
				<li class="fieldcontain">
					<span id="passwordExpired-label" class="property-label"><g:message code="user.passwordExpired.label" default="Password Expired" /></span>
					
						<span class="property-value" aria-labelledby="passwordExpired-label"><g:formatBoolean boolean="${userInstance?.passwordExpired}" /></span>
					
				</li>
				</g:if>
			
			</ol>
            <fieldset class="buttons">
                <g:if test="${currentUser.id == userInstance.id || currentUser.admin}">
                    <g:link class="edit" action="edit" resource="${userInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                </g:if>
            </fieldset>
		</div>
	</body>
</html>
