package bgu.spl181.net.srv.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class User implements Serializable {
	String username;
	String password;
	String type;
	String country;
	ArrayList<MovieInUser> movies;
	String  balance="0";
	public User(String UserId,String Pass,String type,String Country,  ArrayList<MovieInUser> movies,String Balance)
	{
		this.username=UserId;
		this.password=Pass;
		this.type=type;
		this.country=Country;
		this.balance=Balance;
		this.movies=movies;
	}
	public void setbalance(int newbalance)
  	{   
		this.balance=Integer.toString(newbalance);
	}
	public void addmovie(MovieInUser m)
	{
		
	}
	public String getcountry()
	{
		return this.country;
	}
	public String getUser()
	{
		return this.username;
	}
	public String getpass()
	{
		return this.password;
	}
	public String gettype()
	{
		return this.type;
	}
	public  ArrayList<MovieInUser> getmovies()
	{
		return this.movies;
	}
	public int getbalance()
	{
		return Integer.parseInt(balance);
	}

}
