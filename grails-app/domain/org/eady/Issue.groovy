package org.eady

class Issue {

    Date dateCreated
    Date lastUpdated

    String name
    String description
    User proposedBy
    Status status
    //Helps throttle the email notifier
    //from sending out tons of spam
    Status notifiedStatus

    Date statusDate

    List<Vote> votes
    List<Comment> comments

    Realm realm

    static hasMany = [
        votes: Vote,
        comments: Comment
    ]

    static constraints = {
        description size: 3..2000
        statusDate nullable: true
        notifiedStatus nullable: true
    }

    boolean hasVoted(User user){
        return votes.find {it.user == user} != null
    }

    List<Vote> getYayVotes(){
        return votes.findAll {it.type == Vote.Type.YAY}
    }

    List<Vote> getNayVotes(){
        return votes.findAll {it.type == Vote.Type.NAY}
    }

    List<Vote> getAbstainVotes(){
        return votes.findAll {it.type == Vote.Type.ABSTAIN}
    }

    boolean isPendingApproval(){

        int totalYays = this.getYayVotes()?.sum {it.weight} ?: 0
        int neededToPass = this.getVotesNeededToPass()
        boolean quorumReached = this.isQuorumReached()

        return totalYays >= neededToPass && quorumReached
    }

    boolean isPendingRejection(){

        int totalNays = this.getNayVotes()?.sum {it.weight} ?: 0
        int neededToPass = this.getVotesNeededToPass()
        boolean quorumReached = this.isQuorumReached()

        return totalNays >= neededToPass && quorumReached
    }

    boolean isInconclusive(){

        boolean inconclusive = false
        int totalVotes = this.votes?.size() ?: 0
        int totalVoters = UserRealm.countByRealmAndActiveAndNumberOfVotesGreaterThan(realm, true, 0)
        if(totalVotes == totalVoters){
            if(!isPendingApproval() && !isPendingRejection()){
                inconclusive = true
            }
        }
        return inconclusive
    }

    int getVotesNeededToPass(){

        int votesNeeded = realm.neededVotes

        def userRealms = UserRealm.findAllByRealmAndActiveAndNumberOfVotesGreaterThan(realm, true, 0)
        int totalVotes = userRealms.sum {
            it.numberOfVotes
        }
        int currentVotes = votes?.sum {
            it.weight
        } ?: 0

        if(!realm.quorum){

            if(realm.majority){
                votesNeeded = (int)totalVotes / 2
                votesNeeded += 1
            }

            //If voters have abstained it reduces the number of votes required for a majority
            //The amount of reduction is the vote-weight /2
            int abstainCount = this.abstainVotes?.sum {it.weight} ?: 0
            votesNeeded -= (int)abstainCount / 2


        }else{ //Quorum.  Needs majority of votes cast

            int minNeeded
            int quorumCount
            if(realm.quorumCount){
                quorumCount = realm.quorumCount
            }else{ //percentage
                quorumCount = (int)(totalVotes * realm.quorumPercentage) / 100
                if((int)(totalVotes * realm.quorumPercentage) % 100 != 0){
                    quorumCount += 1
                }
            }
            minNeeded = (int)quorumCount / 2
            minNeeded += 1

            //If the current vote level is higher than the min quorum level, then
            //the majority threshold changes.
            if(currentVotes > quorumCount){
                minNeeded = (int)currentVotes / 2
                minNeeded += 1
            }

            votesNeeded = minNeeded
        }

        return votesNeeded
    }

    int getVotesNeededForQuorum(){

        if(!realm.quorum){

            return 0

        }else{ //Quorum.  Needs majority of votes cast

            def userRealms = UserRealm.findAllByRealmAndActiveAndNumberOfVotesGreaterThan(realm, true, 0)
            int totalVotes = userRealms.sum {
                it.numberOfVotes
            }


            int quorumCount
            if(realm.quorumCount){
                quorumCount = realm.quorumCount
            }else{ //percentage
                quorumCount = (int)(totalVotes * realm.quorumPercentage) / 100
                if((int)(totalVotes * realm.quorumPercentage) % 100 != 0){
                    quorumCount += 1
                }
            }
            return quorumCount
        }

    }

    boolean isQuorumReached(){
        if(realm.quorum){
            int quorumCount
            def userRealms = UserRealm.findAllByRealmAndActiveAndNumberOfVotesGreaterThan(realm, true, 0)
            int totalVotes = userRealms.sum {
                it.numberOfVotes
            }
            int currentVotes = votes?.sum {
                it.weight
            } ?: 0

            if(realm.quorumCount){
                quorumCount = realm.quorumCount
            }else{ //percentage
                totalVotes * 100
                quorumCount = (int)(totalVotes * realm.quorumPercentage) / 100
                if((int)(totalVotes * realm.quorumPercentage) % 100 != 0){
                    quorumCount += 1
                }
            }

            return currentVotes >= quorumCount
        }else{
            return true
        }
    }

    int getVotesCastCount(){
        return this.votes?.sum {it.weight} ?: 0
    }

    Date getCloseDate(){
        return votes?.max {
            it.type == Vote.Type.ABSTAIN ? 0 : it.dateCreated.time
        }?.dateCreated ?: null
    }

    boolean isCompleted(){
        this.status in Status.completedStatuses
    }

    enum Status{
        Discuss('Open for Discussion'),
        Vote('Open for Voting'),
        Tabled('Tabled'),
        Approved('Approved'),
        Rejected('Rejected'),
        Approved_Pending('Approved (Pending)'),
        Rejected_Pending('Rejected (Pending)')

        final String name

        Status(def nameParam){
            this.name = nameParam
        }

        String getName(){
            return this.name;
        }


        static final List<Status> activeStatuses = [Discuss, Vote, Approved_Pending, Rejected_Pending]
        static final List<Status> inactiveStatuses = [Approved, Rejected, Tabled]
        static final List<Status> completedStatuses = [Approved, Rejected]
        static final List<Status> votingStatuses = [Vote, Approved_Pending, Rejected_Pending]

        public static getActiveStatuses(){
            return activeStatuses
        }

        public static getInactiveStatuses(){
            return inactiveStatuses
        }

        public static getCompletedStatuses(){
            return completedStatuses
        }

        public static getVotingStatuses(){
            return votingStatuses
        }
    }


}
