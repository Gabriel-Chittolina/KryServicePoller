package se.kry.poller.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import se.kry.poller.persistence.PersistenceRepository;

public class ServiceVerticle extends AbstractVerticle{
	private final PersistenceRepository persistenceRepository;
	
	public ServiceVerticle(PersistenceRepository persistenceRepository) {
		this.persistenceRepository = persistenceRepository;
	}

	@Override
	public void start() {
		EventBus eventBus = vertx.eventBus();
		
		eventBus.consumer("services.find", this::find);
		eventBus.consumer("service.register", this::register);
		eventBus.consumer("service.update", this::update);
		eventBus.consumer("service.checkStatus", this::checkStatus);
		eventBus.consumer("service.delete", this::delete);
	}
	
	private void register(Message<JsonObject> message){
		JsonObject service = message.body();
		persistenceRepository.findService(service)
		.onComplete(ar ->{
			if(ar.succeeded() && ar.result().size() == 0) {
			persistenceRepository.registerService(service)
			.onSuccess(row -> message.reply(row.size()))
			.onFailure(err -> message.fail(500, err.getMessage()));
			} else {
				message.fail(422, "Not allowed to register the same service twice.");
			}
		});
		
	}
	
	private void update(Message<JsonObject> message) {
		JsonObject service = message.body();
		persistenceRepository.updateService(service)
		.onComplete(ar -> messageHandler(ar, message, "updated"));
	}
	private void checkStatus(Message<JsonObject> message) {
		persistenceRepository.checkServiceStatus(message.body())
		.onComplete(ar -> {
			if(ar.succeeded()) {
				JsonArray services = new JsonArray();
				ar.result().forEach(row -> {
					services.add(row.toJson());
				});
				message.reply(services.toString());
			} else {
				message.fail(500, ar.cause().getMessage());
			}
		});
	}
	
	private void delete(Message<JsonObject> message){
		JsonObject service = message.body();
		persistenceRepository.deleteService(service)
		.onComplete(ar -> messageHandler(ar, message, "deleted"));
	}
	
	private void find(Message<JsonObject> message) {
		persistenceRepository.findServices(message.body().getString("username"))
		.onComplete(ar -> {
			if(ar.succeeded()) {
				JsonArray services = new JsonArray();
				ar.result().forEach(row -> {
					services.add(row.toJson());
				});
				message.reply(services.toString());
			} else {
				message.fail(500, ar.cause().getMessage());
			}
		});
	}
	
	private void messageHandler(AsyncResult<RowSet<Row>> ar, Message<JsonObject> message, String action) {
		if(ar.succeeded() && ar.result().rowCount() == 1) {
			message.reply("Service " + action + " successfully.");
		} else if(ar.succeeded()){
			message.fail(422, "Service does not exist.");
		} else {
			message.fail(500, ar.cause().getMessage());
		}
	}
}
