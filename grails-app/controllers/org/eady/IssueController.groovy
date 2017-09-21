package org.eady

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationErrors
import groovy.time.TimeCategory

import java.security.Security

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(['ROLE_USER'])
class IssueController {

    def springSecurityService
    def notificationService
    def securityService
    
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {

        User user = springSecurityService.currentUser

        def check = securityService.verifyRealmAccess(user.selectedRealm)
        if(check != SecurityService.VALID && check != SecurityService.EXPIRED){
            rejectAccess(check)
            return
        }
        if(check == SecurityService.EXPIRED){
            request.statusMessage = "Your organization's subscription has expired.  Please have an administrator renew it."
        }else{
            if(!user.selectedRealm.active){
                request.statusMessage = "This organization is no longer active."
            }
        }

        def activeOnly = true
        if(params.inactive && params.inactive == 'yes'){
            activeOnly = false
        }

        params.sort = "id"
        params.order = "desc"
        params.max = Math.min(max ?: 10, 100)

        //TODO: Clean up this if/then/else mess with a dynamic finder/criteria query
        if(activeOnly){
            if(params.crit){
                respond Issue.findAllByRealmAndNameIlikeAndStatusInList(user.selectedRealm, Issue.Status.getActiveStatuses(), "%$params.crit%", params),
                        model:[issueInstanceCount: Issue.countByRealmAndNameIlikeAndStatusInList(user.selectedRealm, Issue.Status.getActiveStatuses(),  "%$params.crit%"),
                            activeOnly: activeOnly]
            }else{
                respond Issue.findAllByRealmAndStatusInList(user.selectedRealm, Issue.Status.activeStatuses, params),
                        model:[issueInstanceCount: Issue.countByRealmAndStatusInList(user.selectedRealm, Issue.Status.getActiveStatuses()), activeOnly: activeOnly]
            }
        }else{ //inactive
            if(params.crit){
                respond Issue.findAllByRealmAndNameIlikeAndStatusInList(user.selectedRealm, Issue.Status.inactiveStatuses, "%$params.crit%", params),
                        model:[issueInstanceCount: Issue.countByRealmAndNameIlikeAndStatusInList(user.selectedRealm, Issue.Status.inactiveStatuses, "%$params.crit%"),
                            activeOnly: activeOnly]
            }else{
                respond Issue.findAllByRealmAndStatusInList(user.selectedRealm, Issue.Status.inactiveStatuses, params),
                        model:[issueInstanceCount: Issue.countByRealmAndStatusInList(user.selectedRealm, Issue.Status.inactiveStatuses), activeOnly: activeOnly]
            }
        }

    }

    def show(Issue issueInstance) {

        def check = securityService.verifyRealmAccess(issueInstance)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }


