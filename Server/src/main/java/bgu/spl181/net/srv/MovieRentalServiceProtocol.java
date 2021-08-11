package bgu.spl181.net.srv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.data.Database;
import bgu.spl181.net.srv.data.User;

public class MovieRentalServiceProtocol extends UserServiceTextbasedprotocol {
	Database db;//data mangment .
	ConcurrentHashMap<String, Integer> loggedinusers;//shared info .

	public MovieRentalServiceProtocol(SharedProtocolData sharedata) {
		super(sharedata);
		db = new Database(sharedata);
		this.loggedinusers = this.shareddata.getlogedinusers();
	}

	@Override
	public void process(String message) {
		String[] messagesplited = message.split(" ");
		//login
		if (messagesplited[0].equalsIgnoreCase("login")) {
			this.shareddata.getuserlock().writeLock().lock();
			db.clear();
			db.readfromusers();
			login(message.substring(6));
			this.shareddata.getuserlock().writeLock().unlock();
		}
		///signout.
		if (messagesplited[0].equalsIgnoreCase("SIGNOUT")) {
			this.shareddata.getuserlock().writeLock().lock();
			db.clear();
			db.readfromusers();
			signout(message.substring(7));
			db.updateusers();
			this.shareddata.getuserlock().writeLock().unlock();
		}//reg.
		if (messagesplited[0].equalsIgnoreCase("REGISTER")) {
			this.shareddata.getuserlock().writeLock().lock();
			db.clear();
			db.readfromusers();
			register(message.substring(9));
			db.updateusers();
			this.shareddata.getuserlock().writeLock().unlock();
		}//balanceinfo.
		if (message.equalsIgnoreCase("REQUEST balance info")) {
			this.shareddata.getuserlock().readLock().lock();
			db.clear();
			db.readfromusers();
			this.reqbalanceinf();
			this.shareddata.getuserlock().readLock().unlock();
		}//add balance.
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("balance")
				&& messagesplited[2].equalsIgnoreCase("add")) {
			this.shareddata.getuserlock().writeLock().lock();
			db.clear();
			db.readfromusers();
                        if(messagesplited.length>3)
			this.addbalance(messagesplited[3]);
			db.updateusers();
			this.shareddata.getuserlock().writeLock().unlock();
		} // movie info.
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("info")) {
			this.shareddata.getmovieslock().readLock().lock();
			this.shareddata.getuserlock().readLock().lock();
			db.clear();
			db.readfromusers();
			db.readfrommovies();
			if (messagesplited.length > 2) {
				this.reqinfo(message.substring(message.indexOf('"') + 1, message.length() - 1));

			} else {
				this.reqinfo("");

			}
			this.shareddata.getuserlock().readLock().unlock();
			this.shareddata.getmovieslock().readLock().unlock();

		} // rent movie.
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("rent")) {
			this.shareddata.getmovieslock().writeLock().lock();
			this.shareddata.getuserlock().writeLock().lock();
			db.clear();
			db.readfrommovies();
			db.readfromusers();
			this.rentmovi(message.substring(13));
			db.updateusers();
			db.updatemovies();
			this.shareddata.getuserlock().writeLock().unlock();
			this.shareddata.getmovieslock().writeLock().unlock();
		} // return movei
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("return")) {
			this.shareddata.getmovieslock().writeLock().lock();
			this.shareddata.getuserlock().writeLock().lock();
			db.clear();
			db.readfrommovies();
			db.readfromusers();
			this.returnmovie(message.substring(message.indexOf('"') + 1, message.length() - 1));
			db.updateusers();
			db.updatemovies();
			this.shareddata.getuserlock().writeLock().unlock();
			this.shareddata.getmovieslock().writeLock().unlock();

		} // addmovie
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("addmovie")) {
			this.shareddata.getmovieslock().writeLock().lock();
			this.shareddata.getuserlock().readLock().lock();
			db.clear();
			db.readfrommovies();
			db.readfromusers();
			message = message.substring(17);
			this.addmovie(message);
			db.updatemovies();
			this.shareddata.getuserlock().readLock().unlock();
			this.shareddata.getmovieslock().writeLock().unlock();

		} // remmovie
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("remmovie")) {
			this.shareddata.getmovieslock().writeLock().lock();
			this.shareddata.getuserlock().readLock().lock();
			db.clear();
			db.readfrommovies();
			db.readfromusers();
			message = message.substring(17);
			this.removie(message);
			db.updatemovies();
			this.shareddata.getuserlock().readLock().unlock();
			this.shareddata.getmovieslock().writeLock().unlock();

		} // changeprice.
		if (messagesplited[0].equalsIgnoreCase("REQUEST") && messagesplited[1].equalsIgnoreCase("changeprice")) {
			this.shareddata.getmovieslock().writeLock().lock();
			this.shareddata.getuserlock().readLock().lock();
			db.clear();
			db.readfrommovies();
			db.readfromusers();
			message = message.substring(20);
			this.chnagemovieprice(message);
			db.updatemovies();
			this.shareddata.getuserlock().readLock().unlock();
			this.shareddata.getmovieslock().writeLock().unlock();
		}

	}

	@Override
	public void register(String data) {//reg.
		String username = "";
		String pass = "";
		String country = "";
		boolean flag = false;
		String[] message = data.split(" ");
		if (message.length > 2) {
			username = message[0];
			pass = message[1];
			country = message[2].substring(message[2].indexOf('"') + 1, message[2].length() - 1);

			if (!(username.equals("") | pass.equals("") | country.equals("")))
				if (!country.matches(".*\\d.*")) {
					flag = db.adduser(username, pass, country);

				}

			if (flag) {
				this.connections.send(connectionid, "ACK registration succeeded");
			} else
				this.connections.send(connectionid, "ERROR registration failed");
		}else
			this.connections.send(connectionid, "ERROR registration failed");
	}

	@Override
	public void login(String data) {//login
		String username = "";
		String pass = "";
		String[] dataarray = data.split(" ");
		username = dataarray[0];
		pass = dataarray[1];
		User u = db.finduserwithname(username);
		if (u != null && u.getpass().equals(pass) && !loggedinusers.containsKey(username) && !logedin) {
			loggedinusers.put(username, connectionid);
			logedin = true;
			this.userName = username;
			connections.send(connectionid, "ACK login succeeded");
		} else
			connections.send(connectionid, "ERROR login failed");
	}

	public void reqinfo(String message) {//movie info.
		if (this.logedin)
			this.connections.send(connectionid, db.movieinfo(message));
		else
			this.connections.send(connectionid, "ERROR request info failed");
	}

	public void reqbalanceinf() {//balanceinfo.
		if (this.logedin) {
			this.connections.send(connectionid, db.balanceinfo(userName));

		} else
			this.connections.send(connectionid, "ERROR request balance failed");
	}

	public void addbalance(String message) {//balance to add.
		if (this.logedin) {
			int amount = Integer.parseInt(message);
			this.connections.send(connectionid, db.addamount(amount, this.userName));

		} else
			this.connections.send(connectionid, "ERROR request balance add failed");
	}

	public void rentmovi(String message) {//rent moveis.
		String regex = Character.toString('"');
		String[] messagesplt = message.split(regex);
		message = messagesplt[1];
		if (this.logedin) {
			if (db.rentmovie(userName, message)) {
				String ans = "ACK rent " + '"' + message + '"' + " success";
				this.connections.send(connectionid, (ans));
				this.BroadCast("BROADCAST movie " + '"' + message + '"' + " "
						+ db.findmoviewithname(message).getavamount() + " " + db.findmoviewithname(message).getprice());
			} else
				this.connections.send(connectionid, "ERROR request rent failed");

		} else
			this.connections.send(connectionid, "ERROR request rent failed");
	}

	public void returnmovie(String message) {//return moveis.
		if (this.logedin && db.returnmovie(message, userName)) {
			this.connections.send(connectionid, "ACK return " + '"' + message + '"' + " success");
			this.BroadCast("BROADCAST movie " + '"' + message + '"' + " " + db.findmoviewithname(message).getavamount()
					+ " " + db.findmoviewithname(message).getprice());
		} else
			this.connections.send(connectionid, "ERROR request return failed");

	}
     /*admin commands*/
	public void addmovie(String message) {
		String moviename = "";
		String regex = Character.toString('"');
		String[] messagesplt = message.split(regex);
		moviename = messagesplt[1];
		String amount = messagesplt[2].substring(1);
		amount = amount.substring(0, amount.indexOf(' '));
		String price = messagesplt[2].substring(1);

		if (messagesplt.length > 3) {
			price = price.substring(price.indexOf(' ') + 1, price.length() - 1);
			ArrayList<String> countries = new ArrayList<String>();
			for (int i = 3; i < messagesplt.length; i = i + 2) {
				countries.add(messagesplt[i]);
			}

			if (this.logedin
					&& db.addmovie(userName, moviename, Integer.parseInt(price), Integer.parseInt(amount), countries)) {
				this.connections.send(connectionid, "ACK addmovie " + '"' + moviename + '"' + " success");
				this.BroadCast("BROADCAST movie " + '"' + moviename + '"' + " " + Integer.parseInt(amount) + " "
						+ Integer.parseInt(price));
			} else
				this.connections.send(connectionid, "ERROR request addmovie failed");

		} else {
			price = price.substring(price.indexOf(' ') + 1);
			if (this.logedin && db.addmovie(userName, moviename, Integer.parseInt(price), Integer.parseInt(amount),
					new ArrayList<String>())) {
				this.connections.send(connectionid, "ACK addmovie " + '"' + moviename + '"' + " success");
				this.BroadCast("BROADCAST movie " + '"' + moviename + '"' + " " + Integer.parseInt(amount) + " "
						+ Integer.parseInt(price));
			} else
				this.connections.send(connectionid, "ERROR request addmovie failed");

		}

	}

	public void removie(String message) {
		String movie = message.substring(1, message.length() - 1);
		if (this.logedin && db.removie(userName, movie)) {
			this.connections.send(connectionid, "ACK remmovie " + '"' + movie + '"' + " success");
			this.BroadCast("BROADCAST movie " + '"' + movie + '"' + " removed");
		} else
			this.connections.send(connectionid, " ERROR request remmovie failed");

	}

	public void chnagemovieprice(String message) {
		String regex = Character.toString('"');
		String[] messagesplt = message.split(regex);
		String movie = messagesplt[1];
		String price = messagesplt[2].substring(1);
		if (this.logedin && db.changeprice(this.userName, movie, Integer.parseInt(price))) {
			this.connections.send(connectionid, "ACK changeprice " + '"' + movie + '"' + " success");
			this.BroadCast("BROADCAST movie " + '"' + movie + '"' + " " + db.findmoviewithname(movie).getavamount()
					+ " " + db.findmoviewithname(movie).getprice());
		} else
			this.connections.send(connectionid, "ERROR request changeprice failed");

	}

}
