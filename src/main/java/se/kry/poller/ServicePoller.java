package se.kry.poller;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import se.kry.poller.persistence.PersistenceRepository;

public class ServicePoller extends AbstractVerticle{

	private final WebClient client;
	private final PersistenceRepository persistenceRepository;

	public ServicePoller(WebClient webClient, PersistenceRepository persistenceRepository) {
		this.client = webClient;
		this.persistenceRepository = persistenceRepository;
	}
	
	@Override
	public void start() {
		vertx.setPeriodic(5000, timerId -> poll(persistenceRepository.findAllServices()));
	}

	public void poll(Future<RowSet<Row>> futureRow) {
		futureRow.onComplete(ar ->{
			if(ar.succeeded()) {
				ar.result().forEach(row -> {
					checkService(row.toJson());
				});
			}
		});
	}

	private void checkService(JsonObject service) {
		String url = service.getString("service_url");
		JsonObject jsonObject = new JsonObject()
				.put("updated_at", new Date().toString())
				.put("service_id", service.getString("service_id"));
		
		client.getAbs(url).timeout(4000).send()
			.onComplete(ar ->{
				if (ar.succeeded() && ar.result().statusCode() == 200) {
					jsonObject.put("service_status", "OK");
					persistenceRepository.updateServiceStatus(jsonObject);
				} else {
					jsonObject.put("service_status", "FAIL");
					persistenceRepository.updateServiceStatus(jsonObject);
				}
			});
	}
}