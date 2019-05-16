package iperf;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Iperfer {
	
	private long bytesReceived = 0;
	private float rate;

	public static void main(String[] args) throws IOException, InterruptedException {

		// If there is no argument, it closes the program
		if( args.length ==  0){
			System.out.println("Error: missing or additional arguments");
			System.exit(-2);
		}

		String serverHost = "";
		int port = -1;
		int time = -1;



		if(args[0].equals("-s")){
			port = Integer.parseInt(args[2]);
			if(isPortValid(port)){

			}
		} else if (args[0].equals("-c")){
			serverHost = args[2];
			port = Integer.parseInt(args[4]);
			time = Integer.parseInt(args[6]);

			if(isPortValid(port)){
				if (isTimeValid(time)) {
					sendData(serverHost, port, time);
				}
			}	
		} else {
			System.out.println("ERROR: Incorrect use");
		}
	
		System.exit(0);		
	}

	//Method to check if the port is valid
	private static boolean isPortValid(int port){
		if (port >= 1024 && port < 65536) {
			return true;
		} else {
			return false;
		}
	}

	// Method to check if time for client is valid
	private static boolean isTimeValid(int time) {
		if (time >= 0) {
			return true;
		} else {
			return false;
		}
	}


	public static void sendData(String serverHost, int serverPort, int time) throws IOException, InterruptedException {

		try (
				Socket tcpSocket = new Socket(serverHost, serverPort);
				PrintWriter out =
						new PrintWriter(tcpSocket.getOutputStream(), true);
				) {
			long totalTime = (long) (time*Math.pow(10,9));
			long startTime = System.nanoTime();
			boolean toFinish = false;
			long totalNumberOfBytes = 0;
			while(!toFinish){
				byte[] dataChunk = new byte[1000];
				totalNumberOfBytes+=(long)1000;
				Arrays.fill(dataChunk, (byte)0);
				out.println(dataChunk);
				toFinish = (System.nanoTime() - startTime >= totalTime);
			}
			int sentInKB = (int) (totalNumberOfBytes/1024);
			double rate = ((totalNumberOfBytes/(long)Math.pow(2,20 )/time)*.1);
			System.out.print("sent= "+sentInKB+" KB rate= "+rate+" Mbps");
		}  catch (IOException e) {
			System.err.println("Cant connection to " +
					serverHost);
			System.exit(1);
		} 

	}


	public void listenClients(int port) throws IOException{
		
		ServerSocket serverSocket = new ServerSocket();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		Socket clientSocket = serverSocket.accept();

		long firstMoment = System.currentTimeMillis();		
		InputStream in = clientSocket.getInputStream();
		byte[] clientData = new byte[1024];


		// While the server keep receiving data,the loop continues

		while(in.read() != -1) {
			in.read(clientData);
			
			bytesReceived += clientData.length;
		}

		long lastMoment = System.currentTimeMillis();
		long totalTime = lastMoment - firstMoment;
		rate = calculateRate(bytesReceived, totalTime);
		printSummary();
	}
	
	private void printSummary() {
		System.out.println("received=" + bytesReceived/1000.0 + " KB rate=" + (8*rate)/1000.0 + " Mbps");
	}

	
	private float calculateRate(long bytesReceived, long totalTime) {

		float rate = (float) (bytesReceived/ totalTime);
		return rate;
	}
		



}
