
<%@ page import="org.eady.Realm" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'realm.label', default: 'Realm')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-realm" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-realm" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="active" title="${message(code: 'realm.active.label', default: 'Active')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'realm.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="neededVotes" title="${message(code: 'realm.neededVotes.label', default: 'Needed Votes')}" />
					
						<g:sortableColumn property="visible" title="${message(code: 'realm.visible.label', default: 'Visible')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${realmInstanceList}" status="i" var="realmInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${realmInstance.id}">${fieldValue(bean: realmInstance, field: "active")}</g:link></td>
					
						<td>${fieldValue(bean: realmInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: realmInstance, field: "neededVotes")}</td>
					
						<td><g:formatBoolean boolean="${realmInstance.visible}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${realmInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
