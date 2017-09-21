databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404770421895-1") {
		addColumn(tableName: "user_realm") {
			column(name: "verified", type: "bit", defaultValueBoolean: true) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "steve (generated)", id: "1404770421895-2") {
		dropColumn(columnName: "verified", tableName: "user")
	}
}
