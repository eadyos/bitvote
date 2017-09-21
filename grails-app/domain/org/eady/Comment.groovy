package org.eady

class Comment {

    Date dateCreated
    Date lastUpdated

    User user
    String text


    static constraints = {
        text size: 2..2000
    }

    static belongsTo = [ issue: Issue ]


}
