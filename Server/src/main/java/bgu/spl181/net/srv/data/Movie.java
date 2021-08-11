package bgu.spl181.net.srv.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {
	String id;
	String name;
	String price = "0";
	ArrayList<String>  bannedCountries;
	String availableAmount = "0";
	String totalAmount = "0";

	public Movie(String Id, String Name, String Price, ArrayList<String>  BannedCountries,  String TotalAmount) {
		this.availableAmount = TotalAmount;
		this.bannedCountries = BannedCountries;
		this.id = Id;
		this.price = Price;
		this.totalAmount = TotalAmount;
		this.name = Name;
	}

	public void setavam(int availableAmount) {
		this.availableAmount = Integer.toString(availableAmount);
	}

	public int getavamount() {
		return Integer.parseInt(availableAmount);
	}

	public int getprice() {
		return Integer.parseInt(price);
	}
	public void setprice(int newprice) {
	this.price=Integer.toString(newprice);
	}

	public int gettotalAmount() {
		return Integer.parseInt(totalAmount);
	}

	public String getname() {

		return this.name;
	}

	public String getId() {
		return this.id;
	}

	public ArrayList<String> getbanned() {
		return this.bannedCountries;

	}
}
