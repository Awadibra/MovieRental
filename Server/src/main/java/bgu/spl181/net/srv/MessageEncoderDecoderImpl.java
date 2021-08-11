package bgu.spl181.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl181.net.api.MessageEncoderDecoder;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<T> {
	private byte[] bytes = new byte[1 << 10]; 
    private int len = 0;
    
    public MessageEncoderDecoderImpl() {
		// TODO Auto-generated constructor stub
	}
    
	public T decodeNextByte(byte nextByte) {
		if (nextByte == '\n') {
            return (T) popString();
        }
 
        pushByte(nextByte);
        return null;
    }

	@Override
	public byte[] encode(T message) {
		 return (message + "\n").getBytes();
	}
	
	private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
 
        bytes[len++] = nextByte;
    }
	
	private String popString() {
        String result = new String(bytes, 0, len);
        len = 0;
        return result;
    }

}
