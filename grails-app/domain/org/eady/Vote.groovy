package org.eady

class Vote {

    Issue issue
    User user
    Type type
    int weight = 1

    Date dateCreated

    static constraints = {
    }

    enum Type{
        YAY,
        NAY,
        ABSTAIN
    }
}
