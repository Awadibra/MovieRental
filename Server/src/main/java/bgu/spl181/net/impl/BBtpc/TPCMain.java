package bgu.spl181.net.impl.BBtpc;
import bgu.spl181.net.srv.MessageEncoderDecoderImpl;
import bgu.spl181.net.srv.MovieRentalServiceProtocol;
import bgu.spl181.net.srv.Server;
import bgu.spl181.net.srv.SharedProtocolData;

public class TPCMain {
	public static void main(String[] args) {
		
		SharedProtocolData sharedobject=new SharedProtocolData();
		
		
		Server<String> TPC= Server.threadPerClient(
				Integer.parseInt(args[0]),
				()-> new MovieRentalServiceProtocol(sharedobject) ,
				()->new MessageEncoderDecoderImpl()
				);
		TPC.serve();

	}
}
