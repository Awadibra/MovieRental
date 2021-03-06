package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.MessagingProtocol;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.srv.ConnectionsImpl;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

	private final BidiMessagingProtocol<T> protocol;
	private final MessageEncoderDecoder<T> encdec;
	private final Socket sock;
	private BufferedInputStream in;
	private BufferedOutputStream out;
	private volatile boolean connected = true;
	private int connid = 0;
	ConnectionsImpl<T> connections;

	public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol,
			ConnectionsImpl<T> connections) {
		this.sock = sock;
		this.encdec = reader;
		this.protocol = protocol;
		this.connections=connections;	

	}

	@Override
	public void run() {
		try (Socket sock = this.sock) { // just for automatic closing
			protocol.start(connid, connections);
			int read;

			in = new BufferedInputStream(sock.getInputStream());
			out = new BufferedOutputStream(sock.getOutputStream());

			while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
				T nextMessage = encdec.decodeNextByte((byte) read);
				if (nextMessage != null) {
					protocol.process(nextMessage);

				}
			
			}
			if(protocol.shouldTerminate())
				this.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		connected = false;
		sock.close();
	}

	@Override
	public void send(T msg) {
		if (msg != null) {
			try {
				out.write(encdec.encode(msg));
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void setConnectionId(int connectionId) {
		this.connid=connectionId;
		
		
	}
}
