package com.shuyun.query.jersey.context;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * GET: /ok
 */
@Path("/ok")
public class OkService {
    private static Logger logger = Logger.getLogger(OkService.class);
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGet() {
		return Response.status(200).entity("ok").build();
	}
}
