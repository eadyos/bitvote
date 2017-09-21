package org.eady

class StripeCharge {

    BigDecimal amount
    User user

    String stripeJson

    Date dateCreated
    Date lastUpdated


    static constraints = {
        stripeJson maxSize: 2000
    }
}
