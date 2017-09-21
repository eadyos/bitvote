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
        <g:if test='${flash.message}'>
            <div class='login_message'>${flash.message}</div>
        </g:if>
        <div class='fheader'>Please update your password..</div>
        <g:form action='updatePassword' id='passwordResetForm' class='cssform' autocomplete='off'>
            <p>
                <label for='j_username' class="wide">Username</label>
                <g:textField name="j_username"></g:textField>
            </p>
            <p>
                <label for='password' class="wide">Current Password</label>
                <g:passwordField name='password' class='text_' />
            </p>
            <p>
                <label for='password' class="wide">New Password</label>
                <g:passwordField name='password_new' class='text_' />
            </p>
            <p>
                <label for='password' class="wide">New Password (again)</label>
                <g:passwordField name='password_new_2' class='text_' />
            </p>
            <p>
                <input type='submit' value='Reset' />
            </p>
        </g:form>
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
