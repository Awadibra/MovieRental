package bgu.spl181.net.srv.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import bgu.spl181.net.srv.SharedProtocolData;

/*data mangment object*/
public class Database {
	final String Diruser = "Database/Users.json";// diricotries for the data
	final String Dirmovie = "Database/Movies.json";
	Users users;
	movies movies;
	FileWriter writer;

	SharedProtocolData sharedata;
	ConcurrentHashMap<String, Integer> loggedinusers;

	public Database(SharedProtocolData sharedata) {
		users = new Users(new ArrayList<User>());
		movies = new movies(new ArrayList<Movie>());
		this.sharedata = sharedata;
		loggedinusers = new ConcurrentHashMap<String, Integer>();
	}

	public User finduserwithname(String s)//// to get the wanted user with its
											//// objects that holdss all the
											//// data.
	{
		ArrayList<User> userslist = users.getlist();
		for (int i = 0; i < userslist.size(); i++) {
			if (userslist.get(i).getUser().equals(s))
				return userslist.get(i);
		}
		return null;
	}

	public Movie findmoviewithname(String s)////// to get the wanted movie with
											////// its objects that holds all
											////// the data.
	{
		ArrayList<Movie> movielist = movies.getlist();
		for (int i = 0; i < movielist.size(); i++) {
			if (movielist.get(i).getname().equals(s))
				return movielist.get(i);
		}
		return null;
	}

	public String balanceinfo(String user)//// for the balance info command.
	{
		User u = this.finduserwithname(user);
		if (u != null)
			return "ACK balance " + u.getbalance();
		else
			return null;
	}

	public String addamount(int addedamount, String user)// for adding amout
															// command .
	{
		User u = this.finduserwithname(user);
		u.setbalance(u.getbalance() + addedamount);
		return "ACK balance " + u.getbalance() + " added " + addedamount;

	}

	public String movieinfo(String moviename)// for the info about specific or
												// all the movies.
	{
		ArrayList<Movie> movieslist = movies.getlist();
		String ans = "ACK info ";
		String ans2 = "ACK info ";
		int i = 0;
		if (moviename == "") {
			for (i = 0; i < movieslist.size() - 1; i++) {
				ans = ans + '"' + movieslist.get(i).getname() + '"' + " ";
			}
			ans = ans + '"' + movieslist.get(i).getname() + '"';
			return ans;
		} else {
			Movie m = this.findmoviewithname(moviename);
			if (m != null) {
				ans2 = ans2 + '"' + m.getname() + '"' + " " + m.getavamount() + " " + m.getprice() + " ";
				ArrayList<String> bannedcountries = m.getbanned();
				if (!bannedcountries.isEmpty()) {
					for (i = 0; i < bannedcountries.size() - 1; i++) {
						ans2 = ans2 + '"' + bannedcountries.get(i) + '"' + " ";
					}
					ans2 = ans2 + '"' + bannedcountries.get(i) + '"';
				}
				return ans2;
			} else
				return "ERROR request info failed";

		}
	}

	public boolean rentmovie(String username, String moviename)// for renting
																// movie for
																// user command.
	{
		Movie m = this.findmoviewithname(moviename);
		User u = this.finduserwithname(username);
		if (m == null | u == null)
			return false;
		if (u.getbalance() < m.getprice() | m.getavamount() == 0 | m.getbanned().contains(u.getcountry()))
			return false;
		ArrayList<MovieInUser> usermovies = u.getmovies();
		for (int i = 0; i < usermovies.size(); i++) {
			if (usermovies.get(i).getId().equals(m.getId()))
				return false;
		}
		u.setbalance(u.getbalance() - m.getprice());
		m.setavam(m.getavamount() - 1);
		usermovies.add(new MovieInUser(m.getId(), moviename));
		return true;

	}

	public boolean returnmovie(String moviename, String username)////// for
																	////// returning
																	////// the
																	////// movie
																	////// back
																	////// for
																	////// the
																	////// user
																	////// command

	{
		User u = this.finduserwithname(username);
		Movie m = this.findmoviewithname(moviename);
		boolean flag = false;
		if (m == null)
			return false;
		ArrayList<MovieInUser> usermovies = u.getmovies();
		for (int i = 0; i < usermovies.size() & !flag; i++) {
			if (usermovies.get(i).getName().equals(moviename)) {
				flag = true;
				usermovies.remove(i);
				m.setavam(m.getavamount() + 1);
			}
		}
		return flag;

	}

