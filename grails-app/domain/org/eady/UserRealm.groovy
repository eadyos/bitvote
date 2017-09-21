package org.eady

import org.apache.commons.lang.builder.HashCodeBuilder

//TODO  Refactor this class to not be static create/remove weird
class UserRealm implements Serializable {

    private static final long serialVersionUID = 1

    User user
    Realm realm
    boolean admin
    boolean chairman = false
    boolean active = true
    String title
    int numberOfVotes = 1
    boolean issueNotificationEnabled = true
    boolean commentNotificationEnabled = true
    boolean completedNotificationEnabled = true
    boolean verified = true

    Date dateCreated
    Date lastUpdated

    boolean equals(other) {
        if (!(other instanceof UserRealm)) {
            return false
        }

        other.user?.id == user?.id &&
                other.realm?.id == realm?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (realm) builder.append(realm.id)
        builder.toHashCode()
    }

    static UserRealm get(long userId, long realmId) {
        UserRealm.where {
            user == User.load(userId) &&
                    realm == Realm.load(realmId)
        }.get()
    }

    static boolean exists(long userId, long realmId) {
        UserRealm.where {
            user == User.load(userId) &&
                    realm == Realm.load(realmId)
        }.count() > 0
    }

    static UserRealm create(User user, Realm realm, boolean isAdmin, boolean isChairman, boolean isActive, String title,
                            int numberOfVotes,  boolean issueNotificationEnabled, boolean commentNotificationEnabled,
                            boolean completedNotificationEnabled, boolean verified, boolean flush = false) {
        def instance = new UserRealm(user: user, realm: realm, admin: isAdmin, chairman: isChairman, active: isActive,
                title: title, numberOfVotes: numberOfVotes, issueNotificationEnabled: issueNotificationEnabled,
                commentNotificationEnabled: commentNotificationEnabled, completedNotificationEnabled: completedNotificationEnabled,
                verified: verified)
        instance.save(flush: flush, insert: true)
        instance
    }

    static boolean remove(User u, Realm r, boolean flush = false) {
        if (u == null || r == null) return false

        int rowCount = UserRealm.where {
            user == User.load(u.id) &&
                    realm == Realm.load(r.id)
        }.deleteAll()

        if (flush) { UserRealm.withSession { it.flush() } }

        rowCount > 0
    }

    static void removeAll(User u, boolean flush = false) {
        if (u == null) return

        UserRealm.where {
            user == User.load(u.id)
        }.deleteAll()

        if (flush) { UserRealm.withSession { it.flush() } }
    }

    static void removeAll(Realm r, boolean flush = false) {
        if (r == null) return

        UserRealm.where {
            realm == Realm.load(r.id)
        }.deleteAll()

        if (flush) { UserRealm.withSession { it.flush() } }
    }

    static constraints = {
        title nullable: true
        realm validator: { Realm r, UserRealm ur ->
            if (ur.user == null) return
            boolean existing = false
            UserRealm.withNewSession {
                existing = UserRealm.exists(ur.user.id, r.id)
            }
            if (existing) {
                return 'userRealm.exists'
            }
        }
    }

    static mapping = {
        id composite: ['realm', 'user']
        version false
    }
}
