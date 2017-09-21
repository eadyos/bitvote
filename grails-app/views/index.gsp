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
                <h1>Already a member?</h1>
                <g:link controller="issue" action="index" class="frontPageLink">Go to your site</g:link>
                <h1>Get Started</h1>
                <g:link controller="realm" action="create" class="frontPageLinkLast">Add your organization</g:link>
                <g:link controller="realm" action="joinRealm" class="frontPageLink">Join your organization</g:link>
                <h1>More Information</h1>
                <g:link uri="/about" class="frontPageLinkLast">About Rob's Rules</g:link>
            </div>
            <div class="mainLogo">
                <g:img dir="images" file="logo.jpg" width="200"></g:img>
            </div>
            <div class="mainLogoText">
                <h1>Welcome to Rob's Rules</h1>
                <p>
                    Use Rob's Rules to track your proposals and agenda items online and ease the burden of
                    frequent live meetings.
                </p>
                <p>
                    This is the best <strong>FREE</strong> online voting system to help your home owner's association, club, or other governing body
                    easily manage the proposal, discussion, and voting of issues for your organization.
                </p>
            </div>
            <div style="padding: 0px 20px; clear: both;">
                <hr/>
            </div>
        </div>
        <div class="controller-list" role="navigation">
            <ul>
                <li>Propose issues</li>
                <li>Discuss issues with comment forum.</li>
                <li>Vote to pass, reject, or abstain on open issues</li>
                <li>Integrated notification system keeps members up to date when issues are created or pending votes.</li>
                <li>Helps reduce the need for detailed minutes by Documents organization's issues and voting record in one place.</li>
            </ul>
        </div>
        <div class="controller-list" role="navigation">
            <ul>
                <li>Easily manage members.</li>
                <li>Accountability.  See who voted for what in one easy place.</li>
                <li>Supports different voting weights if some members have greater representation.</li>
                <li>Organizations can vote using a simple majority system or quorum based voting system.</li>
                <li>Allows non-voting observers to see issues and participate in discussion.</li>
            </ul>
        </div>
        <div class="screenshot" style="clear: both; text-align: center;">
            <g:img dir="images" file="sshot3.jpg"/>
        </div>
	</body>
</html>
