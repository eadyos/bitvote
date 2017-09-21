import org.eady.Issue
import org.eady.User
import org.eady.Vote

databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404423656530-1") {
		createTable(tableName: "vote") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "votePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "issue_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "votes_idx", type: "integer")
		}
	}

	changeSet(author: "steve (generated)", id: "1404423656530-2") {
		createIndex(indexName: "FK3752EA9F234647", tableName: "vote") {
			column(name: "issue_id")
		}
	}

	changeSet(author: "steve (generated)", id: "1404423656530-3") {
		createIndex(indexName: "FK3752EAF2C8910D", tableName: "vote") {
			column(name: "user_id")
		}
	}

	changeSet(author: "steve (generated)", id: "1404423656530-4") {
		addForeignKeyConstraint(baseColumnNames: "issue_id", baseTableName: "vote", constraintName: "FK3752EA9F234647", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "issue", referencesUniqueColumn: "false")
	}

	changeSet(author: "steve (generated)", id: "1404423656530-5") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "vote", constraintName: "FK3752EAF2C8910D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

    changeSet(author: "steve (generated)", id: "1404423656530-6") {
        grailsChange{
            change{
                def updates =  []
                groovy.sql.Sql s = sql
                s.eachRow('select user_id, issue_nays_id from issue_user where issue_nays_id is not null'){ row ->

                    def vote = new Vote(type: Vote.Type.NAY, issue: Issue.get(row.issue_nays_id), user: User.get(row.user_id))
                    vote.save(flush:true)
                }
                s.eachRow('select user_id, issue_yays_id from issue_user where issue_yays_id is not null'){ row ->

                    def vote = new Vote(type: Vote.Type.YAY, issue: Issue.get(row.issue_yays_id), user: User.get(row.user_id))
                    vote.save(flush:true)
                }

            }
        }
    }
    changeSet(author: "steve (generated)", id: "1404423656530-7") {
        dropForeignKeyConstraint(baseTableName: "issue_user", baseTableSchemaName: "bitvote", constraintName: "FK56089B71B9379D2D")
    }

    changeSet(author: "steve (generated)", id: "1404423656530-8") {
        dropForeignKeyConstraint(baseTableName: "issue_user", baseTableSchemaName: "bitvote", constraintName: "FK56089B71F2C8910D")
    }

    changeSet(author: "steve (generated)", id: "1404423656530-9") {
        dropForeignKeyConstraint(baseTableName: "issue_user", baseTableSchemaName: "bitvote", constraintName: "FK56089B71FF1C28F8")
    }

    changeSet(author: "steve (generated)", id: "1404423656530-10") {
        dropTable(tableName: "issue_user")
    }

    /* Need to store the weight, as it can change */
    changeSet(author: "steve (generated)", id: "1404423656530-11") {
        addColumn(tableName: "vote") {
            column(name: "weight", type: "integer")
        }
    }


}
