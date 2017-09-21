import org.eady.Issue
import org.eady.User
import org.eady.Vote

databaseChangeLog = {

	changeSet(author: "steve (generated)", id: "1404682148686-1") {
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

			column(name: "weight", type: "integer") {
				constraints(nullable: "false")
			}

			column(name: "votes_idx", type: "integer")
		}
	}

	changeSet(author: "steve (generated)", id: "1404682148686-2") {
		addColumn(tableName: "issue") {
			column(name: "notified_status", type: "varchar(255)")
		}
	}

	changeSet(author: "steve (generated)", id: "1404682148686-3") {
		addColumn(tableName: "realm") {
			column(name: "quorum", type: "bit") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "steve (generated)", id: "1404682148686-4") {
		addColumn(tableName: "realm") {
			column(name: "quorum_count", type: "integer")
		}
	}

	changeSet(author: "steve (generated)", id: "1404682148686-5") {
		addColumn(tableName: "realm") {
			column(name: "quorum_percentage", type: "integer")
		}
	}

	changeSet(author: "steve (generated)", id: "1404682148686-6") {
		dropForeignKeyConstraint(baseTableName: "issue_user", baseTableSchemaName: "bitvote", constraintName: "FK56089B71B9379D2D")
	}

	changeSet(author: "steve (generated)", id: "1404682148686-7") {
		dropForeignKeyConstraint(baseTableName: "issue_user", baseTableSchemaName: "bitvote", constraintName: "FK56089B71FF1C28F8")
	}

	changeSet(author: "steve (generated)", id: "1404682148686-8") {
		dropForeignKeyConstraint(baseTableName: "issue_user", baseTableSchemaName: "bitvote", constraintName: "FK56089B71F2C8910D")
	}

	changeSet(author: "steve (generated)", id: "1404682148686-11") {
		createIndex(indexName: "FK3752EA9F234647", tableName: "vote") {
			column(name: "issue_id")
		}
	}

	changeSet(author: "steve (generated)", id: "1404682148686-12") {
		createIndex(indexName: "FK3752EAF2C8910D", tableName: "vote") {
			column(name: "user_id")
		}
	}

    changeSet(author: "steve (generated)", id: "1404423656530-13") {
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


	changeSet(author: "steve (generated)", id: "1404682148686-14") {
		dropTable(tableName: "issue_user")
	}

	changeSet(author: "steve (generated)", id: "1404682148686-15") {
		addForeignKeyConstraint(baseColumnNames: "issue_id", baseTableName: "vote", constraintName: "FK3752EA9F234647", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "issue", referencesUniqueColumn: "false")
	}

	changeSet(author: "steve (generated)", id: "1404682148686-16") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "vote", constraintName: "FK3752EAF2C8910D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}
}
