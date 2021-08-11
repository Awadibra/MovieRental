package bgu.spl181.net.srv;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedProtocolData {
	ConcurrentHashMap<String, Integer> loggedinusers;//all logged in users :usernames , connection id .
	ConcurrentHashMap<String,String> registered;//for utbp reg command .username,pass.
	ReentrantReadWriteLock userlock;//lock for users data.
	ReentrantReadWriteLock movielock;//lock for movies data.
	public SharedProtocolData()
	{
		loggedinusers=new ConcurrentHashMap<String,Integer>();
		registered=new ConcurrentHashMap<String,String>();
		userlock=new ReentrantReadWriteLock();
		movielock=new ReentrantReadWriteLock();
		
	}
	/*getters whatsoever*/
	public ConcurrentHashMap<String,Integer> getlogedinusers()
	{
		return this.loggedinusers;
	}
	public ConcurrentHashMap<String,String> getregusers()
	{
		return this.registered;
	}
	public ReentrantReadWriteLock getuserlock()
	{
		return this.userlock;
	}
	public ReentrantReadWriteLock getmovieslock()
	{
	return this.movielock;
	}


}
