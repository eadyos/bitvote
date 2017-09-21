package org.eady

import grails.transaction.Transactional

@Transactional
class NotificationService {

    def mailService
    def grailsApplication
    def springSecurityService
    def grailsLinkGenerator

    static final String fromName = "Rob's Rules Notification <notify@robsrules.com>"


    protected sendToUsers(List userEmails, String subjectParam, String bodyParam, String excludeAddress = null ) {

        if(excludeAddress){
            userEmails.remove(excludeAddress)
        }

        if(grailsApplication.config.enableMail && !userEmails.isEmpty()) {

            mailService.sendMail {

                from fromName
                to userEmails
                subject subjectParam
                html bodyParam
            }
        }
    }

    protected sendToCommentUsers(Issue issue, String subjectParam, String bodyParam, String excludeAddress = null){

        def userRealms = UserRealm.findAllByRealmAndActiveAndCommentNotificationEnabled(
                issue.realm, true, true)
        def addresses = userRealms.collect {it.user.username}
        sendToUsers(addresses, subjectParam, bodyParam, excludeAddress)
    }

    protected sendToIssueUsers(Issue issue, String subjectParam, String bodyParam, String excludeAddress = null){

        def userRealms = UserRealm.findAllByRealmAndActiveAndIssueNotificationEnabled(
                issue.realm, true, true)
        def addresses = userRealms.collect {it.user.username}
        sendToUsers(addresses, subjectParam, bodyParam, excludeAddress)
    }

    protected sendToCompletedIssueUsers(Issue issue, String subjectParam, String bodyParam, String excludeAddress = null){

        def userRealms1 = UserRealm.findAllByRealmAndActiveAndIssueNotificationEnabled(
                issue.realm, true, true)
        def userRealms2 = UserRealm.findAllByRealmAndActiveAndCompletedNotificationEnabled(
                issue.realm, true, true)
        def addresses = (userRealms1 + userRealms2).unique().collect{it.user.username}
        sendToUsers(addresses, subjectParam, bodyParam, excludeAddress)
    }

    protected sendToUser(User user, String subjectParam, String bodyParam) {

        if(grailsApplication.config.enableMail) {

            mailService.sendMail {
                from fromName
                to user.username
                subject subjectParam
                html bodyParam
            }
        }
    }

    protected List<String> allUserEmails(){

        def userRealms = UserRealm.findAllByRealmAndActive(springSecurityService.currentUser.selectedRealm, true)
        return userRealms.collect {it.user.username}
    }

    def notifyIssueCreation(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index')

        sendToIssueUsers(issue,
                "Issue Created: $issue.name",
                """A Rob's Rules issue was created for $issue.realm.name regarding $issue.name
                        <br/><br/>
                        Details: $issue.description
                    <br/>
                    <br/>
                You can discuss the issue by <a href="$link">clicking here</a>.
            """, issue.proposedBy.username)
    }

    def notifyIssueTabled(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index', params: [inactive: 'yes'])

        sendToIssueUsers(issue,
                "Issue Tabled: $issue.name",
                """A Rob's Rules issue was tabled for $issue.realm.name regarding $issue.name
                        <br/><br/>
                        Details: $issue.description
                    <br/>
                You can view the issue by <a href="$link">clicking here</a>.
            """, issue.proposedBy.username)
    }

    def notifyIssueUntabled(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index')

        sendToIssueUsers(issue,
                "Issue Re-Opened: $issue.name",
                """A Rob's Rules issue was re-opened for $issue.realm.name regarding $issue.name
                        <br/><br/>
                        Details: $issue.description
                    <br/>
                    <br/>
                You can discuss the issue by <a href="$link">clicking here</a>.
            """, issue.proposedBy.username)
    }

    def notifyVotingEnabled(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index')

        sendToIssueUsers(issue,
                "Voting Enabled: $issue.name",
                """A Rob's Rules issue was created for $issue.realm.name regarding $issue.name
                    is now open for voting.
                        <br/><br/>
                        Details: $issue.description
                    <br/>
                    <br/>
                You can vote on the issue by <a href="$link">clicking here</a>.
            """, issue.proposedBy.username)
    }

    def notifyVotePassed(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index', params: [inactive: 'yes'])

        sendToCompletedIssueUsers(issue,
                "Issue Approved: $issue.name",
                """A Rob's Rules issue was Approved for $issue.realm.name regarding $issue.name
                <br/>
                Description:
                <br/>
                $issue.description
                <br/><br/>
            You can view the issue by <a href="$link">clicking here</a>.
        """)
    }

    def notifyVotePassedPending(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index')

        sendToIssueUsers(issue,
                "Pending Issue Approved: $issue.name",
                """A Rob's Rules issue is pending approval for $issue.realm.name regarding $issue.name
                <br/>
                The issue will remain open for 2 days to allow members to add or change their vote.
                <br/>
                Description:
                <br/>
                $issue.description
                <br/><br/>
            You can view the issue by <a href="$link">clicking here</a>.
        """)
    }

    def notifyVoteFailedPending(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index')

        sendToIssueUsers(issue,
                "Pending Issue Approved: $issue.name",
                """A Rob's Rules issue is pending rejection for $issue.realm.name regarding $issue.name
                <br/>
                The issue will remain open for 2 days to allow members to add or change their vote.
                <br/>
                Description:
                <br/>
                $issue.description
                <br/><br/>
            You can view the issue by <a href="$link">clicking here</a>.
        """)
    }

