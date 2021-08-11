package bgu.spl181.net.srv.data;

import java.io.Serializable;
import java.util.ArrayList;

public class movies implements Serializable{
	ArrayList<Movie> movies;
	public movies(ArrayList<Movie> movies)
	{
		this.movies=movies;
	}
   public ArrayList<Movie> getlist()
   {
	   return this.movies;
   }
}
