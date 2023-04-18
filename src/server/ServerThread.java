package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerThread implements Runnable {
	private String clientNumber;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean isClosed;
	private boolean isLoggedIn;
	

	public ServerThread(Socket socket, String clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.isLoggedIn = false;
		this.isClosed = false;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;

	}

	@Override
	public void run() {
		try {
			
			while (!isClosed) {
				handleClientRequest();
			}
			dos.close();
			dis.close();
			socket.close();
		} catch (IOException e) {
			isClosed = true;
			if(isLoggedIn) {
				Server.sharedResource.removeServerThread(this);
				System.out.println(this.clientNumber + " đã thoát");
				Server.sharedResource.mutilCastSend("login notice|>Client " + clientNumber + ": exited the room");
				Server.sharedResource.sendOnlineList();
			}
		}
	}

	private void handleClientRequest() throws IOException {
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		// Đọc dữ liệu từ client
		System.out.println("Dang đợi ở hàm readline");
		String request = dis.readUTF();
		System.out.println(request);

		String[] rqSplit = request.split("\\|>");
		if(isLoggedIn ==false && rqSplit[0].equals("login request")) {
			String[] rqLogin =rqSplit[1].split("\\-");
			String username = rqLogin[0].trim();
			String password = rqLogin[1].trim();
			
			// xu ly dang nhap
			String response = getData(username, password);
			System.out.println(response);
			sendString(response);
			if(response.equals("success")) {
				isLoggedIn = true;
				this.clientNumber = username;
				Server.sharedResource.addServerThread(this);
				// Receive message from client
				System.out.println("new thread start up successfully, ID: " + clientNumber);
//				sendString("get-id|>" + clientNumber);
				Server.sharedResource.sendOnlineList();
				Server.sharedResource.mutilCastSend("login notice|>Client " + clientNumber + ": entered the room");
			}
		} else {
			String receiver = rqSplit[0];
			
			byte[] imageData = readImage(dis);
			System.out.println("da qua duoc readIamge");
			// Đóng gói dữ liệu tin nhắn và ảnh vào DataPackage
			DataPackage data = new DataPackage("message|>" + request, imageData);
			
			// Lưu DataPackage vào SharedResource
			Server.sharedResource.addData(data);
			System.out.println("da qua duoc luu data");
			if (receiver.equals("Send to all")) {
				Server.sharedResource.broadcast(this.clientNumber);
				System.out.println("hoanf thanh boradcast");
			}
			else {
				System.out.println("Vao send to ono");
				Server.sharedResource.sendToOnePerson(receiver);
			}
		}
		System.out.println("Hoang thanh lap o server");
	}

	private byte[] readImage(DataInputStream dis) throws IOException {
		System.out.println("đang chờ ở readInt");
		int imageSize = dis.readInt();
		System.out.println(imageSize);
		if (imageSize <= 0) {
			System.out.println("svThread" + this.clientNumber + ": bỏ qua việc đọc dữ liệu ảnh");
			return null;
		}
		byte[] imageData = new byte[imageSize];
		System.out.println("Dang doi o readFully");
		dis.readFully(imageData);
		return imageData;
	}

	public void sendData(DataPackage data) {
		try {

			// Gửi tin nhắn
			dos.writeUTF(data.getMessage());

			// Gửi ảnh
			if (data.getImageData() != null) {
				dos.writeInt(data.getImageData().length);
				dos.write(data.getImageData());
			} else {
				dos.writeInt(-1);
			}
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendString(String message) {
		try {
			synchronized (dos) {
				System.out.println("Thread " + clientNumber + " gui chuoi" + message);
				dos.writeUTF(message);
				dos.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String addData(String username, String password) {
		String receivePass = DAO.getData(username);
		if(receivePass!="") {
			return "fail";
		}
		DAO.addData(username,password);
		return "success";
	}

	public static String getData(String username, String password) {
		String receivePass = DAO.getData(username);
		if(password.equals(receivePass)) {
			return "success";
		}
		return "fail";
	}


}
