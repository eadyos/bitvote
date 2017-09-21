package org.eady

import grails.transaction.Transactional
import org.apache.commons.lang.RandomStringUtils

@Transactional
class SecurityService {

    def springSecurityService

    static final int VALID = 1
    static final int INVALID = 2
    static final int EXPIRED = 3
    static final int INACTIVE = 4
    static final int UNVERIFIED = 5

    int verifyRealmAccess(Issue issue) {
        User user = springSecurityService.currentUser
        def ur = UserRealm.findByUserAndRealm(user, issue?.realm)
        if(ur && !ur.active){
            return INACTIVE
        }
        if(!user.verified){
            return UNVERIFIED
        }
        if(ur && ur.realm.expired){
            return EXPIRED
        }
        if(ur != null && !issue?.isDirty("realm")){
            return VALID
        }else{
            return INVALID
        }
    }

    int verifyRealmAccess(User otherUser) {
        User user = springSecurityService.currentUser
        Realm selectedRealm = user.selectedRealm
        def ur = UserRealm.findByUserAndRealm(user, selectedRealm)
        def otherUr = UserRealm.findByUserAndRealm(otherUser, selectedRealm)

        if(ur && !ur.active){
            return INACTIVE
        }
        if(!user.verified){
            return UNVERIFIED
        }
        if(ur && ur.realm.expired){
            return EXPIRED
        }
        if(otherUr != null){
            return VALID
        }else{
            return INVALID
        }
    }

    int verifyRealmAccess(Realm realm) {
        User user = springSecurityService.currentUser
        def ur = UserRealm.findByUserAndRealm(user, realm)

        if(ur && !ur.active){
            return INACTIVE
        }
        if(!user.verified){
            return UNVERIFIED
        }
        if(ur && ur.realm.expired){
            return EXPIRED
        }
        if(ur != null){
            return VALID
        }else{
            return INVALID
        }
    }

    String generatePassword(){
        String charset = (('A'..'Z') + ('0'..'9')).join()
        Integer length = 9
        String randomString = RandomStringUtils.random(length, charset.toCharArray())
        return randomString
    }
}
