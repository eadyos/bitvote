
<%@ page import="org.eady.Issue" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'issue.label', default: 'Issue')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-issue" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
            <div style="float: right;">
                <g:form action="search">
                    <g:if test="${params.crit}">
                        <g:link action="index" style="width: 30px;float: none; display: inline-block">clear</g:link>
                    </g:if>
                    <g:hiddenField name="inactive" value="${activeOnly ? 'no':'yes'}"/>
                    <g:textField name="crit" value="${params.crit}" style="width: 180px"></g:textField>
                    <g:submitButton name="search"></g:submitButton>
                </g:form>
            </div>
			<ul>
				%{--<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--}%
                <g:if test="${currentUser?.verified}">
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                    <li><g:link class="list" controller="user" action="index">Members</g:link></li>
                </g:if>
                <g:if test="${currentUser?.isAdmin()}">
                    <li><g:link class="list" controller="realm" action="show" id="${currentUser.selectedRealm.id}">Manage Account</g:link></li>
                </g:if>
			</ul>
		</div>
		<div id="list-issue" class="content scaffold-list" role="main">

            <g:if test="${activeOnly}">
                <h1>Active Issues
                <g:link action="index" params="['inactive': 'yes']">Show Inactive</g:link>
                </h1>
            </g:if>
            <g:else>
                <h1>Inactive Issues
                <g:link action="index">Show Active</g:link>
                </h1>
            </g:else>

			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
            <g:if test="${request.statusMessage}">
                <div class="message" role="status">${request.statusMessage}</div>
            </g:if>
			<table>
			<thead>
					<tr>
                        <g:sortableColumn property="created" title="${message(code: 'issue.created.label', default: 'Created')}" />

                        <g:sortableColumn property="name" title="${message(code: 'issue.name.label', default: 'Name')}" />

						<th><g:message code="issue.proposedBy.label" default="Proposed By" /></th>
					
						<g:sortableColumn property="status" title="${message(code: 'issue.status.label', default: 'Status')}" />

                        <g:sortableColumn property="statusDate" title="${message(code: 'issue.completed.label', default: 'Status Date')}" />

					</tr>
				</thead>
				<tbody>
				<g:each in="${issueInstanceList}" status="i" var="issueInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td>${issueInstance.dateCreated.format('MM/dd/yyyy')}</td>

                        <td><g:link action="show" id="${issueInstance.id}">${fieldValue(bean: issueInstance, field: "name")}</g:link></td>

						<td>${fieldValue(bean: issueInstance, field: "proposedBy")}</td>
					
						<td>
                            <span class="${issueInstance.status}">${issueInstance.status.name}</span>
                        </td>

                        <td>${issueInstance.statusDate?.format('MM/dd/yyyy')}</td>       <h1

					</tr>
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td colspan="5">
                            <div class="description">
                                <label>Description:</label>
                                <g:lines string="${issueInstance?.description}"/>
                            </div>
                        </td>
                    </tr>
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td colspan="3">
                            <div class="comment">
                                <sec:ifLoggedIn>
                                    <g:form url="[resource:comment, action:'createComment']" >
                                        <g:hiddenField name="issueId" value="${issueInstance.id}"/>
                                        <g:hiddenField name="inactive" value="${activeOnly ? 'no':'yes'}"/>
                                        <g:textArea name="text" class="commentText"></g:textArea>
                                        %{--<g:textField name="text" class="commentText"></g:textField>--}%
                                        <g:submitButton name="create" class="save" value="${message(code: 'comment.button.create.label', default: 'Add Comment')}" />
                                    </g:form>
                                </sec:ifLoggedIn>
                                <g:each in="${issueInstance.comments.sort{it.dateCreated}.reverse()}" var="comment">
                                    <div>
                                        <span class="subLabel">${comment.user} - ${comment.dateCreated.format("MM/dd/yyyy - h:mm aaa")}</span>
                                        <g:lines string="${comment.text}"/>
                                    </div>
                                </g:each>
                            </div>
                        </td>
                        <td colspan="2" class="votesTd">
                            <div class="comment">
                                <div>
                                    <g:if test="${issueInstance.realm.quorum && !issueInstance.quorumReached}">
                                        <g:if test="${issueInstance.votes}">
                                            <div class="voteWrap">
                                                <span class="voteLabel">Votes Cast (quorum not reached)</span>
                                                <g:each in="${issueInstance.votes}" var="vote">
                                                    <span class="unknown">${vote.user}${vote.user.userRealm.numberOfVotes > 1 ? " (x$vote.user.userRealm.numberOfVotes)":''}</span>
                                                </g:each>
                                            </div>
                                        </g:if>
                                    </g:if>
                                    <g:else>
                                        <g:if test="${issueInstance.yayVotes}">
                                            <div class="voteWrap">
                                                <span class="voteLabel">Yays (${issueInstance.yayVotes.sum{it.weight}})</span>
                                                <g:each in="${issueInstance.yayVotes}" var="vote">
                                                    <span class="Approved">${vote.user}${vote.user.userRealm.numberOfVotes > 1 ? " (x$vote.user.userRealm.numberOfVotes)":''}</span>
                                                </g:each>
                                            </div>
                                        </g:if>
                                        <g:if test="${issueInstance.nayVotes}">
                                            <div class="voteWrap">
                                                <span class="voteLabel">Nays (${issueInstance.nayVotes.sum{it.weight}})</span>
                                                <g:each in="${issueInstance.nayVotes}" var="vote">
                                                    <span class="Rejected">${vote.user}${vote.user.userRealm.numberOfVotes > 1 ? " (x$vote.user.userRealm.numberOfVotes)":''}</span>
                                                </g:each>
                                            </div>
                                        </g:if>
                                        <g:if test="${issueInstance.abstainVotes}">
                                            <div class="voteWrap">
                                                <span class="voteLabel">Abstentions (${issueInstance.abstainVotes.sum{it.weight}})</span>
                                                <g:each in="${issueInstance.abstainVotes}" var="vote">
                                                    <span class="abstain">${vote.user}${vote.user.userRealm.numberOfVotes > 1 ? " (x$vote.user.userRealm.numberOfVotes)":''}</span>
                                                </g:each>
                                            </div>
                                        </g:if>
                                    </g:else>
                                </div>
                                <div class="voteDetails">
                                    <span>Votes cast: ${issueInstance.votesCastCount}</span>
                                    <g:if test="${!issueInstance.completed}">
                                        <g:if test="${issueInstance.realm.quorum}">
                                            <span>Required for quorum: ${issueInstance.votesNeededForQuorum}</span>
                                        </g:if>
                                        <g:if test="${!issueInstance.realm.quorum || issueInstance.quorumReached}">
                                            <span>Required for passage: ${issueInstance.votesNeededToPass}</span>
                                        </g:if>
                                    </g:if>
                                </div>
                                <sec:ifLoggedIn>
                                    <div class="options">
                                        <g:if test="${issueInstance.status == org.eady.Issue.Status.Discuss && currentUser.chairman}">
                                            <g:link class="button-link green" action="enableVoting" id="${issueInstance.id}">Enable Voting</g:link>
                                            <g:link class="white button-link" action="tableIssue" id="${issueInstance.id}">Table Issue</g:link>
                                        </g:if>
                                        <g:elseif test="${issueInstance.status == org.eady.Issue.Status.Tabled && currentUser.chairman}">
                                            <g:link class="button-link white" action="untableIssue" id="${issueInstance.id}">Re-Open Issue</g:link>
                                        </g:elseif>
                                        <g:elseif test="${issueInstance.status in org.eady.Issue.Status.votingStatuses}">
                                            <g:if test="${issueInstance.hasVoted(currentUser) && !issueInstance.completed}">
                                                <g:link class="button-link" action="vote" id="${issueInstance.id}" params="[vote: 'cancel', userid: currentUser.id]">Undo Vote</g:link></span>
                                            </g:if>
                                            <g:if test="${!issueInstance.hasVoted(currentUser) && currentUser.userRealm.numberOfVotes > 0}">
                                                <br/>
                                                <g:link class="button-link green" action="vote" id="${issueInstance.id}" params="[vote: 'yes', userid: currentUser.id]">Vote Yes</g:link>
                                                <g:link class="white button-link" action="vote" id="${issueInstance.id}" params="[vote: 'abstain', userid: currentUser.id]">Abstain</g:link>
                                                <g:link class="red button-link" action="vote" id="${issueInstance.id}" params="[vote: 'no', userid: currentUser.id]">Vote No</g:link>
                                            </g:if>
                                        </g:elseif>
                                    </div>
                                </sec:ifLoggedIn>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <hr/>
                        </td>
                    </tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${issueInstanceCount ?: 0}" params="[inactive: activeOnly ? 'no':'yes']" />
			</div>
		</div>
	</body>
</html>
