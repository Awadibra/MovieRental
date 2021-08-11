package bgu.spl181.net.srv.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Users implements Serializable {
	ArrayList<User> users;
	public Users (ArrayList<User> users)
	{
		this.users=users;
	}
	public ArrayList<User> getlist()
	{
		return this.users;
	}

}
