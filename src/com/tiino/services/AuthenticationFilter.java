package com.tiino.services;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.*;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.tiino.models.User;
import com.tiino.models.*;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter,ContainerResponseFilter  {

	private static String connectionString = "mongodb://tiino_user:ReadWrite%40123@localhost:27017/?authSource=admin";
	MongoClientURI uri = new MongoClientURI(connectionString);
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the HTTP Authorization header from the request
		String authorizationHeader = 
				requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted correctly 
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {

			// Validate the token
			final String username = validateToken(token);
			requestContext.setSecurityContext(new SecurityContext() {

				@Override
				public Principal getUserPrincipal() {

					return new Principal() {

						@Override
						public String getName() {
							return username;
						}
					};
				}

				@Override
				public boolean isUserInRole(String role) {
					return true;
				}

				@Override
				public boolean isSecure() {
					return false;
				}

				@Override
				public String getAuthenticationScheme() {
					return null;
				}
			});

		} catch (Exception e) {
			requestContext.abortWith(
					Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	private String validateToken(String token) throws Exception {
		// Check if it was issued by the server and if it's not expired
		String username = null;
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase db = mongoClient.getDatabase("tiino_db");
		Document queryDoc = new Document("token", token);
		FindIterable<Document> iterable = db.getCollection("userToken").find(queryDoc);
		String userJson = null;
		for (Document document : iterable) {
			userJson = document.toJson().toString();
		}
		mongoClient.close();

		// Throw an Exception if the Token is invalid
		if (userJson != null) {
			Gson gson = new Gson();
			UserToken user = gson.fromJson(userJson, UserToken.class);
			username = user.getUsername();
			if(user.getExpiryTime()<System.currentTimeMillis()){
				AuthenticationEndpoint authentication = new AuthenticationEndpoint();
				Response.ok().header("token",authentication.issueToken(username) );
			}
		} else {
			throw new Exception();
		}

		return username;
		
	}

	@Override
	public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
		// TODO Auto-generated method stub

	}
}