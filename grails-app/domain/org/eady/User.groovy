package org.eady

class User {

	transient springSecurityService
    transient userRealm

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

    String firstName
    String lastName
    Realm selectedRealm
    String confirmCode

    Date dateCreated
    Date lastUpdated


	static transients = ['springSecurityService', 'userRealm']

	static constraints = {
		username blank: false, unique: true, email: true
		password blank: false
        selectedRealm nullable: true
        confirmCode nullable: true
	}

	static mapping = {
		password column: '`password`'
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role }
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}

    String toString(){
        if(this.id){
            return firstName + ' ' + lastName
        }
        else{
            return super.toString()
        }
    }

    List<Realm> getAvailableRealms(){
        return UserRealm.findAllByUser(this)?.collect{it.realm}
    }

    List<Realm> getAvailableActiveRealms(){
        return getAvailableRealms().findAll{it.active}
    }

    List<Realm> getAvailableInactiveRealms(){
        return getAvailableRealms().findAll {!it.active}
    }

    boolean isAdmin(){
        if(this.id){
            return UserRealm.get(this.id, springSecurityService.currentUser?.selectedRealm?.id ?: 0).admin ?: false
        }else{
            return false
        }
    }
    boolean isChairman(){
        if(this.id){
            return UserRealm.get(this.id, springSecurityService.currentUser?.selectedRealm?.id ?: 0).chairman ?: false
        }else{
            return false
        }
    }
    boolean isVerified(){
        if(this.id){
            return UserRealm.get(this.id, springSecurityService.currentUser?.selectedRealm?.id ?: 0).verified ?: false
        }else{
            return false
        }
    }
    boolean isActive(){
        if(this.id){
            def ur = UserRealm.get(this.id, springSecurityService.currentUser?.selectedRealm?.id ?: 0)
            if(ur.active != null){
                return ur.active
            }else{
                return true
            }
        }else{
            return true
        }
    }

    //Returns the UserRealm of the selected realm
    UserRealm getUserRealm(){
        if(!userRealm){
            if(this.id){
                userRealm = UserRealm.get(this.id, springSecurityService.currentUser.selectedRealm?.id)
            }
            if(!userRealm){
                userRealm = new UserRealm()
            }
        }
        return userRealm
    }
    void setUserRealm(UserRealm ur){
        this.userRealm = ur
    }

    String getRoles(){
        def roles = []
        UserRealm userRealm = getUserRealm()
        if(userRealm.admin){
            roles << "Admin"
        }
        if(userRealm.chairman){
            roles << "Chairman"
        }
        if(userRealm.numberOfVotes > 0){
            roles << "Member"
        }else{
            roles << "Observer"
        }
        return roles.join(' ')
    }
}
