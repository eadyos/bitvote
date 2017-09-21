databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404758266792-1") {
		addColumn(tableName: "realm") {
			column(name: "join_password", type: "varchar(255)")
		}
	}

	changeSet(author: "steve (generated)", id: "1404758266792-2") {
		addColumn(tableName: "user") {
			column(name: "verified", type: "bit") {
				constraints(nullable: "false")
			}
		}
	}
}
