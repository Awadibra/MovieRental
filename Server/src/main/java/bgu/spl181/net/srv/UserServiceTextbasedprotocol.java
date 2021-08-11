package bgu.spl181.net.srv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

public class UserServiceTextbasedprotocol implements BidiMessagingProtocol<String> {
	ConcurrentHashMap<String, Integer> loggedinusers;
	int connectionid = 0;
	Connections<String> connections;
	Boolean Shouldterminate = false;
	boolean logedin = false;
	String userName = "";
	SharedProtocolData shareddata;
	ConcurrentHashMap<String, String> registered;

	public UserServiceTextbasedprotocol(SharedProtocolData sharedata) {
		this.shareddata = sharedata;
		this.loggedinusers = this.shareddata.getlogedinusers();
		this.registered = this.shareddata.getregusers();

	}

	@Override
	public void start(int connectionId, Connections<String> connections) {
		this.connectionid = connectionId;
		this.connections = connections;

	}

	@Override
	public void process(String message) {
		if (message.substring(0, 6).equalsIgnoreCase("LOGIN")) {
		
			login(message.substring(6, message.length()));
		}
		if (message.equalsIgnoreCase("SIGNOUT")) {
			login(message.substring(6, message.length()));
		}

	}

	@Override
	public boolean shouldTerminate() {

		return this.Shouldterminate;
	}

	public void signout(String data) {
		if (this.logedin) {
			connections.send(connectionid, "ACK signout succeeded");
			this.logedin = false;
			loggedinusers.remove(userName);
		     this.connections.disconnect(connectionid);
			this.Shouldterminate=true;
		} else
			connections.send(connectionid, "ERROR signout failed");
	}

	public void BroadCast(String message) {
		for (Map.Entry<String, Integer> entry : loggedinusers.entrySet())
			this.connections.send(entry.getValue(), message);

	}

	public void login(String data) {
		int i = data.indexOf(' ');
		String username = data.substring(0, i);
		data = data.substring(i + 1, data.length());
		String pass = data.substring(i + 1);
		if (registered.containsKey(username) && registered.get(username).equals(pass)) {
			if (!loggedinusers.containsKey(username) & !this.logedin) {
				this.logedin = true;
				this.loggedinusers.put(username, connectionid);
				this.userName = username;
				connections.send(connectionid, "ACK login succeeded");
			}
		}

	}

	public void register(String data) {
		String username;
		String pass;
		username = data.substring(0, data.indexOf(' '));
		data = data.substring(data.indexOf(' ') + 1);
		pass = data.substring(0, data.length());
		if (pass.indexOf(' ') != -1) {
			if (username != "" && pass != "" && !this.registered.contains(username) && !this.logedin) {
				this.registered.put(username, pass);
				this.connections.send(connectionid, "ACK registration succeeded");
			}
			else
				this.connections.send(connectionid, "ERROR registration failed");
			
		}else this.connections.send(connectionid, "ERROR registration failed");

	}

}