    def notifyVoteFailed(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index', params: [inactive: 'yes'])

        sendToIssueUsers(issue,
                "Issue Rejected: $issue.name",
                """A Rob's Rules issue was Rejected for $issue.realm.name regarding $issue.name
                <br/>
            You can view the issue by <a href="$link">clicking here</a>.
        """)
    }

    def notifyVoteDeadlock(Issue issue){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index', params: [inactive: 'yes'])

        sendToIssueUsers(issue,
            "Issue Deadlocked: $issue.name",
            """A Rob's Rules issue was Rejected due to deadlock for $issue.realm.name regarding $issue.name
            <br/>
            You can view the issue by <a href="$link">clicking here</a>.
            """)
    }

    def notifyCommentCreation(Issue issue, Comment comment){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'issue', action: 'index')

        sendToCommentUsers(issue,
                "Comment: $issue.name From: $comment.user",
                """A Rob's Rules comment was made on $issue.realm.name issue $issue.name
                from $comment.user
                <br/><br/>
                $comment.text
                <br/><br/>
            You can view and respond by <a href="$link">clicking here</a>.
        """, comment.user.username)
    }

    def notifyNewMember(User user, String password){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(uri: "/")

        boolean isObserver = user.getUserRealm().numberOfVotes == 0

        String observerAccount = """<br/><br/>Your account has been setup as an observer account.
            This means that you can see the organization's issues and comment on them, but you
            will not be able to cast a vote."""

        sendToUser(user,
                "Your Rob's Rules account for $user.selectedRealm.name",
                """A Rob's Rules account was created for you for organization $user.selectedRealm.name
                <br/><br/>
                Your username is your email address: $user.username
                <br/>
                Your temporary password is: $password
                ${isObserver ? observerAccount : ''}
                <br/><br/>
                You can login to your organization by <a href="$link">clicking here</a>.
        """)
    }

    def notifyNewMemberVerified(User user, Realm realm){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(uri: "/")

        sendToUser(user,
                "Rob's Rules account for $realm.name verified",
                """Your Rob's Rules account was verified for organization $realm.name
                <br/><br/>
                You now have full access to your organization's system.
                <br/><br/>
                You can login to your organization by <a href="$link">clicking here</a>.
        """)
    }

    def notifyNewMemberRejected(User user, Realm realm){

        sendToUser(user,
                "Rob's Rules account for $realm.name rejected",
                """Your Rob's Rules account was rejected for organization $realm.name
                <br/><br/>
                If this is a mistake, please contact your organization's administrator.
        """)
    }

    def notifyAdminsUserSignup(User user, Realm realm){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(uri: "/")

        List<String> users = UserRealm.findAllByRealmAndAdminAndActive(realm, true, true).collect {it.user.username}

        sendToUsers(users,
                "Rob's Rules account for $realm.name user $user needs verification",
                """A Rob's Rules account created in organization $realm.name for $user
                <br/><br/>
                Please login to the system and verify or deny their access.
                <br/>
                <br/>
                <br/><br/>
                You can login to your organization by <a href="$link">clicking here</a>.
        """)
    }

    def confirmEmailAddressForCreateRealm(User userInstance){
        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'user',
                action: 'confirmEmailRealm', params: [id: userInstance.id, code: userInstance.confirmCode])

        sendToUser(userInstance,
                "Confirm your Rob's Rules account",
                """Thank you for creating an account at Rob's Rules voting system.
                <br/><br/>
                To confirm your email address, please access the following link:
                <br/><br/>
                <a href="$link">Click here to confirm your account.</a>
                <br/><br/>
                If you did not create an account or this message was sent to you in error, please disregard.
        """)
    }

    def confirmEmailAddressForJoinRealm(User userInstance){
        def link = "https://robsrules.com" + grailsLinkGenerator.link(controller: 'user',
                action: 'confirmEmailJoin', params: [id: userInstance.id, code: userInstance.confirmCode])

        println link

        sendToUser(userInstance,
                "Confirm your Rob's Rules account",
                """Thank you for creating an account at Rob's Rules voting system.
                <br/><br/>
                To confirm your email address, please access the following link:
                <br/><br/>
                <a href="$link">Click here to confirm your account.</a>
                <br/><br/>
                If you did not create an account or this message was sent to you in error, please disregard.
        """)
    }

    def notifyExistingNewMember(User user, Realm realm){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(uri: "/")

        boolean isObserver = user.getUserRealm().numberOfVotes == 0

        String observerAccount = """<br/><br/>Your account has been setup as an observer account.
            This means that you can see the organization's issues and comment on them, but you
            will not be able to cast a vote."""

        sendToUser(user,
                "Your Rob's Rules account for $realm.name",
                """A Rob's Rules account was created for you for organization $realm.name
                <br/><br/>
                Your username is your email address: $user.username
                <br/>
                Your password is your current password used for other organizations.
                ${isObserver ? observerAccount : ''}
                <br/><br/>
                You can login to your organization by <a href="$link">clicking here</a>.
        """)
    }

    def notifyPasswordChange(User user, String password){

        def link = "https://robsrules.com" + grailsLinkGenerator.link(uri: "/")

        sendToUser(user,
                "Password Reset for your Rob's Rules account",
                """Your Rob's Rules account.
                <br/><br/>
                Your username is your email address: $user.username
                <br/>
                Your temporary password is: $password
                <br/><br/>
                You can login to your organization by <a href="$link">clicking here</a>.
        """)
    }





}
