package com.tiino.services;

import com.google.gson.Gson;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSON;
import com.tiino.models.*;

import javax.ws.rs.*;
import java.security.SecureRandom;
import java.util.Date;
import java.math.BigInteger;
import javax.ws.rs.core.*;

import org.bson.Document;

@Path("/authentication")
public class AuthenticationEndpoint {
	private SecureRandom random = new SecureRandom();
	private static String connectionString = "mongodb://tiino_user:ReadWrite%40123@localhost:27017/?authSource=admin";
	MongoClientURI uri = new MongoClientURI(connectionString);

	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
System.out.println("test");
		try {

			// Authenticate the user using the credentials provided
			authenticate(username, password);

			// Issue a token for the user
			String token = issueToken(username);

			// Return the token on the response
			return Response.ok(token).build();

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private void authenticate(String username, String password) throws Exception {
		// Authenticate against a database
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase db = mongoClient.getDatabase("tiino_db");
		Document queryDoc = new Document("username", "bala");
		FindIterable<Document> iterable = db.getCollection("users").find(queryDoc);
				String userJson = null;
		for (Document document : iterable) {
			userJson = document.toJson().toString();
		}
		mongoClient.close();

		// Throw an Exception if the credentials are invalid
		if (userJson != null) {
			Gson gson = new Gson();
			User user = gson.fromJson(userJson, User.class);

			if (!(username.equals(user.getUserName()) && password.equals(user.getPassword())))
				throw new Exception();
		} else {
			throw new Exception();
		}
	}

	protected String issueToken(String username) {
		// Issue a token (can be a random String persisted to a database or a
		// JWT token)
		String token = new BigInteger(130, random).toString(32);
		// The issued token must be associated to a user
		UserToken userToken = new UserToken();
		userToken.setToken(token);
		userToken.setUsername(username);
		userToken.setExpiryTime(System.currentTimeMillis() + (30 * 60 * 1000));

		Gson gson = new Gson();

		String userJson = gson.toJson(userToken);

		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase db = mongoClient.getDatabase("tiino_db");
		Document dbObject = Document.parse(userJson);

		MongoCollection<Document> collection = db.getCollection("userToken");
		collection.updateOne(new Document("username", username), new Document("$set", dbObject),new UpdateOptions().upsert(true));
		mongoClient.close();
		// Return the issued token
		return token;
	}
}
