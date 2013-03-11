package com.my.model;

public class Contactor {
	////eee
	private String name;
	private String phoneNr;
	private String phoneNrType;
	
//
	public Contactor(String name, String phoneNr, String phoneNrType){
		this.name = name;
		this.phoneNr = phoneNr;
		this.phoneNrType = phoneNrType;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNr() {
		return phoneNr;
	}
	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}
	public String getPhoneNrType() {
		return phoneNrType;
	}
	public void setPhoneNrType(String phoneNrType) {
		this.phoneNrType = phoneNrType;
	}

}
