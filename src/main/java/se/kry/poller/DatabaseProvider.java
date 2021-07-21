package se.kry.poller;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class DatabaseProvider {

	private final MySQLPool client;

	private static final String DB_NAME = "krydb";

	public DatabaseProvider(Vertx vertx) {
		client = MySQLPool.pool(vertx, new MySQLConnectOptions().setPort(3306).setHost("localhost").setDatabase(DB_NAME)
				.setUser("root").setPassword("my-secret-pw"), new PoolOptions());
	}

	public MySQLPool getClient() {
		return client;
	}

	public Future<RowSet<Row>> query(String query) {
		return client.query(query).execute();
	}

	public Future<RowSet<Row>> preparedQuery(String query, Tuple tuple) {
		return client.preparedQuery(query).execute(tuple);
	}
}