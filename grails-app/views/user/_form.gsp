<%@ page import="org.eady.User" %>



<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="user.username.label" default="Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${userInstance?.username}"/>

</div>

<g:if test="${!currentUser || currentUser?.id == userInstance?.id}">
    <div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
        <label for="password">
            <g:message code="user.password.label" default="Password" />
            <span class="required-indicator">*</span>
        </label>
        <g:passwordField name="password" required="" value="${userInstance?.password}" />

    </div>
</g:if>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'firstName', 'error')} ">
	<label for="firstName">
		<g:message code="user.firstName.label" default="First Name" />
		
	</label>
	<g:textField name="firstName" value="${userInstance?.firstName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'lastName', 'error')} ">
    <label for="lastName">
        <g:message code="user.lastName.label" default="Last Name" />

    </label>
    <g:textField name="lastName" value="${userInstance?.lastName}"/>

</div>

<g:if test="${currentUser}">
<div class="fieldcontain ${hasErrors(bean: userInstance?.userRealm, field: 'title', 'error')} ">
    <label for="userRealm.title">
        <g:message code="user.title.label" default="Title" />
    </label>
    <g:textField name="userRealm.title" value="${userInstance?.userRealm?.title}"/>
    <span class="field-desc">(Position, Address, Lot#, Title, etc.)</span>
</div>
</g:if>

<g:if test="${currentUser && currentUser.admin}">
<div class="fieldcontain ${hasErrors(bean: userInstance?.userRealm, field: 'numberOfVotes', 'error')} ">
    <label for="userRealm.numberOfVotes">
        <g:message code="user.votes.label" default="Number of votes" />
    </label>
    <g:field type="number" name="userRealm.numberOfVotes" value="${userInstance?.userRealm?.numberOfVotes}"/>
    <span class="field-desc">(May Represent numerous people, properties, etc.)
        <br/>
        (Set to zero if a non-voting observer)
    </span>
</div>
</g:if>

<g:if test="${currentUser}">
    <div class="fieldcontain">
        <label for="userRealm.issueNotificationEnabled">
            Issue Notification
        </label>
        <g:checkBox name="userRealm.issueNotificationEnabled" value="${userInstance?.userRealm.issueNotificationEnabled}" />
        <span class="field-desc">(Notify when issues are created.)</span>
    </div>

    <div class="fieldcontain">
        <label for="userRealm.completedNotificationEnabled">
            Completed Notification
        </label>
        <g:checkBox name="userRealm.completedNotificationEnabled" value="${userInstance?.userRealm.completedNotificationEnabled}" />
        <span class="field-desc">(Notify only when issues are passed.)</span>
    </div>

</g:if>

<g:if test="${currentUser}">
    <div class="fieldcontain">
        <label for="userRealm.commentNotificationEnabled">
            Comment Notification
        </label>
        <g:checkBox name="userRealm.commentNotificationEnabled" value="${userInstance?.userRealm.commentNotificationEnabled}" />
        <span class="field-desc">(Notify when comments are created.)</span>
    </div>
</g:if>

<g:if test="${currentUser && currentUser.admin}">
    <div class="fieldcontain">
        <label for="userRealm.admin">
            Administrator
        </label>
        <g:checkBox name="userRealm.admin" value="${userInstance?.userRealm.admin}" />
    </div>
</g:if>
<g:if test="${currentUser && currentUser.admin}">
    <div class="fieldcontain">
        <label for="userRealm.chairman">
            Chairman
        </label>
        <g:checkBox name="userRealm.chairman" value="${userInstance?.userRealm.chairman}" />
        <span class="field-desc">(Can enable voting and table issues.)</span>
    </div>
</g:if>

<g:if test="${currentUser && (currentUser.admin || currentUser.id == userInstance.id)}">
    <div class="fieldcontain">
        <label for="userRealm.active">
            Active
        </label>
        <g:checkBox id="userRealmActive" name="userRealm.active" value="${userInstance?.userRealm.active}"
            class="${userInstance?.userRealm.active}"
        />
    </div>
    <script>
        $(function() {
            $("#userRealmActive").click(
                function(ev){
                    if($('#userRealmActive').hasClass('true')){
                        if(!confirm("Are you sure? This will disable access to this organization.")){
                            ev.preventDefault();
                            return;
                        }else{
                            $('#userRealmActive').removeClass('true');
                        }
                    }else{
                        $('#userRealmActive').addClass('true');
                    }
                }
            );
        });
    </script>
</g:if>

