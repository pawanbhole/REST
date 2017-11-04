package com.mkyong.rest;
 
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
 
@Path("/location")
public class MyLocationService {
	 public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	 public static final String MYSQL_URL = "jdbc:mysql://127.6.24.130:3306/javaTestDB?"
	                                            + "user=adminIiwjHWP&password=IgX6pwUruJv5";
	 // mama number 9767391626
	 
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{userId}")
	public Response getMsg(@PathParam("userId") String userId) {
		LocationDao dao = new LocationDao(MYSQL_DRIVER,MYSQL_URL);
		return Response.status(200).entity(dao.readData( userId)).build();
 
	}
	
	@POST 
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{userId}")
	public Response postMsg(@PathParam("userId") String userId, MyLocation myLocation ) {
		LocationDao dao = new LocationDao(MYSQL_DRIVER,MYSQL_URL);
		return Response.status(200).entity(dao.writeData(myLocation)).build();
	}
 
}