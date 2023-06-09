package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;

public class AppModel {
	private String imagePath;
	private String imageName;
	private int id;
	private String time;
	private String messageContent;
	private byte[] imagebytes;
	private boolean isFileJPG;
	

	public AppModel() {
		this.imagePath = "";
		this.setImagebytes(null);
		this.time = "";
		this.messageContent = "";
		this.isFileJPG = true;
	}
	
	//
	
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public boolean isFileJPG() {
		return isFileJPG;
	}	
	public void setFileJPG(boolean isFileJPG) {
		this.isFileJPG = isFileJPG;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMessage() {
		return messageContent;
	}
	public void setMessageContent(String message) {
		this.messageContent = message;
	}
	
	public void checkFileJPG () {
		String filename = getImageName();
		if (filename.endsWith(".png")) {
			setFileJPG(false);
		}
	}
	
	public void updateTimeNow() {
		LocalTime now = LocalTime.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	    String formattedTime = now.format(formatter);
	    setTime(formattedTime);
	}
	
	public int getId() {
		return id;
		
	}
	public void setId(int id) {
		this.id = id;
	}

	public byte[] getImagebytes() {
		return imagebytes;
		
	}
	public void setImagebytes(byte[] imagebytes) {
		this.imagebytes = imagebytes;
		
	}
	
	public ImageIcon getImageIcon(byte[] imageBytes) {
		 ImageIcon icon = null;
		if (imageBytes != null) {
		    icon = new ImageIcon(imageBytes);
		} else {
			return null;
		}
		return icon;
	}
}
