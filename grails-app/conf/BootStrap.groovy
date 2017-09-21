import grails.util.Environment
import org.eady.Comment
import org.eady.Issue
import org.eady.Realm
import org.eady.Role
import org.eady.User
import org.eady.UserRealm
import org.eady.UserRole

//import org.eady.User

class BootStrap {

    def init = { servletContext ->

        def currentEnv = Environment.current

        def adminRole = Role.findByAuthority('ROLE_ADMIN')
        if(!adminRole){
            adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
        }
        def userRole = Role.findByAuthority('ROLE_USER')
        if(!userRole){
            userRole = new Role(authority: 'ROLE_USER').save(flush: true)
        }

        if(!Role.findByAuthority('ROLE_USER')){
            new Role(authority: 'ROLE_USER').save(flush: true)
        }
        if(!Role.findByAuthority('ROLE_ADMIN')){
            new Role(authority: 'ROLE_ADMIN').save(flush: true)
        }

        Realm realm = Realm.findByName("Homeowner HOA")
        Realm realm2 = Realm.findByName("Super Fun Club")
        if(!realm){
            realm = new Realm(name: "Homeowner HOA")
            realm.save(flush:true)
        }
        if(!realm2){
            realm2 = new Realm(name: "Super Fun Club")
            realm2.save(flush:true)
        }

        def adminUser = User.findByUsername('admin@admin.org')
        if(!adminUser){

            adminUser = new User(username: 'admin@admin.org', password: 'admin', firstName: "Admin",
                    lastName: "Person", selectedRealm: realm).save(flush: true)

            UserRole.create adminUser, userRole, true
            UserRole.create adminUser, adminRole, true
            def userRealm = UserRealm.create(
                    adminUser, //user:
                    realm, //realm:
                    true, //isAdmin:
                    true, //isChairman
                    true, //isActive:
                    "Demo President", //title:
                    1, //numberOfVotes:
                    false, //issueNotificationEnabled:
                    false, //commentNotificationEnabled:
                    false, //completedNotificationEnabled:
                    true, //verified
                    true //flush:
            )
        }


        if (!Issue.count()) {
            def issue = new Issue(proposedBy: adminUser, name: "Issue One", description: "The first issue",
                    status: Issue.Status.Discuss, created: new Date(), realm: realm)
            issue.save(failOnError: true)
            def comment = new Comment(user: adminUser, created: new Date(), text: "This is a comment", issue: issue)
            comment.save(failOnError: true)
        }
    }


    def destroy = {
    }
}
