package se.kry.poller.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.auth.sqlclient.impl.SqlAuthenticationImpl;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import se.kry.poller.DatabaseProvider;

public class PersistenceRepository {

	private DatabaseProvider databaseProvider;

	public PersistenceRepository(DatabaseProvider databaseProvider) {
		this.databaseProvider = databaseProvider;
	}

	public Future<RowSet<Row>> registerUser(JsonObject user) {
		return databaseProvider.preparedQuery("INSERT INTO users (username, password) VALUES (?, ?);",
				Tuple.of(user.getString("username"), hashPassword(user.getString("password"))));
	}

	public Future<RowSet<Row>> findUser(String username) {
		return databaseProvider.preparedQuery("SELECT username FROM users WHERE username = ?;", Tuple.of(username));
	}

	public Future<RowSet<Row>> registerService(JsonObject service) {
		return databaseProvider.preparedQuery(
				"INSERT INTO services (service_name, service_url, service_status, username) VALUES (?, ?, ?, ?);",
				Tuple.of(service.getString("service_name"), service.getString("service_url"), "",
						service.getString("username")));
	}

	public Future<RowSet<Row>> findService(JsonObject service) {
		return databaseProvider.preparedQuery(
				"SELECT username FROM services WHERE service_name = ? AND service_url = ? AND username = ?;",
				Tuple.of(service.getString("service_name"), service.getString("service_url"),
						service.getString("username")));
	}
	
	public Future<RowSet<Row>> checkServiceStatus(JsonObject service) {
		return databaseProvider.preparedQuery(
				"SELECT service_status, updated_at FROM services WHERE service_id = ?;",
				Tuple.of(service.getString("service_id")));
	}

	public Future<RowSet<Row>> findServices(String username) {
		return databaseProvider.preparedQuery(
				"SELECT service_id, service_name, service_url, created_at, updated_at, service_status FROM services WHERE username = ? ORDER BY service_name;",
				Tuple.of(username));
	}

	public Future<RowSet<Row>> findAllServices() {
		return databaseProvider.query("SELECT service_id, service_url FROM services;");
	}

	public Future<RowSet<Row>> updateService(JsonObject service) {
		return databaseProvider.preparedQuery(
				"UPDATE services SET service_name = ?, service_url = ? WHERE service_id = ?;",
				Tuple.of(service.getString("service_name"), service.getString("service_url"),
						service.getString("service_id")));
	}

	public Future<RowSet<Row>> updateServiceStatus(JsonObject service) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		return databaseProvider.preparedQuery("UPDATE services SET service_status = ?, updated_at = ? WHERE service_id = ?;",
				Tuple.of(service.getString("service_status"), date, service.getString("service_id")));
	}

	public Future<RowSet<Row>> deleteService(JsonObject service) {
		return databaseProvider.preparedQuery("DELETE FROM services WHERE service_id = ?",
				Tuple.of(service.getInteger("service_id")));
	}

	private String hashPassword(String password) {
		SqlAuthenticationImpl sqlAuth = new SqlAuthenticationImpl(databaseProvider.getClient(),
				new SqlAuthenticationOptions());

		return sqlAuth.hash("pbkdf2", VertxContextPRNG.current().nextString(32), password);
	}
}
