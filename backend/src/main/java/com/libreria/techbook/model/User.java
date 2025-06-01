package com.libreria.techbook.model;

public class User {
	
	 private int userId;
	 private String username;
	 private String email;
	 private String password;
	 private String nomeLibreria;
     private int punteggio;
     
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userid) {
		this.userId = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNomeLibreria() {
		return nomeLibreria;
	}
	public void setNomeLibreria(String nomeLibreria) {
		this.nomeLibreria = nomeLibreria;
	}
	public int getPunteggio() {
		return punteggio;
	}
	public void setPunteggio(int punteggio) {
		this.punteggio = punteggio;
	}
            
     
}
