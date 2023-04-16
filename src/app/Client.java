package app;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.ActClient;
import model.AppModel;
import model.TimeImagePanel;
import model.TimeImageRenderer;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.Font;

public class Client extends JFrame {

	private AppModel appModel;

	private BufferedReader reader;
	private DataOutputStream dos;
	private PrintWriter writer;
	private DataInputStream dis;

	private JPanel contentPane;
	private JTextField textFieldInput;

	private Thread thread;
	private Socket socket;
	private List<String> onlineList;
	private JButton btnSend;
	private JComboBox<String> comboBox;
	private JLabel imageSendLabel;
	private JButton btnChooseImage;
	private JList<TimeImagePanel> list;
	private DefaultListModel<TimeImagePanel> model = new DefaultListModel<>();
	private JTextArea onlineListArea;

	public JButton btnCloseImage;

	public static void main(String[] args) {
		Client frame = new Client();
	}

	private void setUpSocket() {
		thread = new Thread() {
			@Override
			public void run() {
				try {
					appModel = new AppModel();
					socket = new Socket("localhost", 12345);
					System.out.println("successful connection :3");

					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream(), true);
					dos = new DataOutputStream(socket.getOutputStream());
					dis = new DataInputStream(socket.getInputStream());
					// Receive response from server
					receiveMessageFromServer();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		};
		thread.run();
	}

	private void writeString(String message) {
		System.out.println("Client send to server: " + message);
		writer.println(message);
		writer.flush();
	}

	private void writeImage() throws IOException {
		byte[] imageClientBytes = appModel.getImagebytes();
		dos.write(imageClientBytes,0, appModel.getLength());
		dos.flush();
		System.out.println("Đã gửi ảnh cho server");
	}
	
	private void receiveMessageFromServer() {
		try {
			while (true) {
				System.out.println(appModel.getId()+" Dang doi o readLien");
				String message = reader.readLine();
				 if (message == null) {
                     break;
                 }
				System.out.println("Message from server: " + message);

				String[] rp = message.split("\\-\\-");
				String[] messageSplit = rp[0].split("\\|");
				String stateMessage = messageSplit[0];
				String stateImage = rp[1];
				int lengthFromServer = Integer.parseInt(stateImage);
				String messageContent = messageSplit[2];
				String name = messageSplit[1];
				String time = messageSplit[3];

				if (stateMessage.equals("get-id")) {
					setID(Integer.parseInt(name));
					setIDTitle();
					continue;
				}
				if (stateMessage.equals("update-online-list")) {
					onlineList = new ArrayList<>();
					String online = "";
					String[] onlineSplit = name.split("\\+");
					for (int i = 0; i < onlineSplit.length; i++) {
						onlineList.add(onlineSplit[i]);
						online += "Client " + onlineSplit[i] + " đang online \n";
					}
					System.out.println(online);
					onlineListArea.setText(online);
					updateCombobox();
					continue;
				}
				if (stateMessage.equals("entered")) {
					updateUImessage(name + time, "", "");
					continue;
				}
				if (stateMessage.equals("global-message")) {
					if (!stateImage.equals("0")) {
						System.out.println(appModel.getId() + " Dang doi o readFully");

						System.out.println(lengthFromServer);
						byte[] imageBytes = new byte[lengthFromServer];
						dis.readFully(imageBytes);
						ImageIcon newIcon = appModel.getImageIcon(imageBytes);
						updateUImessage(name, messageContent, newIcon, time);
					}
					if (stateImage.equals("0")) {
						updateUImessage(name, messageContent, time);
					}
					continue;
				}
				System.out.println("cái lozz gì thế: "+message);
				System.out.println("cái lozz gì thế: "+message);
				
			}
		} catch (Exception e) {

		}
	}

	public void btnChooseFileEvent() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			updateChangeFile(path, selectedFile.getName());
			btnCloseImage.setVisible(true);
		}
	}

	public void btnSendEvent() {
		String str = textFieldInput.getText();
		appModel.setMessageContent(str);
		appModel.updateTimeNow();
		String time = appModel.getTime();
		System.out.println("đã nhấn nut send");
		if (comboBox.getSelectedIndex() == 0) {
			int lent = appModel.getLength();
			if (lent !=0) {
				writeString("send-to-global" + "|" + str + "|" + appModel.getId() + "|" + time + "--" + appModel.getLength());
				updateUImessage("You: ", str, appModel.getIcon(), time);
				try {
					writeImage();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				writeString("send-to-global" + "|" + str + "|" + appModel.getId() + "|" + time + "--" + appModel.getLength());
				updateUImessage("You: ", str, time);
			}
		}
//		else {
//			try {
//				String[] parner = ((String) comboBox.getSelectedItem()).split(" ");
//				if (appModel.getImagePath() != "") {
//					writeString("send-to-person" + "|" + str + "|" + this.id + "|" + parner[1] + "|"
//							+ appModel.getTime() + "--" + appModel.getLength());
//					writeImage();
//				} else {
//					writeString("send-to-person" + "|" + str + "|" + this.id + "|" + parner[1] + "|"
//							+ appModel.getTime() + "--" + appModel.getLength());
//				}
//				updateUImessage("You send to " + parner[1], str, time);
//			} catch (IOException ex) {
//				JOptionPane.showMessageDialog(rootPane, "Có lỗi xảy ra");
//			}
//		}
//		 reset
//		appModel = new AppModel();
//		textFieldInput.setText("");
//		imageSendLabel.setIcon(null);
//		btnCloseImage.setVisible(false);
	}

	public void btnCloseImageEvent() {
		updateChangeFile("", "");
		btnChooseImage.setVisible(false);
	}

	private void updateChangeFile(String path, String name) {
		if (path != "") {
			appModel.setImageName(name);
			appModel.setImagePath(path);
			appModel.checkFileJPG();
			System.out.println(path);
			imageSendLabel.setIcon(new ImageIcon(new ImageIcon(path).getImage()
					.getScaledInstance(imageSendLabel.getWidth(), imageSendLabel.getHeight(), Image.SCALE_SMOOTH)));
			try {
				BufferedImage image = ImageIO.read(new File(path));
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				if (appModel.isFileJPG()) {
					ImageIO.write(image, "jpg", byteArrayOutputStream);
				} else {
					ImageIO.write(image, "png", byteArrayOutputStream);
				}
				byte[] imageBytes = byteArrayOutputStream.toByteArray();
				appModel.setImagebytes(imageBytes);
				appModel.setLength(imageBytes.length);
				appModel.setIcon(appModel.getImageIcon(imageBytes));
			} catch (IOException e) {
			}

		} else {
			appModel = new AppModel();
		}
	}

	private void updateUImessage(String name, String messageConten, ImageIcon icon, String time) {
		TimeImagePanel newPanel = new TimeImagePanel(name, messageConten, icon, time);
		model.addElement(newPanel);
	}

	private void updateUImessage(String name, String messageConten, String time) {
		TimeImagePanel newPanel = new TimeImagePanel(name, messageConten, null, time);
		model.addElement(newPanel);
	}

	private void updateCombobox() {
		comboBox.removeAllItems();
		comboBox.addItem("Send to everyone");
		String idString = "" + appModel.getId();
		for (String e : onlineList) {
			if (!e.equals(idString)) {
				comboBox.addItem("Client " + e);
			}
		}
	}

	private void setIDTitle() {
		this.setTitle("Client " + appModel.getId());
	}

	private void setID(int id) {
		appModel.setId(id);
	}

	public Client() {
		ActionListener act = new ActClient(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 909, 659);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(218, 11, 665, 467);
		contentPane.add(scrollPane);

		list = new JList<>(model);
		list.setCellRenderer(new TimeImageRenderer());
		scrollPane.setViewportView(list);

		textFieldInput = new JTextField();
		textFieldInput.setFont(new Font("Times New Roman", Font.PLAIN, 19));
		textFieldInput.setBounds(218, 500, 421, 45);
		contentPane.add(textFieldInput);
		textFieldInput.setColumns(10);

		btnSend = new JButton("SEND");
		btnSend.addActionListener(act);
		btnSend.setBounds(676, 556, 207, 44);
		contentPane.add(btnSend);

		btnChooseImage = new JButton("Choose a photo");
		btnChooseImage.addActionListener(act);
		btnChooseImage.setBounds(426, 556, 213, 45);
		contentPane.add(btnChooseImage);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Everyone" }));
		comboBox.setBounds(676, 500, 207, 45);
		contentPane.add(comboBox);

		imageSendLabel = new JLabel("");
		imageSendLabel.setBackground(new Color(128, 128, 128));
		imageSendLabel.setOpaque(true);
		imageSendLabel.setBounds(292, 556, 77, 44);
		contentPane.add(imageSendLabel);

		onlineListArea = new JTextArea();
		onlineListArea.setBounds(10, 11, 198, 467);
		onlineListArea.setFocusable(false);
		contentPane.add(onlineListArea);

		btnCloseImage = new JButton();
		btnCloseImage.setIcon(
				new ImageIcon(new ImageIcon("icon_x.png").getImage().getScaledInstance(21, 21, DO_NOTHING_ON_CLOSE)));
		btnCloseImage.setBorder(null);
		btnCloseImage.setBounds(372, 556, 21, 21);

		btnCloseImage.addActionListener(act);
		btnCloseImage.setVisible(false);
		contentPane.add(btnCloseImage);
		this.setVisible(true);

		setUpSocket();
	}
}
