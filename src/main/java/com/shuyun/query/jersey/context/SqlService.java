package com.shuyun.query.jersey.context;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/sql")
public class SqlService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void doGet(@DefaultValue("false") @QueryParam("test") boolean test, @DefaultValue("plain") @QueryParam("style") String style,
                      @QueryParam("report_param") String param, @Suspended final AsyncResponse res) {
        doPost(test, style, param, res);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void doPost(@DefaultValue("false") @QueryParam("test") boolean test, @DefaultValue("plain") @QueryParam("style") final String style,
                       @FormParam("report_param") final String param, @Suspended final AsyncResponse res) {
        String ret = param;
        res.resume(Response.ok().entity(ret).build());

    }
}
