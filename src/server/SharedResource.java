package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedResource {
	private Queue<DataPackage> dataQueue;
	private List<ServerThread> serverThreads;

	public List<ServerThread> getListServerThreads() {
		return serverThreads;
	}

	public SharedResource() {
		dataQueue = new LinkedList<>();
		this.serverThreads = new ArrayList<>();
	}

	public void addServerThread(ServerThread serverThread) {
		serverThreads.add(serverThread);
	}

	public void removeServerThread(ServerThread serverThread) {
		serverThreads.remove(serverThread);
	}

	public synchronized void addData(DataPackage data) {
		dataQueue.add(data);
		notifyAll();
	}

	public synchronized void mutilCastSend(String message) {
		for (ServerThread serverThread : Server.sharedResource.getListServerThreads()) {
			serverThread.sendString(message);
		}
	}

	public synchronized void broadcast(String clientName) {
		while (dataQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("da den poll da ta");
		DataPackage data = dataQueue.poll();
		for (ServerThread serverThread : serverThreads) {
			if (serverThread.getClientNumber() != clientName) {
				serverThread.sendData(data);
			}
		}
		System.out.println("hoanf");
	}
	
	public synchronized void sendToOnePerson(String clientName) {
		while (dataQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("da den poll sendtooneperson" + clientName);
		DataPackage data = dataQueue.poll();
		for (ServerThread serverThread : Server.sharedResource.getListServerThreads()) {
			System.out.println(serverThread.getClientNumber());
			if (serverThread.getClientNumber().equals(clientName)) {
				serverThread.sendData(data);
				break;
			}
		}
	}

	public void sendOnlineList() {
		String res = "";
		for (ServerThread serverThread : Server.sharedResource.getListServerThreads()) {
			res += serverThread.getClientNumber() + "+";
		}
		mutilCastSend("update-list|>" + res);
	}

	public boolean checkOnline(String response) {
		for (ServerThread serverThread : Server.sharedResource.getListServerThreads()) {
			if(serverThread.getClientNumber().equals(response)) {
				return true;
			}
		}
		return false;
	}
	

}
