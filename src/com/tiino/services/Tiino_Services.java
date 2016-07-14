package com.tiino.services;

import java.security.Principal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/tiino_services")
public class Tiino_Services {

  // This method is called if TEXT_PLAIN is request
  @GET
  @Secured({Roles.User})
  @Path("/textHello")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextHello(@Context SecurityContext securityContext) {
	  Principal principal = securityContext.getUserPrincipal();
	    String username = principal.getName();
    return username;
  }
  @GET
  @Secured({Roles.Admin})
  @Path("/textHello")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextHelloAdmin(@Context SecurityContext securityContext) {
	  Principal principal = securityContext.getUserPrincipal();
	    String username = principal.getName();
	    return username;
  }
//  // This method is called if XML is request
//  @GET
//  @Secured
//  @Produces(MediaType.TEXT_XML)
//  public String sayXMLHello() {
//    return "<?xml version=\"1.0\"?>" + "<hello>Welcome to TIINO" + "</hello>";
//  }

//  // This method is called if HTML is request
//  @GET
//  @Secured
//  @Produces(MediaType.TEXT_HTML)
//  public String sayHtmlHello(@Context SecurityContext securityContext) {
//	  Principal principal = securityContext.getUserPrincipal();
//	    String username = principal.getName();
//  return username;
//  }

} 