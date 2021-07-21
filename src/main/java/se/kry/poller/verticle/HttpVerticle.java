package se.kry.poller.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import se.kry.poller.handler.ServiceHandler;
import se.kry.poller.handler.UserHandler;

public class HttpVerticle extends AbstractVerticle {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private AuthenticationProvider authProvider;
	
	public HttpVerticle(AuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}
	
	@Override
	public void start(Promise<Void> startPromise) {
		Router baseRouter = Router.router(vertx);
		Router apiRouter = Router.router(vertx);
		
		ServiceHandler serviceHandler = new ServiceHandler();
		UserHandler userHandler = new UserHandler();
		
		baseRouter.route().handler(CorsHandler.create("*")
				.allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST)
				.allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
				.allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
				.allowedMethod(io.vertx.core.http.HttpMethod.PUT)
				.allowedHeader("Access-Control-Request-Method")
				.allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin")
				.allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type")
				.allowCredentials(true));
		
		apiRouter.post("/service*").handler(BodyHandler.create());
		apiRouter.put("/service*").handler(BodyHandler.create());
		apiRouter.delete("/service*").handler(BodyHandler.create());
		apiRouter.route("/register*").handler(BodyHandler.create());
		apiRouter.route("/login*").handler(BodyHandler.create());
		
		apiRouter.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
		
		SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
		sessionHandler.setCookieSameSite(CookieSameSite.STRICT);
		apiRouter.route().handler(sessionHandler);
		
		apiRouter.route("/logged_in").handler(ctx -> userHandler.loggedIn(ctx));
		
		apiRouter.post("/login").handler(FormLoginHandler.create(authProvider));
	    
	    apiRouter.post("/register").handler(ctx -> userHandler.register(ctx));
	    
	    apiRouter.delete("/logout").handler(ctx -> userHandler.logOut(ctx));
		
		apiRouter.route("/service*").handler(RedirectAuthHandler.create(authProvider, "/")); 
	    
	    apiRouter.post("/service").handler(ctx -> serviceHandler.register(ctx));
	    
	    apiRouter.get("/services").handler(ctx -> serviceHandler.find(ctx));
	    
	    apiRouter.delete("/service").handler(ctx -> serviceHandler.delete(ctx));
	    
	    apiRouter.put("/service").handler(ctx -> serviceHandler.update(ctx));
	    
	    apiRouter.get("/service/status").handler(ctx -> serviceHandler.checkStatus(ctx));
	    
	    baseRouter.get().handler(StaticHandler.create());
	    	    
	    baseRouter.mountSubRouter("/api", apiRouter);
	    
		vertx.createHttpServer(new HttpServerOptions().setMaxHeaderSize(32 * 1024))
				.requestHandler(baseRouter)
				.listen(8080)
				.onSuccess(server -> {
					logger.info("HTTP server started on port " + server.actualPort());
				}).onFailure(event -> {
					logger.error("Failed to start HTTP server: " + event.getMessage());
				});
	}
}