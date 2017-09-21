databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404834294685-1") {
		addColumn(tableName: "user") {
			column(name: "confirm_code", type: "varchar(255)")
		}
	}
}
