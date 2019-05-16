package ping;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	private static final int MAX_TIMEOUT = 1000;	// milliseconds

	public static void main(String[] args) throws Exception
	{
		// Get command line arguments.
		if (args.length != 7) {
			System.out.println("Invalid Input");
			return;
		}
		// Port number to access
		int port = Integer.parseInt(args[4]);
		// Server to Ping (has to have the PingServer running)
		InetAddress server;
		server = InetAddress.getByName(args[2]);
		
		int numOfPackets = Integer.parseInt(args[6]);

		// new Datagram
		DatagramSocket socket = new DatagramSocket(port);

		int sequence_number = 0;

		while (sequence_number < numOfPackets) {
		
			Date now = new Date();
			long msSend = now.getTime();
			
			String str = "PING " + sequence_number + " " + msSend + " \n";
			byte[] buf = new byte[1024];
			buf = str.getBytes();
	
			DatagramPacket ping = new DatagramPacket(buf, buf.length, server, port);

			// Send the Ping datagram to the specified server
			socket.send(ping);
			
			try {
				
				socket.setSoTimeout(MAX_TIMEOUT);
				
				DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
				
				socket.receive(response);
				
				
				now = new Date();
				long msReceived = now.getTime();
				// Print the packet and the delay
				printData(response, msReceived - msSend);
			} catch (IOException e) {
				// When Packet Times out
				System.out.println("Timeout for packet " + sequence_number);
			}
			// next packet
			sequence_number ++;
		}
	}

   private static void printData(DatagramPacket request, long delayTime) throws Exception
   {
      byte[] buf = request.getData();

      ByteArrayInputStream bais = new ByteArrayInputStream(buf);

      InputStreamReader isr = new InputStreamReader(bais);
 
      BufferedReader br = new BufferedReader(isr);

      String line = br.readLine();

      System.out.println(
         "Received from " + 
         request.getAddress().getHostAddress() + 
         ": " +
         new String(line) + " Delay: " + delayTime );
   }
	
}
