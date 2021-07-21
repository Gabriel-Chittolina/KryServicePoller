package se.kry.poller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.web.client.WebClient;
import se.kry.poller.persistence.PersistenceRepository;
import se.kry.poller.verticle.HttpVerticle;
import se.kry.poller.verticle.ServiceVerticle;
import se.kry.poller.verticle.UserVerticle;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		DatabaseProvider databaseProvider = new DatabaseProvider(vertx);
		SqlAuthenticationOptions options = new SqlAuthenticationOptions();
		PersistenceRepository persistenceRepository = new PersistenceRepository(databaseProvider);
		WebClient webClient = WebClient.create(vertx);
		
		CompositeFuture.all(vertx.deployVerticle(new HttpVerticle(SqlAuthentication.create(databaseProvider.getClient(), options))),
				vertx.deployVerticle(new UserVerticle(persistenceRepository)),
				vertx.deployVerticle(new ServiceVerticle(persistenceRepository)),
				vertx.deployVerticle(new ServicePoller(webClient, persistenceRepository)))
				.onSuccess(event -> startPromise.complete())
				.onFailure(err -> startPromise.fail(err.getMessage()));
		
	}
}