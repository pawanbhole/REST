package com.mkyong.rest;
 
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
 
@Path("/hello")
public class HelloWorldService {
	 public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	 public static final String MYSQL_URL = "jdbc:mysql://127.6.24.130:3306/javaTestDB?"
	                                            + "user=adminIiwjHWP&password=IgX6pwUruJv5";
	 // mama number 9767391626
	 
	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {
 
		
		MySQLJava dao = new MySQLJava(MYSQL_DRIVER,MYSQL_URL);
        String output = "{\"msg22\": \"" +msg+"---" + dao.readData(msg)+"\"}";
		return Response.status(200).entity(output).build();
 
	}
	
	@POST
	@Path("/{param}")
	public Response postMsg(@PathParam("param") String msg) {
 
		String output = "Jersey say post : " + msg;
 
		return Response.status(200).entity(output).build();
 
	}
 
}