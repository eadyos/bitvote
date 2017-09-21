package org.eady

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

class LoginController extends grails.plugin.springsecurity.LoginController {

    def auth() {

        def config = SpringSecurityUtils.securityConfig

        def requestURL = session[SpringSecurityUtils.SAVED_REQUEST]

        if (springSecurityService.isLoggedIn()) {
            redirect uri: config.successHandler.defaultTargetUrl
            return
        }

        String view = 'auth'
        boolean selfSignup = false
        //Override
        String createPostUrl
        if(requestURL?.toString()?.contains("realm/create")){
            view = "authCreate"
            createPostUrl = createLink(controller: 'user', action: 'save')
        }
        if(requestURL?.toString()?.contains("realm/joinRealm")){
            view = "authCreate"
            createPostUrl = createLink(controller: 'user', action: 'saveJoin')
        }
        String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
        render view: view, model: [createPostUrl: createPostUrl, postUrl: postUrl,
                                   rememberMeParameter: config.rememberMe.parameter]
    }

}
