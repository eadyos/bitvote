package org.eady

import com.stripe.model.Charge

class Realm {

    String name
    boolean active = true
    int neededVotes = 3
    boolean majority = false

    Date expiryDate = new Date() + 30

    boolean quorum = false
    Integer quorumCount
    Integer quorumPercentage
    String joinPassword

    List<StripeCharge> charges

    Date dateCreated
    Date lastUpdated

    static hasMany = [
        charges: StripeCharge
    ]

    static constraints = {
        quorumCount nullable: true
        quorumPercentage nullable: true, range: 1..100
        joinPassword nullable: true, minSize: 10
    }

    def boolean isNew(){
        def monthAgo = new Date() - 30
        return dateCreated > monthAgo
    }

    def boolean isExpiringSoon(){
//        return (expiryDate - 32) < new Date()
        return false
    }

    def boolean isExpired(){
//        return expiryDate < new Date()
        return false
    }

}
