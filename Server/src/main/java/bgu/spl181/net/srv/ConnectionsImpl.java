package bgu.spl181.net.srv;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {
	ConcurrentHashMap<Integer, ConnectionHandler<T>> connections;// holds connid for each client and its conn handler.
	AtomicInteger connectionId = new AtomicInteger(0);

	public ConnectionsImpl() {
		connections = new ConcurrentHashMap<Integer, ConnectionHandler<T>>();

	}

	@Override
	public boolean send(int connectionId, T msg) {//sends msg t to client with the given connid number
		if(connections.get(connectionId)!=null){
		connections.get(connectionId).send(msg);
		return true;
		}
		return false;
	}

	@Override
	public void broadcast(T msg){ //sends msg to all connected  clients . 
	for(int i=0;i<connections.size();i++)
	{
		connections.get(i).send(msg);
	}

	}

	@Override
	public void disconnect(int connectionId) {
		this.connections.remove(connectionId);

	}

	public void add(ConnectionHandler<T> handler) {// for adding new connection and gives the handler connid for the client .
		connections.put(connectionId.get(), handler);
		handler.setConnectionId(connectionId.get());
		connectionId.incrementAndGet();
		
	}
	
}
