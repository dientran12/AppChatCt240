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
    	String[] messageSplit = message.split("--");
    	System.out.println(messageSplit[1]);
    	System.out.println(message);
    	if (messageSplit[1].equals("have image")) {
    		for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
    			if (serverThread.getClientNumber() == id) {
    				continue;
    			} else {
    				serverThread.writeString(message);
    				try {
						serverThread.getAndSendImage();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
    		}
    	} else {
    		System.out.println("----------");
    		for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
    			int i = 0;
    			System.out.println(i++);
    			serverThread.writeString(message);
//    			if (serverThread.getClientNumber() == id) {
//    				continue;
//    			} else {
//    			}
    		}
    	}
    }
    
    public void sendOnlineList() {
        String res = "";
        System.out.println("da toi ham sendOnlineList");
        List<ServerThread> threadbus = Server.serverThreadBus.getListServerThreads();
        for (ServerThread serverThread : threadbus) {
            res += serverThread.getClientNumber() + "-";
        }
        Server.serverThreadBus.mutilCastSend("update-online-list" + "|" + res);
    }

    public void sendMessageToPersion(int id, String message) {
    	String[] messageSplit = message.split("--");
    	if (messageSplit[1].equals("have image")) {
    		for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                if (serverThread.getClientNumber() == id) {
                    serverThread.writeString(message);
                    try {
						serverThread.getAndSendImage();
					} catch (IOException e) {
						e.printStackTrace();
					}
    				break;
                }
            }
    	}else {
    		for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
    			if (serverThread.getClientNumber() == id) {
    				serverThread.writeString(message);
    				break;
    			}
    		}
    	}
    }
    
    public void add(ServerThread serverThread){
        listServerThreads.add(serverThread);
    }

    public void remove(int id) {
        for(int i = 0; i<Server.serverThreadBus.getLength(); i++) {
            if(Server.serverThreadBus.getListServerThreads().get(i).getClientNumber()== id) {
                Server.serverThreadBus.listServerThreads.remove(i);
            }
        }
    }
    public int getLength() {
        return listServerThreads.size();
    }

}
