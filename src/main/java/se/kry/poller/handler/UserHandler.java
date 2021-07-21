package se.kry.poller.handler;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class UserHandler {
	
    public void register(RoutingContext routingContext) {
		JsonObject user = new JsonObject()
				.put("username", routingContext.request().getParam("username"))
				.put("password", routingContext.request().getParam("password"));
		
		routingContext.vertx().eventBus().request("user.register", user, ar -> {
			if(ar.succeeded()) {
				routingContext.response()
	            .putHeader("Content-Type", "application/json; charset=UTF-8")
	            .setStatusCode(201)
	            .end(Json.encode(ar.result().body()));
			} else {
				routingContext.response().end(ar.cause().getMessage());
			}
		});
	}
    
    public void loggedIn(RoutingContext routingContext) {
    	int statusCode;
    	JsonObject json = new JsonObject();
    	if(routingContext.user() != null) {
    		statusCode = 200;
    		json.put("logged_in", true).mergeIn(routingContext.user().principal());
    	} else {
    		statusCode = 401;
    		json.put("logged_in", false);
    	}
    	routingContext.response().setStatusCode(statusCode).end(json.toString());
    }
    
    public void logOut(RoutingContext routingContext) {
    	int statusCode;
    	if(routingContext.user() != null) {
    		statusCode = 200;
    		routingContext.clearUser();
    	} else {
    		statusCode = 401;
    	}
    	routingContext.response().setStatusCode(statusCode).end();
    }
    
}