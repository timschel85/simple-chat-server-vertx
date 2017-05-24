package com.timschel85.blog;

import io.vertx.core.AbstractVerticle;

/**
 * Created by Charles on 2017-05-24.
 */

public class HTTPServerVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        vertx.createHttpServer().requestHandler(req->req.response().end("Hello World!")).listen(8080);
    }
}