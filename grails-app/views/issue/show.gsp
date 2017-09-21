
<%@ page import="org.eady.Issue" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'issue.label', default: 'Issue')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-issue" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				%{--<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--}%
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-issue" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list issue">
			
				<g:if test="${issueInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="created-label" class="property-label"><g:message code="issue.created.label" default="Created" /></span>
					
						<span class="property-value" aria-labelledby="created-label"><g:formatDate date="${issueInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${issueInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="issue.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label">
                            <g:lines string="${issueInstance?.description}"/>
                        </span>
					
				</li>
				</g:if>
			
				<g:if test="${issueInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="issue.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${issueInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${issueInstance?.proposedBy}">
				<li class="fieldcontain">
					<span id="proposedBy-label" class="property-label"><g:message code="issue.proposedBy.label" default="Proposed By" /></span>
					
						<span class="property-value" aria-labelledby="proposedBy-label"><g:link controller="user" action="show" id="${issueInstance?.proposedBy?.id}">${issueInstance?.proposedBy?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${issueInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="issue.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${issueInstance}" field="status"/></span>
					
				</li>
				</g:if>

				<g:if test="${issueInstance?.yayVotes}">
				<li class="fieldcontain">
					<span id="yays-label" class="property-label"><g:message code="issue.yays.label" default="Yays" /></span>
					
						<g:each in="${issueInstance.yayVotes}" var="y">
						<span class="property-value" aria-labelledby="yays-label"><g:link controller="user" action="show" id="${y.user.id}">${y?.user.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>

                <g:if test="${issueInstance?.nayVotes}">
                    <li class="fieldcontain">
                        <span id="nays-label" class="property-label"><g:message code="issue.nays.label" default="Nays" /></span>

                        <g:each in="${issueInstance.nayVotes}" var="n">
                            <span class="property-value" aria-labelledby="nays-label"><g:link controller="user" action="show" id="${n?.user?.id}">${n?.user.encodeAsHTML()}</g:link></span>
                        </g:each>

                    </li>
                </g:if>

                <g:if test="${issueInstance?.abstainVotes}">
                    <li class="fieldcontain">
                        <span id="abstains-label" class="property-label"><g:message code="issue.abstains.label" default="Abstains" /></span>

                        <g:each in="${issueInstance.abstainVotes}" var="a">
                            <span class="property-value" aria-labelledby="abstains-label"><g:link controller="user" action="show" id="${a?.user?.id}">${a?.user.encodeAsHTML()}</g:link></span>
                        </g:each>

                    </li>
                </g:if>


            </ol>
			<g:form url="[resource:issueInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${issueInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
