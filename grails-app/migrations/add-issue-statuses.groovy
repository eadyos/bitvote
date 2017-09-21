import org.eady.Issue
import org.eady.User
import org.eady.Vote

databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1406555666095-1") {
		addColumn(tableName: "issue") {
			column(name: "status_date", type: "datetime")
		}
	}

    changeSet(author: "steve (generated)", id: "1406555666095-2") {
        grailsChange{
            change{
                groovy.sql.Sql s = sql
                s.executeUpdate("update issue set status_date = completed")
            }
        }
    }

    changeSet(author: "steve (generated)", id: "1406555666095-3") {
        grailsChange{
            change{
                groovy.sql.Sql s = sql
                s.executeUpdate("update issue set status = 'Vote' where status = 'Open'")
            }
        }
    }

    changeSet(author: "steve (generated)", id: "1406555666095-4") {
        dropColumn(columnName: "completed", tableName: "issue")
    }

    changeSet(author: "steve (generated)", id: "1406555666095-5") {
        addColumn(tableName: "user_realm") {
            column(name: "chairman", type: "bit"){
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "steve (generated)", id: "1406555666095-6") {
        grailsChange{
            change{
                groovy.sql.Sql s = sql
                s.executeUpdate("update user_realm set chairman = admin")
            }
        }
    }
}
