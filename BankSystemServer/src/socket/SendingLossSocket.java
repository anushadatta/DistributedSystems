package socket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import main.Console;
import message.BytePacker;

// Simulates packet loss when packets are being sent out 
public class SendingLossSocket extends WrapperSocket {
	
	private final Random random;
	private final double probability;
	public SendingLossSocket(Socket socket, double probability) {
		super(socket);
		this.random = new Random();
		this.probability = probability;
	}
	
	// Higher probability - higher chance of sending successfully i.e. lower packet loss
	public void send(BytePacker msg, InetAddress address, int port) throws IOException {
		if(random.nextDouble()<probability){
			super.send(msg, address, port);
		}
		else{
			try{
				Thread.sleep(1000);
				Console.debug("Simulate Packet Loss while Sending");
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	

}
