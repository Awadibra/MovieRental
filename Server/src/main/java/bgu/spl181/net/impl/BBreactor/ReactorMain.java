package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.srv.MessageEncoderDecoderImpl;
import bgu.spl181.net.srv.MovieRentalServiceProtocol;
import bgu.spl181.net.srv.Server;
import bgu.spl181.net.srv.SharedProtocolData;

public class ReactorMain {

	public static void main(String[] args) {
       SharedProtocolData sharedobject=new SharedProtocolData();
		
		
		Server<String> Reactor= Server.reactor(5,
				Integer.parseInt(args[0]),
				()-> new MovieRentalServiceProtocol(sharedobject) ,
				()->new MessageEncoderDecoderImpl()
				);
		Reactor.serve();
	}

}
