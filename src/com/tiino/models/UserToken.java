package com.tiino.models;

import java.util.Date;

public class UserToken {
private String token;
private String username;
private long expiryTime;
public String getToken() {
	return token;
}
public void setToken(String token) {
	this.token = token;
}
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public long getExpiryTime() {
	return expiryTime;
}
public void setExpiryTime(long expiryTime) {
	this.expiryTime = expiryTime;
}

}
