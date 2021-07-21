package se.kry.poller.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import se.kry.poller.persistence.PersistenceRepository;

public class UserVerticle extends AbstractVerticle {
	
	private final PersistenceRepository persistenceRepository;

	public UserVerticle(PersistenceRepository persistenceRepository) {
		this.persistenceRepository = persistenceRepository;
	}

	@Override
	public void start() {
		vertx.eventBus().consumer("user.register", this::register);
	}
	
	private void register(Message<JsonObject> message) {
		JsonObject user = message.body();
		persistenceRepository.findUser(user.getString("username"))
		.onComplete(ar ->{
			if(ar.succeeded() && ar.result().size() == 0) {
				persistenceRepository.registerUser(user)
					.onSuccess(result -> message.reply(result.size()))
					.onFailure(err -> message.fail(500, err.getMessage()));
			} else {
				message.fail(422, "Username is already taken! Try another.");
			}
		});
	}
}