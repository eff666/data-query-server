package com.shuyun.query.jersey.context;

import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.shuyun.query.meta.Results;
import org.apache.log4j.Logger;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import org.glassfish.jersey.server.ManagedAsync;

@Path("/search")
public class SearchService {
    private static Logger logger = Logger.getLogger(SearchService.class);

    @Context
    ActorSystem actorSystem;

    @GET
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doGet(@DefaultValue("false") @QueryParam("test") boolean test, @DefaultValue("plain") @QueryParam("style") String style,
                      @QueryParam("param") String param, @Suspended final AsyncResponse res) {
        doPost(test, style, param, res);
    }

    @POST
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doPost(@DefaultValue("false") @QueryParam("test") boolean test, @DefaultValue("plain") @QueryParam("style") final String style,
                       @FormParam("param") final String param, @Suspended final AsyncResponse res) {


        if (Strings.isNullOrEmpty(param)) {
            res.resume(Response.ok().entity(Results.NullParam).build());
            return;
        }

        final Stopwatch stopwatch = new Stopwatch().start();
        //final Stopwatch stopwatch = Stopwatch.createUnstarted().start();

        logger.info("query begin [" + param + "]");

        ActorRef queryActor = actorSystem.actorFor("/user/QueryRouter");

        Timeout timeout = new Timeout(Duration.create(300, TimeUnit.SECONDS));

        Future<Object> future = Patterns.ask(queryActor, param, timeout);

        future.onComplete(new OnComplete<Object>() {

            public void onComplete(Throwable failure, Object result) {

                if (failure != null) {

                    // time out exception occurs
                    if (failure.getMessage() != null) {
                        logger.error(failure.getMessage(), failure);
                    }

                    // mark as timeout error
                    res.resume(Response.ok().entity(result).build());

                } else {
                    res.resume(Response.ok().entity(result).build());
                }

                logger.info("query cost " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " query is " + param);
    }
}, actorSystem.dispatcher());

    }
}