package com.timschel85.blog;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Charles on 2017-05-24.
 */

public class HTTPServerVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();

        Logger logger = LoggerFactory.getLogger(HTTPServerVerticle.class);

        Router router = Router.router(vertx);
        router.route("/").handler(ctx -> {
            HttpServerRequest req = ctx.request();
            HttpMethod method = req.method();
            String uri = req.uri();
            String path = req.path();
            String query= req.query();
            logger.info("received http request: {method="+method+", uri="+uri+", path="+path+", query="+query+"}");

            // Http request params
            List<Map.Entry<String, String>> params = req.params().entries();
            for(Map.Entry<String, String> param : params) {
                logger.info("param["+param.getKey()+"] = "+param.getValue());
            }

            // Http request headers
            List<Map.Entry<String, String>> headers = req.headers().entries();
            for(Map.Entry<String, String> header : headers) {
                logger.info("header["+header.getKey()+"] = "+header.getValue());
            }

            req.bodyHandler(buffer-> {
               logger.info("received data: " + buffer.toString());
            });

            req.response().end("OK");
        });

        SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        sockJSHandler.socketHandler(socket -> {
           socket.handler(buffer -> {
                logger.info("received data : " + buffer.toString());
                socket.write(buffer);
           });
           socket.exceptionHandler(Throwable::printStackTrace);
        });
        router.route("/mySockJS/*").handler(sockJSHandler);


        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(8080, ar-> {
                if(ar.succeeded())
                    logger.info("bind result : Success");
                else
                    logger.info("bind result : failed");
            });
    }
}