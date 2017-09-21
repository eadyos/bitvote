<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
    <style type='text/css' media='screen'>
    </style>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id='login'>
    <div class='inner'>

        <g:if test="${passwordReset}">
            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>
        </g:if>
        <g:else>
            <div class='fheader'>Enter your email address</div>

            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>

            <form action='${createLink(action: "submitForgotPassword")}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
                <p>
                    <label for='username'>Email:</label>
                    <input type='text' class='text_' name='j_username' id='username'/>
                </p>

                <p>
                    <input type='submit' id="submit" value='Reset Password'/>
                </p>

            </form>
        </g:else>
    </div>
</div>
<script type='text/javascript'>
    <!--
    (function() {
        document.forms['loginForm'].elements['j_username'].focus();
    })();
    // -->
</script>
</body>
</html>