	public boolean addmovie(String username, String moviename, int price, int totalamount,
			ArrayList<String> bannedcountry)// adding new movie ////admin
											// command.
	{
		User u = this.finduserwithname(username);
		if (u == null || !u.gettype().equals("admin"))
			return false;
		if (price <= 0 | totalamount <= 0)
			return false;
		ArrayList<Movie> movieslist = movies.getlist();
		for (int i = 0; i < movieslist.size(); i++) {
			if (movieslist.get(i).getname().equals(moviename))
				return false;
		}

		movieslist.add(new Movie(Integer.toString(this.getnextindex()), moviename, Integer.toString(price),
				bannedcountry, Integer.toString(totalamount)));
		return true;

	}

	public boolean removie(String username, String moviename)///// removing
																///// movie
																///// ///admin
																///// command
	{
		User u = this.finduserwithname(username);
		Movie m = this.findmoviewithname(moviename);
		if (u == null || m == null || !u.gettype().equals("admin") || m.gettotalAmount() - m.getavamount() != 0)
			return false;
		movies.getlist().remove(m);
		return true;

	}

	public boolean changeprice(String username, String moviename, int price)//// changing
																			//// price
																			//// of
																			//// a
																			//// movie
																			//// ///admin
																			//// command.
	{
		User u = this.finduserwithname(username);
		Movie m = this.findmoviewithname(moviename);
		if (u == null || m == null || !u.gettype().equalsIgnoreCase("admin") || price <= 0)
			return false;
		m.setprice(price);
		return true;
	}

	public boolean adduser(String username, String pass, String country) {// adding
																			// users
																			// ..reg
																			// command.
		if (this.finduserwithname(username) == null) {
			users.getlist().add(new User(username, pass, "normal", country, new ArrayList<MovieInUser>(), "0"));
			return true;
		}
		return false;
	}

	/* getter for fields if wanted */
	public movies getmovies() {
		return this.movies;
	}

	public Users getusers() {
		return this.users;
	}

	public void clear() {
		users.getlist().clear();
		movies.getlist().clear();
	}

	public int getnextindex() {
		int ans = 1;
		for (int i = 0; i < movies.getlist().size(); i++)
			if (Integer.parseInt(movies.getlist().get(i).getId()) > ans)
				ans = Integer.parseInt(movies.getlist().get(i).getId());
		return ans + 1;
	}

	public void updateusers() {////// for updating the json file database each
		////// time data is changed in the server.
		try {
			writer = new FileWriter(Diruser);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(users, users.getClass());
			writer.write(s);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updatemovies() {////// for updating the json file database each
		////// time data is changed in the server.
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			writer = new FileWriter(Dirmovie);
			gson = new GsonBuilder().setPrettyPrinting().create();
			String s2 = gson.toJson(movies, movies.getClass());
			writer.write(s2);
			writer.flush();
		} catch (

		IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readfromusers() {

		JSONParser parser = new JSONParser();
		try {
			Reader reader = new InputStreamReader(new FileInputStream(Diruser));
			JSONObject jo = (JSONObject) parser.parse(reader);
			JSONArray jarr = (JSONArray) jo.get("users");
			JSONArray jarr2;
			JSONObject jo2;
			ArrayList<User> userslist = users.getlist();
			for (int i = 0; i < jarr.size(); i++) {
				jo = (JSONObject) jarr.get(i);
				String username = (String) jo.get("username");
				String type = (String) jo.get("type");
				String pass = (String) jo.get("password");
				String country = (String) jo.get("country");
				ArrayList<MovieInUser> movies = new ArrayList<MovieInUser>();
				jarr2 = (JSONArray) jo.get("movies");
				for (int j = 0; j < jarr2.size(); j++) {
					jo2 = (JSONObject) jarr2.get(j);
					String Id = (String) jo2.get("id");
					String name = (String) jo2.get("name");
					movies.add(new MovieInUser(Id, name));
				}
				String balance = (String) jo.get("balance");
				userslist.add(new User(username, pass, type, country, movies, balance));
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readfrommovies() {

		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(Dirmovie));
			JSONParser parser = new JSONParser();
			JSONObject jo;
			jo = (JSONObject) parser.parse(reader);

			JSONArray jarr = (JSONArray) jo.get("movies");
			ArrayList<Movie> movieslist = movies.getlist();

			for (int i = 0; i < jarr.size(); i++) {
				jo = (JSONObject) jarr.get(i);
				String id = (String) jo.get("id");
				String name = (String) jo.get("name");
				String price = (String) jo.get("price");
				String availableAmount = (String) jo.get("availableAmount");
				ArrayList<String> bannedCountries = new ArrayList<String>();
				JSONArray jarr2 = (JSONArray) jo.get("bannedCountries");
				for (int j = 0; j < jarr2.size(); j++) {
					bannedCountries.add((String) jarr2.get(j));

				}
				String totalAmount = (String) jo.get("totalAmount");
				movieslist.add(new Movie(id, name, price, bannedCountries, totalAmount));
				movieslist.get(i).setavam(Integer.parseInt(availableAmount));

			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
