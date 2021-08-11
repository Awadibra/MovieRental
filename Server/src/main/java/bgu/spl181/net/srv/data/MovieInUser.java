package bgu.spl181.net.srv.data;

import java.io.Serializable;

public class MovieInUser implements Serializable {
	String id;
	String name;
	public MovieInUser(String Id,String Name)
	{
		this.id=Id;
		this.name=Name;
	}
	public String getId()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
}
