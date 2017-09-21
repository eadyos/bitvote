package org.eady

import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Charge
import grails.plugin.springsecurity.annotation.Secured
import groovy.time.TimeCategory

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(['ROLE_USER'])
class RealmController {

    def springSecurityService
    def grailsApplication
    def securityService
    def notificationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def show(Realm realmInstance) {

        def check = securityService.verifyRealmAccess(realmInstance)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        respond realmInstance
    }

    def create() {
        respond new Realm(params)
    }

    @Transactional
    def save(Realm realmInstance) {
        if (realmInstance == null) {
            notFound()
            return
        }

        if (realmInstance.hasErrors()) {
            respond realmInstance.errors, view:'create'
            return
        }

        //This block shouldn't occur.  Only new realms would call save()
        //But I suppose it can't hurt to leave it in, just in case hackery
        if(realmInstance.id){
            def check = securityService.verifyRealmAccess(realmInstance)
            if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
                rejectAccess(check)
                return
            }
        }

        if(params.voteType == 'quorum'){
            realmInstance.quorum = true
            if(realmInstance.quorumPercentage){
                realmInstance.quorumCount = null
                //Should be null by default
                //But don't trust client
            }
        }

        realmInstance.save flush:true

        springSecurityService.currentUser.selectedRealm = realmInstance

        UserRealm.create(
                springSecurityService.currentUser, //user:
                realmInstance, //realm:
                true, //isAdmin:
                true, //isChairman
                true, //isActive:
                null, //title:
                1, //numberOfVotes:
                true, //issueNotificationEnabled:
                true, //commentNotificationEnabled:
                true, //completedNotificationEnabled:
                true, //verified
                true //flush:
        )

        flash.message = message(code: 'default.created.message', args: [message(code: 'realmInstance.label', default: 'Organization'), realmInstance.name])
        redirect(action: "show", base: grailsApplication.config.baseRedirectUrl, params: [id: realmInstance.id])
        return
    }

    def edit(Realm realmInstance) {

        def check = securityService.verifyRealmAccess(realmInstance)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        respond realmInstance
    }

    @Transactional
    def update(Realm realmInstance) {
        if (realmInstance == null) {
            notFound()
            return
        }

        if (realmInstance.hasErrors()) {
            respond realmInstance.errors, view:'edit'
            return
        }

        def check = securityService.verifyRealmAccess(realmInstance)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        if(params.voteType == 'quorum'){
            realmInstance.quorum = true
            if(realmInstance.quorumPercentage){
                realmInstance.quorumCount = null
                //Should be null by default
                //But don't trust client
            }
        }else{
            realmInstance.quorum = false
        }

        realmInstance.save flush:true

        flash.message = message(code: 'default.updated.message', args: [message(code: 'Realm.label', default: 'Organization'), realmInstance.name])
        redirect(action: "show", base: grailsApplication.config.baseRedirectUrl, params: [id: realmInstance.id])
        return

    }

    protected void notFound() {


        flash.message = message(code: 'default.not.found.message', args: [message(code: 'realmInstance.label', default: 'Organization'), params.id])
        redirect(controller: 'issue', action: "index", base: grailsApplication.config.baseRedirectUrl)
        return
    }

    @Transactional
    def charge(String stripeToken, Integer amount) {

        Realm selectedRealm = springSecurityService.currentUser.selectedRealm


        Stripe.apiKey = grailsApplication.config.grails.plugins.stripe.secretKey;
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", amount);
        chargeMap.put("currency", "usd");
        chargeMap.put("card", stripeToken);
        try {
            Charge charge = Charge.create(chargeMap);
            StripeCharge sc = new StripeCharge(amount: amount, user: springSecurityService.currentUser, stripeJson: charge.toString())
            int months = (int)(amount / 1000)
            use(TimeCategory) {
                if(selectedRealm.expiryDate < new Date()){
                    selectedRealm.expiryDate = new Date() + months.months
                }else{
                    selectedRealm.expiryDate += months.months
                }
            }
            sc.save(flush: true)
            selectedRealm.addToCharges(sc)
            selectedRealm.save(flush:true)
        } catch (StripeException e) {
            e.printStackTrace();
        }

        flash.message = "Payment Successful"

        redirect(action: "show", params: [id: selectedRealm.id], basee: grailsApplication.config.baseRedirectUrl)
    }

    def selfSignup(){
        if(!springSecurityService.currentUser.admin){
            rejectAccess(SecurityService.INVALID)
            return
        }

        render view: 'selfSignup', model: [realmInstance: springSecurityService.currentUser.selectedRealm]
    }

    @Transactional
    def selfSignupSubmit(Realm realmInstance){
        if(!springSecurityService.currentUser.admin){
            rejectAccess(SecurityService.INVALID)
            return
        }
        def check = securityService.verifyRealmAccess(realmInstance)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }

        if (realmInstance.hasErrors()) {
            realmInstance.joinPassword = null
            render view:'selfSignup', model: [realmInstance: realmInstance]
            return
        }

        realmInstance.save()

        render view: 'selfSignup', model: [realmInstance: realmInstance]
    }

    def joinRealm(){

        render view: 'joinRealm'
    }

    @Transactional
    def joinRealmSubmit(Realm realmInstance){

        def p = params

        if(!realmInstance){
            flash.message = "Invalid Organization ID or Join Password"
            render view: 'joinRealm'
            return
        }


        if(realmInstance.joinPassword && realmInstance.joinPassword == params.password){

            springSecurityService.currentUser.selectedRealm = realmInstance
            springSecurityService.currentUser.save()

            def userRealm = UserRealm.create(
                    springSecurityService.currentUser, //user:
                    (Realm)springSecurityService.currentUser.selectedRealm, //realm:
                    false, //isAdmin:
                    false, //isChairman
                    true, //isActive:
                    params.title, //title:
                    1, //numberOfVotes:
                    true, //issueNotificationEnabled:
                    true, //commentNotificationEnabled:
                    true, //completedNotificationEnabled:
                    false, //verified
                    true //flush:
            )
        }else{  //invalid password
            flash.message = "Invalid Organization ID or Join Password"
            render view: 'joinRealm'
            return
        }

        notificationService.notifyAdminsUserSignup(springSecurityService.currentUser,
                springSecurityService.currentUser.selectedRealm)

        flash.message = "You have successfully joined your organization.  The administrator will need " +
                "to verify your account before you have full access."
        redirect(controller: 'issue', action: 'index')
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
        redirect(controller: 'user', action: 'statusMessage', base: grailsApplication.config.baseRedirectUrl)
    }


}
