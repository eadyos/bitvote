
<%@ page import="org.eady.Realm" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'realm.label', default: 'Realm')}" />
		<title><g:message code="default.show.label" args="['Organization']" /></title>
        <script src="https://checkout.stripe.com/checkout.js"></script>
	</head>
	<body>
		<a href="#show-realm" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="list" href="${createLink(controller: 'issue', action: 'index')}">Issue List</a></li>
                <li><g:link class="list" controller="user" action="index">Members</g:link></li>
				%{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
				%{--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
			</ul>
		</div>
		<div id="show-realm" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="['Organization']" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list realm">
			
				<g:if test="${realmInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="realm.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${realmInstance}" field="name"/></span>
					
				</li>
				</g:if>

                <li class="fieldcontain">
                    <span id="voteType-label" class="property-label"><g:message code="realm.voteType.label" default="Vote Type" /></span>

                    <span class="property-value" aria-labelledby="voteType-label">
                        <g:if test="${!realmInstance.quorum}">
                            Precise Majority
                        </g:if>
                        <g:else>
                            Quorum Based
                        </g:else>
                    </span>

                </li>

				<li class="fieldcontain">
					<span id="neededVotes-label" class="property-label"><g:message code="realm.neededVotes.label" default="Needed Votes" /></span>
					
						<span class="property-value" aria-labelledby="neededVotes-label">
                            <g:if test="${!realmInstance.quorum && realmInstance.majority}">
                                Simple Majority
                            </g:if>
                            <g:elseif test="${!realmInstance.quorum}">
                                <g:fieldValue bean="${realmInstance}" field="neededVotes"/>
                            </g:elseif>
                            <g:elseif test="${realmInstance.quorumPercentage}">
                                ${realmInstance.quorumPercentage}% of members
                            </g:elseif>
                            <g:else>
                                ${realmInstance.quorumCount} members
                            </g:else>
                        </span>
					
				</li>


                <li class="fieldcontain">
                    <span id="active-label" class="property-label"><g:message code="realm.active.label" default="Organization Active" /></span>

                    <span class="property-value" aria-labelledby="active-label">${realmInstance?.active ? 'Yes': 'No'}</span>

                </li>

                <li class="fieldcontain">
                    <span id="active-label" class="property-label"><g:message code="realm.expiry.label" default="Organization Expiration" /></span>

                    <span class="property-value" aria-labelledby="active-label"><g:formatDate date="${realmInstance?.expiryDate}" format="MM/dd/yyyy" /></span>

                </li>

            </ol>

            <g:if test="${realmInstance?.isExpiringSoon() }">

                <div id="status" style="text-align: left; color: darkgreen; font-weight: bold; float: none; width: 550px; margin-bottom: 40px; margin-top: 12px; margin-left: 100px;">

                        <g:if test="${realmInstance?.isNew() }">
                            The Rob's Rules Voting System is free to use for 1 month.
                            <br/><br/>
                            If you find the system helpful to your organization,
                            please consider purchasing a subscription
                            to help support continued enhancements.
                            <br/><br/>
                            Thanks!
                        </g:if>
                        <g:else>
                            The Rob's Rules Voting System membership is expiring soon.
                            <br/><br/>
                            If you find the system helpful to your organization,
                            please consider purchasing a subscription
                            to help support continued enhancements.
                            <br/><br/>
                            Thanks!
                        </g:else>


                    <div style="margin: 20px 0px 12px 6px;">

                        <form id="chargeForm" action="<g:createLink action='charge'/>" method="POST">

                            <g:hiddenField id="stripeToken" name="stripeToken"></g:hiddenField>
                            <g:hiddenField id="stripeAmount" name="amount"></g:hiddenField>
                            <g:hiddenField id="number" name="number"></g:hiddenField>
                            <g:hiddenField id="exp_month" name="exp_month"></g:hiddenField>
                            <g:hiddenField id="exp_year" name="exp_year"></g:hiddenField>

                            <select name="amountSelector" id="amountSelector">
                                <option value="10" selected>1 month - $10</option>
                                <option value="20">2 months - $20</option>
                                <option value="30">3 months - $30</option>
                                <option value="40">4 months - $40</option>
                                <option value="50">5 months - $50</option>
                                <option value="60">6 months - $60</option>
                                <option value="70">7 months - $70</option>
                                <option value="80">8 months - $80</option>
                                <option value="90">9 months - $90</option>
                                <option value="100">10 months - $100</option>
                                <option value="110">11 months - $110</option>
                                <option value="120">12 months - $120</option>
                            </select>


                        <button id="subscribeButton">Subscribe Now</button>

                        <span style="margin-left: 30px">or</span>

                        <span style="margin-left: 30px">
                            <g:link controller="issue" action="index">Continue</g:link>
                        </span>

                            <script>
                                var handler = StripeCheckout.configure({
                                    key: '${grailsApplication.config.grails.plugins.stripe.publishableKey}',
                                    image: '<g:createLink uri='/images/logo.jpg'/>',
                                    token: function(token) {
                                        console.log("----------");
                                        console.log("----------");
                                        console.log(token);
                                        $('#stripeToken').val(token.id);
                                        $('#stripeAmount').val($('#amountSelector').val() * 100);
                                        $('#exp_month').val(token.card.exp_month);
                                        $('#exp_year').val(token.card.exp_year);


                                        $('#chargeForm').submit();
                                        // Use the token to create the charge with a server-side script.
                                        // You can access the token ID with `token.id`
                                    }
                                });

                                document.getElementById('subscribeButton').addEventListener('click', function(e) {
                                    // Open Checkout with further options
                                    handler.open({
                                        name: 'BitCrane Consulting, LLC',
                                        description: $('#amountSelector option:selected').text(),
                                        amount: $('#amountSelector').val() * 100
                                    });
                                    e.preventDefault();
                                });
                            </script>


                        </form>


                    </div>

                </div>
            </g:if>

            <fieldset class="buttons">
                <g:link class="edit" action="edit" resource="${realmInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
            </fieldset>
		</div>
	</body>
</html>
