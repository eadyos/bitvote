<%@ page import="org.eady.User" %>

<g:if test="${activeOnly}">
    <g:set var="realms" value="${userInstance.availableActiveRealms}"></g:set>
</g:if>
<g:else>
    <g:set var="realms" value="${userInstance.availableInactiveRealms}"></g:set>
</g:else>


<g:each in="${realms}" var="realm">

    <div style="margin: 8px; font-weight: bold">
        <g:link action="changeRealmSave" id="${realm.id}">${realm.name}</g:link>
    </div>

</g:each>


%{--<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'selectedRealm', 'error')} required">--}%
    %{--<label for="selectedRealm">--}%
        %{--<g:message code="user.selectedRealm.label" default="Select" />--}%
    %{--</label>--}%
    %{--<g:select name="selectedRealm" from="${userInstance.availableRealms}" optionKey="id" optionValue="name"></g:select>--}%

%{--</div>--}%

