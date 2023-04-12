package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import app.Client;

public class ActClient implements ActionListener {
	private Client viewClient;
	
	public ActClient(Client client) {
		this.viewClient = client;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String nameBtn = e.getActionCommand();
		
		if(nameBtn.equals("Choose a photo")) {
			try {
				this.viewClient.btnChooseFileEvent();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(nameBtn.equals("SEND")) {
			this.viewClient.btnSendEvent();
		}
		Object pathBtn = e.getSource();
		if(pathBtn == this.viewClient.btnCloseImage) {
			System.out.println("da nhan nut");
			this.viewClient.btnCloseImageEvent();
		}
	}

}