        respond issueInstance
    }

    def create() {

        def check = securityService.verifyRealmAccess(springSecurityService.currentUser.selectedRealm)
        if(!springSecurityService.currentUser.selectedRealm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }

        respond new Issue(params)
    }

    @Transactional
    def save(Issue issueInstance) {
        if (issueInstance == null) {
            issueInstance.discard()
            notFound()
            return
        }

        issueInstance.clearErrors()
        issueInstance.proposedBy = springSecurityService.currentUser
        issueInstance.realm = springSecurityService.currentUser.selectedRealm
        issueInstance.status = Issue.Status.Discuss
        issueInstance.validate()

        def check = securityService.verifyRealmAccess(issueInstance)
        if(check != SecurityService.VALID){
            issueInstance.discard()
            rejectAccess(check)
            return
        }

        if(!issueInstance.realm.active){
            issueInstance.discard()
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        if (issueInstance.hasErrors()) {
            respond issueInstance.errors, view:'create'
            return
        }

        issueInstance.save flush:true

        notificationService.notifyIssueCreation(issueInstance)

        flash.message = message(code: 'default.created.message', args: [message(code: 'issueInstance.label', default: 'Issue'), issueInstance.name])

        redirect(action: 'show', base: grailsApplication.config.baseRedirectUrl, params: [id: issueInstance.id])
    }

    def edit(Issue issueInstance) {

        def check = securityService.verifyRealmAccess(issueInstance)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }
        if(!issueInstance.realm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        if(issueInstance.completed){
            flash.message = "Can't edit an issue that has been voted on."
            redirect action: 'show', params: [id: issueInstance.id], base: grailsApplication.config.baseRedirectUrl
        }else{
            respond issueInstance
        }
    }

    @Transactional
    def update(Issue issueInstance) {
        if (issueInstance == null) {
            notFound()
            return
        }

        def check = securityService.verifyRealmAccess(issueInstance)
        if(check != SecurityService.VALID){
            issueInstance.discard()
            rejectAccess(check)
            return
        }
        if(!issueInstance.realm.active){
            issueInstance.discard()
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        if(!verifyOwnership(issueInstance)){
            issueInstance.discard()
            redirect(action: 'show', params: [id: issueInstance.id])
            return
        }

        if (issueInstance.hasErrors()) {
            respond issueInstance.errors, view:'edit'
            return
        }

        log.info("Issue Updated: " + issueInstance.id + ":" + issueInstance.proposedBy)
        issueInstance.save flush:true

        flash.message = message(code: 'default.updated.message', args: [message(code: 'Issue.label', default: 'Issue'), issueInstance.name])

        redirect(action: 'show', base: grailsApplication.config.baseRedirectUrl, params: [id: issueInstance.id])
    }

    @Transactional
    def delete(Issue issueInstance) {

        if (issueInstance == null) {
            notFound()
            return
        }
        def check = securityService.verifyRealmAccess(issueInstance)
        if(check != SecurityService.VALID){
            issueInstance.discard()
            rejectAccess(check)
            return
        }

        if(!verifyOwnership(issueInstance)){
            issueInstance.discard()
            redirect action: 'show', params: [id: issueInstance.id], base: grailsApplication.config.baseRedirectUrl
            return
        }

        if(issueInstance.completed){
            issueInstance.discard()
            flash.message = "Can't delete an issue that has been voted on."
            redirect action: 'show', params: [id: issueInstance.id], base: grailsApplication.config.baseRedirectUrl
            return
        }

        issueInstance.delete flush:true

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Issue.label', default: 'Issue'), issueInstance.name])
        redirect action:"index", method:"GET", base: grailsApplication.config.baseRedirectUrl
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueInstance.label', default: 'Issue'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Transactional
    def vote(){
        Issue issue = Issue.get(params.id)
        User user = springSecurityService.currentUser

        def check = securityService.verifyRealmAccess(issue)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }
        if(!issue.realm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        //Disabled in the UI, but server-side check in case of hackery
        if(user.userRealm.numberOfVotes == 0){
            if(!issue.realm.active){
                redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
                return
            }
        }
        if(!user.verified){
            if(!issue.realm.active){
                redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
                return
            }
        }
        //Make sure the issue has voting enabled
        if(!issue.status in Issue.Status.votingStatuses){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        log.info("Vote Recorded: $user - $params.vote - $issue.id : $issue.name")

        if(params.vote == 'cancel'){
            def votes = issue.votes.findAll {
                it.user.id == user.id
            }
            votes.each {
                issue.removeFromVotes(it)
                it.delete()
            }
        }else{
            def voteType = params.vote == 'yes' ? Vote.Type.YAY : params.vote == 'no' ? Vote.Type.NAY : Vote.Type.ABSTAIN
            issue.votes << new Vote(user: user, issue: issue, type: voteType, weight: user.userRealm.numberOfVotes)
        }

        if(!issue.completed){
            if(issue.isPendingApproval()){
                issue.status = Issue.Status.Approved_Pending
                log.info("Issue Pending Approved: $issue.id : $issue.name")
                flash.message = "Issue Passed - Pending"
            }else if(issue.isPendingRejection()){
                issue.status = Issue.Status.Rejected_Pending
                log.info("Issue Pending Rejected: $issue.id : $issue.name")
                flash.message = "Issue Rejected - Pending"
            }else if(issue.isInconclusive()){
                issue.status = Issue.Status.Rejected_Pending
                log.info("Issue Pending Rejected/Inconclusive: $issue.id : $issue.name")
                flash.message = "Issue Rejected/Inconclusive - Pending"
            }else{
                issue.status = Issue.Status.Vote
                flash.message = "Vote Recorded"
            }
        }

        issue.save()
        redirect(action: "index", base: grailsApplication.config.baseRedirectUrl, params: [inactive: issue.completed ? 'yes' : 'no'])
    }

    @Transactional
    def createComment(){

        def comment = new Comment()
        comment.user = springSecurityService.currentUser
        comment.text = params.text
        def issue = Issue.get(params.issueId)

        def check = securityService.verifyRealmAccess(issue)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }

        if(!issue.realm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        if(!comment.text.trim().isEmpty()){
            issue.addToComments(comment)
            comment.issue = issue

            comment.save()

            notificationService.notifyCommentCreation(issue, comment)
        }


        redirect(action: "index", base: grailsApplication.config.baseRedirectUrl, params: [inactive: params.inactive])
    }

    def login(){
        redirect(action: "index", params: params, base: grailsApplication.config.baseRedirectUrl)
    }

    def search() {
        redirect(action: "index", params: params, base: grailsApplication.config.baseRedirectUrl)
    }

    @Transactional
    def enableVoting(){
        Issue issue = Issue.get(params.id)
        User user = springSecurityService.currentUser

        def check = securityService.verifyRealmAccess(issue)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }
        if(!issue.realm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(!user.verified){
            if(!issue.realm.active){
                redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
                return
            }
        }
        if(!user.isChairman()){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(issue.status != Issue.Status.Discuss){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        log.info("Voting Enabled: $user - $issue.id : $issue.name")

        issue.status = Issue.Status.Vote
        issue.statusDate = new Date()
        issue.save()

        notificationService.notifyVotingEnabled(issue)

        flash.message = "Voting Enabled for issue: $issue.name"

        redirect(action: "index", base: grailsApplication.config.baseRedirectUrl, params: [inactive: issue.completed ? 'yes' : 'no'])
    }

    @Transactional
    def tableIssue(){
        Issue issue = Issue.get(params.id)
        User user = springSecurityService.currentUser

        def check = securityService.verifyRealmAccess(issue)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }
        if(!issue.realm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(!user.verified){
            if(!issue.realm.active){
                redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
                return
            }
        }
        if(!user.isChairman()){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(issue.status != Issue.Status.Discuss){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        log.info("Issue Tabled: $user - $issue.id : $issue.name")

        issue.status = Issue.Status.Tabled
        issue.statusDate = new Date()
        issue.save()

        notificationService.notifyIssueTabled(issue)

        flash.message = "Issue Tabled: $issue.name"

        redirect(action: "index", base: grailsApplication.config.baseRedirectUrl, params: [inactive: issue.completed ? 'yes' : 'no'])
    }

    @Transactional
    def untableIssue(){
        Issue issue = Issue.get(params.id)
        User user = springSecurityService.currentUser

        def check = securityService.verifyRealmAccess(issue)
        if(check != SecurityService.VALID){
            rejectAccess(check)
            return
        }
        if(!issue.realm.active){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(!user.verified){
            if(!issue.realm.active){
                redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
                return
            }
        }
        if(!user.isChairman()){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }
        if(issue.status != Issue.Status.Tabled){
            redirect action: 'index', base: grailsApplication.config.baseRedirectUrl
            return
        }

        log.info("Issue Re-Opened: $user - $issue.id : $issue.name")

        issue.status = Issue.Status.Discuss
        issue.statusDate = new Date()
        issue.save()

        notificationService.notifyIssueUntabled(issue)

        flash.message = "Issue Re-Opened: $issue.name"

        redirect(action: "index", base: grailsApplication.config.baseRedirectUrl, params: [inactive: issue.completed ? 'yes' : 'no'])
    }



    protected boolean verifyOwnership(Issue issue){
        if(issue.proposedBy != springSecurityService.currentUser){
            issue.errors.putAt("proposedBy", "Can't update an issue created by somebody else.")
            return false
        }else{
            return true
        }
    }

    protected rejectAccess(int check){

        String message
        if(check == SecurityService.INACTIVE){
            message = "Your member account is no longer active.  Contact your organization's administrator for help."
        }else if(check == SecurityService.UNVERIFIED){
            message = "Thanks for signing up!  You will have full access once an administrator verifies your information."
            flash.unverified = true
        }else if(check == SecurityService.EXPIRED){
            message = "Your organization's subscription has expired. Contact your organization's administrator for help."
        }else{  //INVALID
            message = "Invalid Access"
        }
        flash.message = message
        redirect(controller: 'user', action: 'statusMessage', base: grailsApplication.config.baseRedirectUrl)
    }

    @Transactional
    @Secured(['permitAll'])
    def closePendingIssues() {

        def issues = Issue.findAllByStatusInList([Issue.Status.Approved_Pending, Issue.Status.Rejected_Pending])
        def now = new Date()
        use(TimeCategory) {
            issues.each {Issue issue ->
//                if(issue.closeDate < now){
                if(issue.closeDate < now - 48.hours){
                    if(issue.status == Issue.Status.Approved_Pending){
                        issue.status = Issue.Status.Approved
                        notificationService.notifyVotePassed(issue)
                    }else{
                        issue.status = Issue.Status.Rejected
                        notificationService.notifyVoteFailed(issue)
                    }
                    issue.statusDate = new Date()
                    issue.save()
                }else{
                    if(issue.status == Issue.Status.Approved_Pending){
                        if(!issue.notifiedStatus || issue.notifiedStatus != Issue.Status.Approved_Pending){
                            notificationService.notifyVotePassedPending(issue)
                            issue.notifiedStatus = Issue.Status.Approved_Pending
                            issue.save()
                        }
                    }else{
                        if(!issue.notifiedStatus || issue.notifiedStatus != Issue.Status.Rejected_Pending){
                            notificationService.notifyVoteFailedPending(issue)
                            issue.notifiedStatus = Issue.Status.Rejected_Pending
                            issue.save()
                        }
                    }
                }
            }
        }
        render "Success"
    }



}
