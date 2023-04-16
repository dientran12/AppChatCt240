package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import model.AppModel;

public class ServerThread implements Runnable {
	private int clientNumber;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private boolean isClosed;
	
	private byte[] imageData;
	private Thread thread;

	public int getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(int clientNumber) {
		this.clientNumber = clientNumber;

	}

	public ServerThread(Socket socket, int clientNumber) {
		this.socket = socket;
		this.setClientNumber(clientNumber);
		this.isClosed = false;
	}

	@Override
	public void run() {
		try {
			System.out.println("Client connected");

			// Receive message from client
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("new thread start up successfully, ID: " + clientNumber);
			writeString("get-id" + "|" + this.clientNumber + "|4"+ "|4" + "--0");
			Server.serverThreadBus.sendOnlineList();
			Server.serverThreadBus.mutilCastSend("entered" + "|" + "Client " + this.clientNumber + "|4" + "|"
					+ " entered the chat room--0");
			while (!isClosed) {
				System.out.println("Thread "+this.getClientNumber()+" tới hàm readLine");
				String message = reader.readLine();
				System.out.println("Message from client: " + message);
				receiveMessageFromClient(message);
			}
		} catch (IOException e) {
			isClosed = true;
			Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            Server.serverThreadBus.sendOnlineList();
            Server.serverThreadBus.mutilCastSend("entered" + "|" + "Client " + this.clientNumber+ "|4" + "|" + " left the chat room--0");}

	}
	
	public void receiveMessageFromClient(String message) {
		String[] messageSplit = message.split("\\|");
		String[] messageTimeState = message.split("\\-\\-");
		int lengthImage = Integer.parseInt(messageTimeState[1]);
		
		// Xử lý khi không có ảnh
		if (lengthImage==0) {
			if (messageSplit[0].equals("send-to-global")) {
				String str = "global-message" + "|" + "Client " + messageSplit[2] + ": " + "|" + messageSplit[1]
						+ "|" + messageSplit[3];
				Server.serverThreadBus.boardCast(this.getClientNumber(), str);
			}
		}
		
		// Xử lý có ảnh
				if (lengthImage !=0) {
					
					System.out.println("ServerThread: "+this.getClientNumber()+ " sau luon nhan anh");
					handleHaveImage(message, lengthImage);			
				}
		// sent message to client
//		if (messageSplit[0].equals("send-to-person")) {
//			Server.serverThreadBus.sendMessageToPersion(Integer.parseInt(messageSplit[3]),
//					"Client " + messageSplit[2] + " (tới bạn): " + "|" + messageSplit[1] + messageSplit[4]);
//		}
	}

	public void receiveImage(int lengthImage) {
		System.out.println("đã đến hàm getImageFromClient");
		try {
			byte[] imageBytes = new byte[lengthImage];
			dis.readFully(imageBytes);
			for (byte wr : imageBytes) {
				System.out.print(wr);
			}
			System.out.println();
			System.out.println("da nhận ảnh từ client");
			imageData = imageBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleHaveImage(String Message, int lengthImage) {
		String[] messageSplit = Message.split("\\|");
		if (messageSplit[0].equals("send-to-global")) {
			
			String str = "global-message" + "|" + "Client " + messageSplit[2] + ": " + "|" + messageSplit[1]
					+ "|" + messageSplit[3];
			System.out.println("Gui cho tat ca: " + str);
			Server.serverThreadBus.boardCast(this.getClientNumber(), str);
			receiveImage(lengthImage);
			Server.serverThreadBus.boardCast(this.getClientNumber(), this.imageData);
		}
		// sent message to client
//		if (messageSplit[0].equals("send-to-person")) {
//			Server.serverThreadBus.sendMessageToPersion(Integer.parseInt(messageSplit[3]),
//					"Client " + messageSplit[2] + " (tới bạn): " + "|" + messageSplit[1] + messageSplit[4]);
//		}
	}

	public void writeString(String str) {
		System.out.println("Thread "+ this.getClientNumber() +" gui chuoi: " + str);
		writer.println(str);
		writer.flush();
	}

	public void writeImage(byte[] imageBytes) {
		int imageLength = imageBytes.length;
		System.out.println("ServerThread: "+this.getClientNumber()+" xem do dai anh: "+imageLength);
		try {
			dos.write(imageBytes, 0, imageLength);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("đã gửi ảnh cho client");
	}

}
