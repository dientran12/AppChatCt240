package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class Server {

	public static SharedResource sharedResource;
	private static String clientNumber;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		sharedResource = new SharedResource();
		Socket socket = null;
		clientNumber = "No Name";
		try {
			serverSocket = new ServerSocket(3333);
			System.out.println("Server started");
		} catch (IOException e) {
			System.out.println("Could not listen on port 3333");
			System.exit(-1);
		}

		// create thread pool executor
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(8));

		// start accepting client connections
		while (true) {
			try {
				socket = serverSocket.accept();

				ServerThread serverThread = new ServerThread(socket,clientNumber);

				executor.execute(serverThread);

			} catch (IOException e) {
				System.out.println("Accept failed on port 3333");
				System.exit(-1);
			}
		}
	}
}
