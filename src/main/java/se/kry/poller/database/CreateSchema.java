package se.kry.poller.database;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class CreateSchema {
	private static final String DB_NAME = "krydb";
	private static final String CREATE_DB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME + ";";
	private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + DB_NAME + ".users ( username varchar(32) NOT NULL, password varchar(150) NOT NULL, CONSTRAINT users_PK PRIMARY KEY (username));";
	private static final String CREATE_TABLE_SERVICES = "CREATE TABLE IF NOT EXISTS " + DB_NAME + ".services ( service_id INT UNSIGNED auto_increment NOT NULL, service_name varchar(100) NOT NULL, service_url varchar(100) NOT NULL, created_at DATETIME DEFAULT now() NOT NULL, updated_at DATETIME DEFAULT now() NOT NULL, service_status varchar(4) NOT NULL, username varchar(32) NOT NULL, CONSTRAINT services_PK PRIMARY KEY (service_id), CONSTRAINT services_FK FOREIGN KEY (username) REFERENCES " + DB_NAME + ".users(username) );";
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		MySQLPool client = MySQLPool.pool(vertx, new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setUser("root").setPassword("my-secret-pw"), new PoolOptions());
		
		client.query(CREATE_DB)
		.execute()
		.compose(resdb -> 
			client.query(CREATE_TABLE_USERS)
				.execute()
				.compose(resusrtbl -> 
					client.query(CREATE_TABLE_SERVICES)
						.execute())
		).onComplete(ar -> {
			if(ar.succeeded()) {
				System.out.println("Database migrated successfully.");
			} else {
				System.out.println(ar.cause().getMessage());
			}
			vertx.close(exit ->{
				System.exit(0);
			});
		});
	}
}