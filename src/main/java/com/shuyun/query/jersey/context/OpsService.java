package com.shuyun.query.jersey.context;

import com.google.common.io.Closeables;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;

/**
 * GET: /ok
 */

public class OpsService extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(200);
		ServletOutputStream servletOutputStream = null;
		try{
			servletOutputStream = response.getOutputStream();
			servletOutputStream.write("pong".getBytes());
			servletOutputStream.flush();
		}
		catch(IOException e){
			// ignore
		}
		finally {
			try{
				Closeables.close(servletOutputStream, true);
			}
			catch (IOException e){
				// ignore
			}
		}
	}
}
