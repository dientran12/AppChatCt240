package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerThreadBus {

	private List<ServerThread> listServerThreads;

	public List<ServerThread> getListServerThreads() {
		return listServerThreads;
	}

	public ServerThreadBus() {
		this.listServerThreads = new ArrayList<>();

	}

	public void mutilCastSend(String message) {
		for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
			serverThread.writeString(message);
		}
	}

	public void boardCast(int id, String message) {
		for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
			if (serverThread.getClientNumber() == id) {
				continue;
			} else {
				System.out.println(id + " Gửi tin nè");
				serverThread.writeString(message);
			}
		}

	}

	public void boardCast(int id, byte[] imageBytes) {
		for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
			if (serverThread.getClientNumber() == id) {
				continue;
			} else {
				System.out.println("serverThread "+serverThread.getClientNumber()+" truoclenh writeImage");
				serverThread.writeImage(imageBytes);
				System.out.println("serverThread "+serverThread.getClientNumber()+"Đã gửi ảnh cho client");
			}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void sendOnlineList() {
		String res = "";
		List<ServerThread> threadbus = Server.serverThreadBus.getListServerThreads();
		for (ServerThread serverThread : threadbus) {
			res += serverThread.getClientNumber() + "+";
		}
		Server.serverThreadBus.mutilCastSend("update-online-list" + "|" + res + "|4" + "|4" + "--0");
	}

	public void sendMessageToPersion(int id, String message) {
		String[] messageSplit = message.split("--");
		if (messageSplit[1].equals("have image")) {
			for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
				if (serverThread.getClientNumber() == id) {
					serverThread.writeString(message);
//					serverThread.writeImage();
					break;
				}
			}
		} else {
			for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
				if (serverThread.getClientNumber() == id) {
					serverThread.writeString(message);
					break;
				}
			}
		}
	}

	public void add(ServerThread serverThread) {
		listServerThreads.add(serverThread);
	}

	public void remove(int id) {
		for (int i = 0; i < Server.serverThreadBus.getLength(); i++) {
			if (Server.serverThreadBus.getListServerThreads().get(i).getClientNumber() == id) {
				Server.serverThreadBus.listServerThreads.remove(i);
			}
		}
	}

	public int getLength() {
		return listServerThreads.size();
	}

}
