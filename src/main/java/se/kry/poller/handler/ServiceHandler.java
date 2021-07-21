package se.kry.poller.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ServiceHandler {
	
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    
    public void register(RoutingContext routingContext) {
		JsonObject service = new JsonObject()
				.put("service_name", routingContext.request().getParam("service_name"))
				.put("service_url", routingContext.request().getParam("service_url"))
				.put("username", routingContext.user().principal().getString("username"));
		
		routingContext.vertx().eventBus().request("service.register", service, ar -> responseHandler(ar, routingContext, 201));
	}
    
    public void update(RoutingContext routingContext) {
    	JsonObject service = new JsonObject()
    			.put("service_id", routingContext.request().getParam("service_id"))
    			.put("service_name", routingContext.request().getParam("service_name"))
    			.put("service_url", routingContext.request().getParam("service_url"));
    	
    	routingContext.vertx().eventBus().request("service.update", service, ar -> responseHandler(ar, routingContext));
    }
    
    public void checkStatus(RoutingContext routingContext) {
    	JsonObject service = new JsonObject()
    			.put("service_id", routingContext.request().getParam("service_id"));
    	
    	routingContext.vertx().eventBus().request("service.checkStatus", service, ar -> responseHandler(ar, routingContext));
    }
    
    public void delete(RoutingContext routingContext) {
		JsonObject message = new JsonObject()
				.put("service_id", Integer.parseInt(routingContext.request().getParam("service_id")));
		routingContext.vertx().eventBus().request("service.delete", message, ar -> responseHandler(ar, routingContext));
	}
	
    public void find(RoutingContext routingContext) {
		routingContext.vertx().eventBus().request("services.find", routingContext.user().principal(), ar -> responseHandler(ar, routingContext));
	}
	
    private void responseHandler(AsyncResult<Message<Object>> ar, RoutingContext routingContext) {
		responseHandler(ar, routingContext, 200);
	}
	
	private void responseHandler(AsyncResult<Message<Object>> ar, RoutingContext routingContext, int succeededStatusCode) {
		if(ar.succeeded()) {
			routingContext.response()
            .putHeader(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
            .setStatusCode(succeededStatusCode)
            .end(ar.result().body().toString());
		} else {
			routingContext.response().setStatusCode(500).end(ar.cause().getMessage());
		}
	}
}