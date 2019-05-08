package ratpack.hub

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import ratpack.exec.Blocking
import ratpack.exec.Promise
import ratpack.service.Service
import ratpack.service.StartEvent

import javax.sql.DataSource
import java.sql.Connection

@Singleton
class HubDAOService implements Service {

	private final DataSource dataSource

	@Inject
	HubDAOService(DataSource dataSource) {
		this.dataSource = dataSource
	}

	@Override
	void onStart(StartEvent event) throws Exception {
		//Note this is not a best practice only here for the demo
		//Use DB migrations like liquidbase or flyway
		createTables()
	}

	void createTables() {
		sql.execute("drop table if exists hubs;")
		sql.execute("create table hubs (id varchar(36) primary key, name VARCHAR (256), hardwareType VARCHAR (256))")

	}

	Promise<Hub> save(Hub hub) {
		def values = [name: hub.name, hardwareType: hub.hardwareType]
		values.id = hub.id ?: UUID.randomUUID().toString()

		return Blocking.get({
			sql.executeInsert(values, '''
INSERT INTO hubs (id, name, hardwareType)
VALUES (:id, :name, :hardwareType)
''')
		}).map({
			hub.id = hub.id ?: values.id
			return hub
		})
	}

	
	Promise<List<Hub>> getAll(){

		String query = "SELECT * FROM hubs;"
		return Blocking.get {
			readSql.rows(query)
		}.map(HubDAOService.&mapRows)
	}

	private static List<Hub> mapRows(List<GroovyRowResult> rows) {
		rows.collect(HubDAOService.&mapRow)
	}

	private static Hub mapRow(GroovyRowResult row) {
		new Hub(
			id: row.id,
			name: row.name,
			hardwareType:  row.hardwareType
		)
	}

	private Sql getSql() {
		new Sql(dataSource)
	}

	private Sql getReadSql() {
		new ReadOnlySql(dataSource)
	}

	private static class ReadOnlySql extends Sql {
		ReadOnlySql(DataSource dataSource) {
			super(dataSource)
		}

		Connection createConnection() {
			Connection connection = super.createConnection()
			connection.setReadOnly(true)
			connection
		}
	}
}
