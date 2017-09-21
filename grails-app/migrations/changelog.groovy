databaseChangeLog = {

    //put schema changes up here

    //put data migrations scripts at the end. Avoid gorm statements in migration scripts.

	include file: 'observer-and-notification-options.groovy'

	include file: 'vote-domain-quorum-notification-status.groovy'

	include file: 'join-password.groovy'


	include file: 'fix-verified.groovy'

	include file: 'user-confirm-code.groovy'

	include file: 'add-issue-statuses.groovy'
}
