<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Rob's Rules Voting System</title>
</head>
<body>
<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div>
    <div id="status" role="complementary">
        <h1>Front Page</h1>
        <g:link uri="/" class="frontPageLink">Back to front page</g:link>
        <h1>Already a member?</h1>
        <g:link controller="issue" action="index" class="frontPageLink">Go to to your site</g:link>
        <h1>Get Started</h1>
        <g:link controller="realm" action="create" class="frontPageLinkLast">Add your organization</g:link>
        <g:link controller="realm" action="joinRealm" class="frontPageLinkLast">Join your organization</g:link>
    </div>
    <div class="mainLogo">
        <g:img dir="images" file="logo.jpg" width="200"></g:img>
    </div>
    <div class="mainLogoText">
        <h1>About Rob's Rules</h1>
        <p>
            As "Rob" is an easier way of saying "Robert", this system is designed to be an easier,
            more convenient way to implement Robert's Rules of Order in your organization.
            In these busy times, having frequent live meetings can be impractical.  This system can
            help make managing your organization easier and less stressful.
        </p>
    </div>
    <div style="padding: 0px 20px; clear: both;">
        <hr/>
    </div>
</div>
<div style="margin: 30px 60px 30px 60px;">
    <p>
        Rob's Rules takes your privacy and security seriously.
    </p>
    <p>
        Your organization's issues, discussions, votes, and member data are all secure.
    </p>
    %{--<p>--}%
        %{--Subscription payments are 100% PCI compliant and secured using the Stripe payment system.--}%
    %{--</p>--}%
    %{--<p>--}%
        %{--Support for your organization is fast and responsive.  If there is a problem that you need--}%
        %{--resolving, we will take care of it the same day.--}%
    %{--</p>--}%
    <p>
        If there is a feature you'd like to see implemented for your organization.  Let us know!
        We enjoy producing quality services and can accommodate most requests.
    </p>
</div>
</body>
</html>
