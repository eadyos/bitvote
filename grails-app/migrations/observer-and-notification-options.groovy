databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404304661691-1") {
		addColumn(tableName: "user_realm") {
			column(name: "comment_notification_enabled", type: "bit", defaultValueBoolean: true) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "steve (generated)", id: "1404304661691-2") {
		addColumn(tableName: "user_realm") {
			column(name: "issue_notification_enabled", type: "bit", defaultValueBoolean: true) {
				constraints(nullable: "false")
			}
		}
	}

    changeSet(author: "steve (generated)", id: "1404304661691-3") {
        addColumn(tableName: "user_realm") {
            column(name: "completed_notification_enabled", type: "bit", defaultValueBoolean: true) {
                constraints(nullable: "false")
            }
        }
    }

	changeSet(author: "steve (generated)", id: "1404304661691-4") {
		addColumn(tableName: "user_realm") {
			column(name: "number_of_votes", type: "integer", defaultValueNumeric: 1) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "steve (generated)", id: "1404304661691-5") {
		addColumn(tableName: "user_realm") {
			column(name: "title", type: "varchar(255)")
		}
	}
}
