package ping;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static final double LOSS_RATE = 0.3;
	   private static final int AVERAGE_DELAY = 100;  // milliseconds

	   public static void main(String[] args) throws Exception
	   {
		   System.out.println(args.length);
	      // Get command line argument.
	      if (args.length != 3) {
	         System.out.println("Required arguments: port");
	         return;
	      }
	      int port = Integer.parseInt(args[2]);

	 Random random = new Random();

	     
	      DatagramSocket socket = new DatagramSocket(port);

	      // Processing loop.
	      while (true) {
	        
	         DatagramPacket request = new DatagramPacket(new byte[1024], 1024);

	       
	         socket.receive(request);
	        
	         printData(request);
	         
	         if (random.nextDouble() < LOSS_RATE) {
	            System.out.println("   Reply not sent.");
	            continue;
	         }

	         // Simulate network delay.
	         Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));

	         // Send reply.
	         InetAddress clientHost = request.getAddress();
	         int clientPort = request.getPort();
	         byte[] buf = request.getData();
	         DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
	         socket.send(reply);

	         System.out.println("   Reply sent.");
	      }
	   }

	   /*
	    * Print ping data to the standard output stream.
	    */
	   private static void printData(DatagramPacket request) throws Exception
	   {
	      // Obtain references to the packet's array of bytes.
	      byte[] buf = request.getData();

	     
	      ByteArrayInputStream bais = new ByteArrayInputStream(buf);

	      InputStreamReader isr = new InputStreamReader(bais);

	      BufferedReader br = new BufferedReader(isr);

	    
	      String line = br.readLine();

	
	      System.out.println(
	         "Received from " +
	         request.getAddress().getHostAddress() +
	         ": " +
	         new String(line) );
	   }
}
}
