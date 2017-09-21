
<%@ page import="org.eady.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                %{--<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--}%
                <li><a class="list" href="${createLink(controller: 'issue', action: 'index')}">Issue List</a></li>
                <g:if test="${currentUser?.admin}">
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="['Member']" /></g:link></li>
                    <li><g:link class="create" controller="realm" action="selfSignup">Manage Self-sign-up</g:link></li>
                </g:if>
			</ul>
		</div>
		<div id="list-user" class="content scaffold-list" role="main">
            <g:if test="${activeOnly}">
                <h1>Active <g:message code="default.list.label" args="['Member']" />
                    <g:link action="index" params="['inactive': 'yes']">Show Inactive</g:link>
                </h1>
            </g:if>
            <g:else>
                <h1>Inactive <g:message code="default.list.label" args="['Member']" />
                    <g:link action="index">Show Active</g:link>
                </h1>
            </g:else>


            <g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>

                        <g:if test="${currentUser?.admin}">
                            <g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
                        </g:if>

                        <g:sortableColumn property="firstName" title="${message(code: 'user.name.label', default: 'Name')}" />

                        <th>${message(code: 'user.title.label', default: 'Title')}</th>

                        <th>${message(code: 'user.role.label', default: 'Role')}</th>

                        <th>Verified</th>

                    </tr>
				</thead>
				<tbody>
				<g:each in="${userInstanceList}" status="i" var="userInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                        <g:if test="${currentUser?.admin}">
    						<td>${fieldValue(bean: userInstance, field: "username")}</td>
	                    </g:if>

						<td><g:link action="show" id="${userInstance.id}">${userInstance.firstName + ' ' + userInstance.lastName}</g:link></td>

                        <td>${userInstance?.userRealm.title}</td>

                        <td>${userInstance?.roles}</td>

                        <td>
                            <g:if test="${userInstance?.verified}">
                                yes
                            </g:if>
                            <g:else>
                                <button id="${userInstance?.id}" class="verifyUser" name="Accept User">Verify Member</button>
                                <button id="${userInstance?.id}" class="rejectUser" name="Accept User">Reject Member</button>
                            </g:else>
                        </td>

                    </tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${userInstanceCount ?: 0}" params="[inactive: activeOnly ? 'no':'yes']" />
			</div>
		</div>
<script>
    $(function() {

        $(".verifyUser").click(
                function(ev){
                    if(!confirm("Are you sure? This will ENABLE access to this organization.")){
                        ev.preventDefault();
                        return;
                    }else{
                        var id = $(this).attr('id');
                        window.location = "${g.createLink(absolute: true, controller: 'user', action: 'verifyUser')}" + '?id=' + id;
                    }
                }
        );
        $(".rejectUser").click(
                function(ev){
                    if(!confirm("Are you sure? This will DISABLE access to this organization.")){
                        ev.preventDefault();
                        return;
                    }else{
                        var id = $(this).attr('id');
                        window.location = "${g.createLink(absolute: true, controller: 'user', action: 'rejectUser')}" + '?id=' + id;
                    }
                }
        );



    });

</script>

	</body>
</html>
