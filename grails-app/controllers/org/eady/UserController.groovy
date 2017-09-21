package org.eady

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(['ROLE_USER'])
class UserController {

    def springSecurityService
    def securityService
    def notificationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def activeOnly = true
        if(params.inactive && params.inactive == 'yes'){
            activeOnly = false
        }
        User user = springSecurityService.currentUser
        def check = securityService.verifyRealmAccess(user.selectedRealm)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        def userRealms = UserRealm.findAllByRealmAndActive(user.selectedRealm, activeOnly, params)
        def count = UserRealm.countByRealmAndActive(user.selectedRealm, activeOnly)
        respond userRealms.collect {it.user}, model:[userInstanceCount: count, activeOnly: activeOnly]
    }

    def show(User userInstance) {
        def check = securityService.verifyRealmAccess(userInstance)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        respond userInstance
    }

    def create() {

        if(!springSecurityService.currentUser.selectedRealm.active){
            redirect controller: 'issue', action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }


        respond new User(params)
    }

    //Used to save new members starting their own organizations
    @Transactional
    @Secured(['permitAll'])
    def save(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            render view: "authCreate", model: [userInstance: userInstance]
            return
        }

        userInstance.confirmCode = securityService.generatePassword()

        userInstance.save flush:true

        notificationService.confirmEmailAddressForCreateRealm(userInstance)

        flash.message = "Thank you for creating an account.  An email has been sent to confirm your email address. " +
                "Please check your email and click the included link."

        redirect action: 'statusMessage'

    }

    @Transactional
    @Secured(['permitAll'])
    def confirmEmailRealm(User userInstance){
        if (userInstance == null) {
            notFound()
            return
        }

        if(userInstance.confirmCode != params.code){
            notFound()
            return
        }

        userInstance.confirmCode = null
        userInstance.save(flush:true)
        UserRole.create userInstance, Role.findByAuthority("ROLE_USER"), true

        springSecurityService.reauthenticate(userInstance.username)

        flash.message = "Thank you for confirming your email address."

        redirect controller: "realm", action: "create", base: grailsApplication.config.baseRedirectUrl
    }

    @Transactional
    @Secured(['permitAll'])
    def confirmEmailJoin(User userInstance){
        if (userInstance == null) {
            notFound()
            return
        }

        if(userInstance.confirmCode != params.code){
            notFound()
            return
        }

        userInstance.confirmCode = null
        userInstance.save(flush:true)
        UserRole.create userInstance, Role.findByAuthority("ROLE_USER"), true

        springSecurityService.reauthenticate(userInstance.username)

        flash.message = "Thank you for confirming your email address."

        redirect controller: "realm", action: "joinRealm", base: grailsApplication.config.baseRedirectUrl
    }

    //Used to save new members joining their organizations
    @Transactional
    @Secured(['permitAll'])
    def saveJoin(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            render view: "authCreate", model: [userInstance: userInstance]
            return
        }

        userInstance.confirmCode = securityService.generatePassword()

        userInstance.save flush:true

        notificationService.confirmEmailAddressForJoinRealm(userInstance)

        flash.message = "Thank you for creating an account.  An email has been sent to confirm your email address. " +
                "Please check your email and click the included link."

        redirect action: 'statusMessage'

    }

    //Used to add users to your realm
    @Transactional
    def saveMember(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if(!springSecurityService.currentUser.selectedRealm.active){
            userInstance.discard()
            redirect controller: 'issue', action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        def admin = params.administrator && params.administrator == 'on'
        def chairman = params.chairman && params.chairman == 'on'

        //TODO: only 1 chairman check

        //If the user already exists, then add them to the realm
        def existingUser = User.findByUsername(userInstance.username)
        if(existingUser){

            def userRealm = UserRealm.create(
                    existingUser, //user:
                    (Realm)springSecurityService.currentUser.selectedRealm, //realm:
                    admin, //isAdmin:
                    chairman, //isChairman
                    true, //isActive:
                    userInstance.userRealm.title, //title:
                    userInstance.userRealm.numberOfVotes, //numberOfVotes:
                    true, //issueNotificationEnabled:
                    true, //commentNotificationEnabled:
                    true, //completedNotificationEnabled:
                    true, //verified
                    true //flush:
            )

            existingUser.selectedRealm = userRealm.realm
            existingUser.save(flush:true)

            notificationService.notifyExistingNewMember(userInstance,
                    (Realm)springSecurityService.currentUser.selectedRealm)
            userInstance = existingUser

        }else{ //New user

            //Create a password
            userInstance.clearErrors()
            String password = securityService.generatePassword()
            userInstance.password = password
            userInstance.validate()

            if (userInstance.hasErrors()) {
                respond userInstance.errors, view:'create'
                return
            }

            userInstance.selectedRealm = springSecurityService.currentUser.selectedRealm

            userInstance.save flush:true

            UserRealm.create(
                    userInstance, //user:
                    userInstance.selectedRealm, //realm:
                    admin, //isAdmin:
                    chairman, //isChairman
                    true, //isActive:
                    userInstance.userRealm.title, //title:
                    userInstance.userRealm.numberOfVotes, //numberOfVotes:
                    true, //issueNotificationEnabled:
                    true, //commentNotificationEnabled:
                    true, //completedNotificationEnabled:
                    true, //verified
                    true //flush:
            )

            UserRole.create userInstance, Role.findByAuthority("ROLE_USER"), true
            notificationService.notifyNewMember(userInstance, password)
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'userInstance.label', default: 'Member'), userInstance])
        redirect action: 'show', base: grailsApplication.config.baseRedirectUrl, params: [id: userInstance.id]
    }

    def edit(User userInstance) {
        respond userInstance
    }

    @Transactional
    def update(User userInstance) {

        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view:'edit'
            return
        }

        //Only admins can update people besides themselves
        if(springSecurityService.currentUser.id != userInstance.id){
            if(!springSecurityService.currentUser.admin){
                userInstance.discard()
                flash.message = "Only administrators can update user details."
                respond userInstance, view:'show'
                return
            }
        }

        //Only administrators can make people administrators
        //Or change the number of votes
        def currentUserRealm = UserRealm.findByUserAndRealm(springSecurityService.currentUser,
                springSecurityService.currentUser.selectedRealm)
        //If you don't discard this instance, then the Gorm retrieval below will pull back the in-memory
        //copy.  Weirdness... I still don't fully comprehend hibernate sessions
        userInstance.userRealm.discard()
        UserRealm existingUserRealm = UserRealm.get(userInstance.id, springSecurityService.currentUser.selectedRealm.id)
        if(!currentUserRealm.admin){ //Then set to existing values
            userInstance.userRealm.admin = existingUserRealm.admin
            userInstance.userRealm.numberOfVotes = existingUserRealm.numberOfVotes
        }


        //Check to make sure at least 1 admin
        if(!userInstance.userRealm.admin && existingUserRealm.admin){
            if(UserRealm.countByAdminAndActiveAndRealm(true, true, currentUserRealm.realm) <=1){
                userInstance.discard()
                flash.message = "An organization must have at least 1 administrator."
                respond userInstance.errors, view:'edit'
                return
            }
        }

        //Check to insure that if admin then has to be active
        if(userInstance.userRealm.admin && !userInstance.userRealm.active){
            flash.message = "Administrators can't be inactive."
            userInstance.discard()
            respond userInstance.errors, view:'edit'
            return
        }

        def check = securityService.verifyRealmAccess(userInstance)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            userInstance.discard()
            rejectAccess(check)
            return
        }

        //Check that if selecting a chairman, we deselect other chairman
        //Only 1 chairman
        if(!existingUserRealm.chairman && userInstance.userRealm.chairman){
            def oldChairmanUserRealms = UserRealm.findAllByRealmAndChairman(existingUserRealm.realm, true)

            oldChairmanUserRealms.each {UserRealm oldChairmanUserRealm ->
                def oldChairman = oldChairmanUserRealm.user
                oldChairmanUserRealm.discard()
                UserRealm.remove(oldChairman, existingUserRealm.realm)

                UserRealm.create(
                        oldChairman,
                        (Realm)springSecurityService.currentUser.selectedRealm, //realm:
                        oldChairmanUserRealm.admin, //isAdmin:
                        false, //isChairman
                        oldChairmanUserRealm.active, //isActive:
                        oldChairmanUserRealm.title, //title:
                        oldChairmanUserRealm.numberOfVotes, //numberOfVotes:
                        oldChairmanUserRealm.issueNotificationEnabled, //issueNotificationEnabled:
                        oldChairmanUserRealm.commentNotificationEnabled, //commentNotificationEnabled:
                        oldChairmanUserRealm.completedNotificationEnabled, //commentNotificationEnabled:
                        oldChairmanUserRealm.verified, //verified
                        true //flush:
                )
            }
        }


        //Some bullcrap here.  need a better solution than to re-create
        existingUserRealm.discard()
        UserRealm.remove(existingUserRealm.user, existingUserRealm.realm, true)

        UserRealm.create(
                userInstance, //user:
                (Realm)springSecurityService.currentUser.selectedRealm, //realm:
                userInstance.userRealm.admin, //isAdmin:
                userInstance.userRealm.chairman, //isChairman
                userInstance.userRealm.active, //isActive:
                userInstance.userRealm.title, //title:
                userInstance.userRealm.numberOfVotes, //numberOfVotes:
                userInstance.userRealm.issueNotificationEnabled, //issueNotificationEnabled:
                userInstance.userRealm.commentNotificationEnabled, //commentNotificationEnabled:
                userInstance.userRealm.completedNotificationEnabled, //commentNotificationEnabled:
                userInstance.userRealm.verified, //verified
                true //flush:
        )

        userInstance.save flush:true
        flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'Member'), userInstance])
        redirect action: 'show', base: grailsApplication.config.baseRedirectUrl, params: [id: userInstance.id]
    }


    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userInstance.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def changeRealm() {
        def activeOnly = true
        if(params.inactive && params.inactive == 'yes'){
            activeOnly = false
        }

        respond springSecurityService.currentUser, model:[currentUser: springSecurityService.currentUser,
                                                          activeOnly: activeOnly]
    }

    @Transactional
    def changeRealmSave(Realm realm){
        if (realm == null) {
            notFound()
            return
        }

        def check = securityService.verifyRealmAccess(realm)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            realm.discard()
            rejectAccess(check)
            return
        }

        User user = springSecurityService.currentUser
        user.selectedRealm = realm
        user.save flush:true

        redirect(controller: 'issue', action: 'index', base: grailsApplication.config.baseRedirectUrl)

    }

    @Secured(['permitAll'])
    def forgotPassword(){
    }

    @Secured(['permitAll'])
    @Transactional
    def submitForgotPassword(){
        def username = params.j_username

        def user = User.findByUsername(username)

        if(!user){
            flash.message = "No such user detected"
            render view: 'forgotPassword'
            return
        }

        def pass = securityService.generatePassword()
        println pass
        user.password = pass
        user.passwordExpired = true
        user.save(flush:true)
        flash.message = "Password Reset.  Check your email."

        notificationService.notifyPasswordChange(user, pass)

        render view: 'forgotPassword', model: [passwordReset: true]
    }

    @Secured(['permitAll'])
    def passwordExpired() {

        def s = session
        [username: session['SPRING_SECURITY_LAST_USERNAME']]
    }

    @Secured(['permitAll'])
    @Transactional
    def updatePassword() {

        String username = params.j_username
        if (!username) {
            flash.message = 'Sorry, an error has occurred'
            redirect controller: 'login', action: 'auth'
            return
        }
        String password = params.password
        String newPassword = params.password_new
        String newPassword2 = params.password_new_2
        if (!password || !newPassword || !newPassword2 || newPassword != newPassword2) {
            flash.message = 'Please enter your current password and a valid new password'
            render view: 'passwordExpired', model: [username: params.j_username]
            return
        }

        def passwordEncoder = springSecurityService.passwordEncoder

        User user = User.findByUsername(username)
        if (!passwordEncoder.isPasswordValid(user.password, password, null /*salt*/)) {
            flash.message = 'Current password is incorrect'
            render view: 'passwordExpired', model: [username: params.j_username]
            return
        }

        if (passwordEncoder.isPasswordValid(user.password, newPassword, null /*salt*/)) {
            flash.message = 'Please choose a different password from your current one'
            render view: 'passwordExpired', model: [username: params.j_username]
            return
        }

        user.password = newPassword
        user.passwordExpired = false
        user.save(flush:true) // if you have password constraints check them here

        springSecurityService.reauthenticate(user.username)

        redirect controller: 'issue', action: 'index'
    }

    @Transactional
    def verifyUser(User userInstance){
        if (userInstance == null) {
            notFound()
            return
        }
        def check = securityService.verifyRealmAccess(springSecurityService.currentUser.selectedRealm)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            userInstance.discard()
            rejectAccess(check)
            return
        }

        def realm = springSecurityService.currentUser.selectedRealm

        def existingUserRealm = UserRealm.get(userInstance.id, realm.id)
        //Some bullcrap here.  need a better solution than to re-create
        existingUserRealm.discard()
        UserRealm.remove(existingUserRealm.user, existingUserRealm.realm, true)

        UserRealm.create(
                userInstance, //user:
                (Realm)springSecurityService.currentUser.selectedRealm, //realm:
                userInstance.userRealm.admin, //isAdmin:
                userInstance.userRealm.chairman, //isChairman:
                userInstance.userRealm.active, //isActive:
                userInstance.userRealm.title, //title:
                userInstance.userRealm.numberOfVotes, //numberOfVotes:
                userInstance.userRealm.issueNotificationEnabled, //issueNotificationEnabled:
                userInstance.userRealm.commentNotificationEnabled, //commentNotificationEnabled:
                userInstance.userRealm.completedNotificationEnabled, //commentNotificationEnabled:
                true, //verified
                true //flush:
        )

        userInstance.save flush:true
        flash.message = "User Verified"
        notificationService.notifyNewMemberVerified(userInstance, realm)

        redirect action: 'index', base: grailsApplication.config.baseRedirectUrl, params: [id: userInstance.id]

    }

    @Transactional
    def rejectUser(User userInstance){
        if (userInstance == null) {
            notFound()
            return
        }
        def check = securityService.verifyRealmAccess(springSecurityService.currentUser.selectedRealm)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        def realm = springSecurityService.currentUser.selectedRealm

        def existingUserRealm = UserRealm.get(userInstance.id, realm.id)
        UserRealm.remove(existingUserRealm.user, existingUserRealm.realm, true)

        def realms = userInstance.availableActiveRealms
        if(!realms || realms.size() == 0){
            realms = userInstance.availableRealms
        }
        if(realms){
            userInstance.selectedRealm = realms.getAt(0)
        }else{
            userInstance.selectedRealm = null
        }

        userInstance.save flush:true
        flash.message = "User Rejected"
        notificationService.notifyNewMemberRejected(userInstance, realm)

        redirect action: 'index', base: grailsApplication.config.baseRedirectUrl, params: [id: userInstance.id]
    }

    @Secured(['permitAll'])
    def statusMessage(){

    }

    protected rejectAccess(int check){

        String message
        if(check == SecurityService.INACTIVE){
            message = "Your member account is no longer active.  Contact your organization's administrator for help."
        }else if(check == SecurityService.EXPIRED){
            message = "Your organization's subscription has expired. Contact your organization's administrator for help."
        }else{  //INVALID
            message = "Invalid Access"
        }
        flash.message = message
        redirect(controller: 'user', action: 'statusMessage')
    }


}
