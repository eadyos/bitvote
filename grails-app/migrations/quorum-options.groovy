databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404502282774-1") {
		addColumn(tableName: "realm") {
			column(name: "quorum", type: "bit", defaultValueBoolean: false) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "steve (generated)", id: "1404502282774-2") {
		addColumn(tableName: "realm") {
			column(name: "quorum_count", type: "integer")
		}
	}

	changeSet(author: "steve (generated)", id: "1404502282774-3") {
		addColumn(tableName: "realm") {
			column(name: "quorum_percentage", type: "integer")
		}
	}
}
