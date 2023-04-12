package model;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimeImagePanel extends JPanel {
    private JLabel timeLabel;
    private JLabel imageLabel;
    private JLabel messageLabel;
    private JLabel nameClientLabel;

    public TimeImagePanel(String nameClient, String message, ImageIcon image, String time) {
        setLayout(new GridLayout(0,1));

        // Tạo và thiết lập label cho thời gian
        timeLabel = new JLabel(time);
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        add(timeLabel);

        // Tạo và thiết lập label cho ảnh
        imageLabel = new JLabel(image);
        add(imageLabel);
        
        // Tạo và thiết lập label cho lời nhắn
        messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        add(messageLabel);
        
        nameClientLabel = new JLabel(nameClient);
        nameClientLabel.setHorizontalAlignment(JLabel.CENTER);
        add(nameClientLabel);
    }
    
    public ImageIcon getImage() {
    	return (ImageIcon) imageLabel.getIcon();
    }
    
    public String getTime() {
    	return timeLabel.getText();
    }
    
    public String getMessage() {
    	return messageLabel.getText();
    }
    
    public String getNameClient() {
    	return nameClientLabel.getText();
    }
    
    

}