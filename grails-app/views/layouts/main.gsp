<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">
		<g:layoutHead/>
        <g:javascript library="jquery" plugin="jquery"/>
        <g:javascript library="application"/>
		<r:layoutResources />
	</head>
	<body>
		<div id="grailsLogo" role="banner">
            <a class="siteLogo" href="${createLink(uri: '/')}">Rob's Rules <span style="font-size: x-small">Voting System</span></a>
            <div class="realm-name">
                <sec:ifLoggedIn>
                    ${currentUser?.selectedRealm?.name}
                    <g:if test="${currentUser?.availableRealms?.size() > 1}">
                        - <g:link controller="user" action="changeRealm">change</g:link>
                    </g:if>
                </sec:ifLoggedIn>
            </div>
            <div class="logout">
                <sec:ifLoggedIn>
                    <g:if test="${currentUser}">
                        Logged In: <span>${currentUser}</span>
                    </g:if>
                    <a href="${createLink(controller: 'logout')}"> Logout</a>
                </sec:ifLoggedIn>
                <sec:ifNotLoggedIn>
                    <g:link controller="issue" action="login">Login</g:link>
                </sec:ifNotLoggedIn>
            </div>
            <div style="clear: both;"></div>
        </div>
		<g:layoutBody/>
		<div class="footer" role="contentinfo">
            &copy; ${new java.util.Date().format("yyyy")} BitCrane Consulting LLC <span>For help or questions contact: support@robsrules.com</span>
		</div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		<r:layoutResources />
        <script>
            (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

            ga('create', 'UA-52645037-1', 'auto');
            ga('send', 'pageview');

        </script>
	</body>
</html>
