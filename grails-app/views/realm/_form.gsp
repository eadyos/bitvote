<%@ page import="org.eady.Realm" %>


<div class="fieldcontain" style="left: 50px; position: relative;">
	<label for="realmName" style="font-weight: bold;">
		<g:message code="realm.name.label" default="Name" />
		
	</label>
	<g:textField id="realmName" class="${hasErrors(bean: realmInstance, field: 'name', 'error')}" name="name" value="${realmInstance?.name}"/>
</div>

<g:if test="${realmInstance.id}">
    <div class="fieldcontain"  style="left: 50px; position: relative;">
        <label for="active" style="font-weight: bold;">
            <g:message code="realm.active.label" default="Organization Active" />

        </label>
        <g:checkBox id="active" name="active" value="${realmInstance.active}"></g:checkBox>

    </div>
</g:if>

<div class="votingSystemTypeWrapper">
    <h4>Choose your voting system rules</h4>
    <div class="vsys ${realmInstance && realmInstance.quorum ? 'boxy':'boxySelected'}">

        <h5>Precise Majority
            <g:if test="${realmInstance && realmInstance.quorum}">
                <g:radio id="voteTypeMajority" name="voteType" value="majority"></g:radio>
            </g:if>
            <g:else>
                <g:radio id="voteTypeMajority" name="voteType" value="majority" checked="checked"></g:radio>
            </g:else>
        </h5>

        <div>
            <label for="neededVotes">
                <g:message code="realm.neededVotes.label" default="Needed Votes" />
            </label>

            <g:field id="neededVotes" name="neededVotes" type="number" value="${realmInstance.neededVotes}" required=""
                     disabled="${realmInstance.majority ? 'disabled' :'false'}" class="realmValue" />
            <br/>
            <span class="field-desc">(to pass or reject issues)</span>

        </div>

        <div style="margin-top: 10px;">
            <label for="majority" style="text-align: left; width: 220px;">Majority of all voting members</label>
            <g:checkBox id="majority" name="majority" value="${realmInstance.majority}"></g:checkBox>

        </div>

        <div class="instructions">
            <p>
                In a precise majority setup, an issue will pass or reject if the issue receives the
                specified number of votes for or against an issue.
            </p>
            <p>
                If the "majority of all voting members" option is selected, then the needed votes to pass or reject
                an issue will be dependent on the number of active voting members in an organization.
                For example, if an organization has 20 voting members, than an issue will pass or reject once it
                receives 11 votes for or against the issue.
            </p>
        </div>


    </div>
    <div class="vsys ${realmInstance && realmInstance.quorum ? 'boxySelected':'boxy'}">

        <h5>Quorum Based
            <g:if test="${realmInstance && realmInstance.quorum}">
                <g:radio id="voteTypeQuorum" name="voteType" value="quorum" checked="checked"></g:radio>
            </g:if>
            <g:else>
                <g:radio id="voteTypeQuorum" name="voteType" value="quorum"></g:radio>
            </g:else>
        </h5>

        <div class="quorumField">
            <label for="quorumPercentage">
                <g:message code="realm.quorumPercentage.label" default="Needed Percentage" />
            </label>

            <g:field id="quorumPercentage" name="quorumPercentage" type="number" value="${realmInstance.quorumPercentage}"
                     class="realmValue" min="1" max="100"    />

            <g:if test="${realmInstance && !realmInstance.quorumCount}">
                <g:radio id="quorumTypePercent" name="quorumType" value="percent" checked="checked"></g:radio>
            </g:if>
            <g:else>
                <g:radio id="quorumTypePercent" name="quorumType" value="percent"></g:radio>
            </g:else>
            <br/>

        </div>

        <div class="quorumField">
            <label for="quorumCount">
                <g:message code="realm.quorumCount.label" default="Needed Votes" />
            </label>

            <g:field id="quorumCount" name="quorumCount" type="number" value="${realmInstance.quorumCount}"
                     class="realmValue disabled" />
            <g:if test="${realmInstance && realmInstance.quorumCount}">
                <g:radio id="quorumTypeNumber" name="quorumType" value="number" checked="checked"></g:radio>
            </g:if>
            <g:else>
                <g:radio id="quorumTypeNumber" name="quorumType" value="number"></g:radio>
            </g:else>

            <br/>
            <span class="field-desc">(to reach a quorum of members)</span>

        </div>

        <div class="instructions" style="position: relative; top: -12px;">
            <p>
                In a quorum based setup, an issue will qualify for passing or rejecting once a quorum of members
                has voted on the issue.  Once a quorum of votes is reached, the issue will be "pending" passage
                or rejection for 48-hours.  Members who have not yet voted on the issue will be notified of the
                pending status and be given a chance to vote on the issue.  As long as an isssue is receiving votes,
                its pending status will remain until there is a 48-hour period of no votes or a 1 week time has
                passed since the issue was opened for voting.
            </p>
        </div>


    </div>

</div>


<script>
    $(function() {
        $('#majority').change(function(){
            if($('#neededVotes').attr('disabled') == 'disabled'){
                $('#neededVotes').removeAttr('disabled');
            }else{
                $('#neededVotes').attr('disabled', 'disabled');
            }
        });

        $('#quorumPercentage').focus(function(){
            $(this).removeClass('disabled');
            $('#quorumTypePercent').prop('checked', true);
            $('#quorumCount').val('').addClass('disabled');
        });
        $('#quorumCount').focus(function(){
            $(this).removeClass('disabled');
            $('#quorumTypeNumber').prop('checked', true);
            $('#quorumPercentage').val('').addClass('disabled');
        });
        $('div.vsys').click(function(){
            if($(this).hasClass('boxy')){
                var oldSelected = $('div.boxySelected');
                var newSelected = $('div.boxy');
                oldSelected.removeClass('boxySelected').addClass('boxy');
                var radioSelector = $("input[type='radio'][name='voteType']");
                newSelected.removeClass('boxy').addClass('boxySelected').find(radioSelector).prop('checked', true);
            }
        });

    });
</script>

