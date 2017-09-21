package bitvote

class RequestFilters {

    def springSecurityService

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                params.currentUser = springSecurityService.currentUser
            }
            after = { Map model ->
                if(!model) {
                    model = [:]
                }
                if(params && params['currentUser']){
                    model.currentUser = params.currentUser
                }
            }
            afterView = { Exception e ->

            }
        }
        noRealm(controller:'issue') {
            before = {
                if(springSecurityService.currentUser && !springSecurityService.currentUser.selectedRealm){
                    redirect(controller: "realm", action: 'create')
                    return false
                }
            }
            after = { Map model ->
            }
            afterView = { Exception e ->

            }
        }
    }
}
