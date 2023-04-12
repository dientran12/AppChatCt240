package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {
	private int clientNumber;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private boolean isClosed;
	
	public BufferedReader getReader() {
        return reader;
    }

    public PrintWriter getWriter() {
        return writer;
    }

	public int getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(int clientNumber) {
		this.clientNumber = clientNumber;

	}

	public ServerThread(Socket socket, int clientName) {
		this.socket = socket;
		this.setClientNumber(clientName);
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
			writeString("get-id" + "|" + this.clientNumber);
			Server.serverThreadBus.sendOnlineList();
			Server.serverThreadBus.mutilCastSend(
					"globel-message" + "|" + "---Client " + this.clientNumber +"|"+"|"+ " entered the chat room--no image");
			String message;
			while (!isClosed) {
				message = reader.readLine();
				if (message.isEmpty()) {
					break;
				}
				System.out.println("Message from client: " + message);

				String[] messageSplit = message.split("\\|");

				System.out.println(messageSplit[0]);
				// sent message to client
				if (messageSplit[0].equals("send-to-global")) {
					System.out.println("chuan bi phan hoi");
					String str = "global-message" + "|" + "Client " + messageSplit[2] + ": " + "|"+ messageSplit[1] +"|" + messageSplit[3];
					Server.serverThreadBus.boardCast(this.getClientNumber(), str);
				}
				if (messageSplit[0].equals("send-to-person")) {
					Server.serverThreadBus.sendMessageToPersion(Integer.parseInt(messageSplit[3]), "Client " + messageSplit[2] + " (tới bạn): "+"|" + messageSplit[1] +  messageSplit[4]);
				}
			}
		} catch (IOException e) {
			isClosed = true;
//			e.printStackTrace();
		}

	}
	
	public void getAndSendImage() throws IOException {
		int imageLength = dis.readInt();
		System.out.println(imageLength);
		byte[] imageBytes = new byte[imageLength];
		dis.readFully(imageBytes);

		dos.writeInt(imageLength);
		dos.write(imageBytes);
		dos.flush();
	}

	public void writeString(String str) {
		System.out.println("gui chuoi: " +str);
		writer.println(str);
		writer.flush();
	}

	public void writeImage(byte[] imageBytes) throws IOException {
		int imageLength = imageBytes.length;
		dos.writeInt(imageLength);
		dos.write(imageBytes);
		dos.flush();
	}

}
