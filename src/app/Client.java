package app;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.ActClient;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JTextArea;
import java.awt.Font;

public class Client extends JFrame {

	private DataOutputStream dos;
	private DataInputStream dis;

	private JPanel contentPane;
	private JTextField textFieldInput;

	private Thread thread;
	private List<String> onlineList;
	private JButton btnSend;
	private JComboBox<String> comboBox;
	private JLabel imageSendLabel;
	private JButton btnChooseImage;
	private JList<TimeImagePanel> list;
	private DefaultListModel<TimeImagePanel> model = new DefaultListModel<>();
	private JTextArea onlineListArea;
	public JButton btnCloseImage;

	public String clientName = "";
	private String receiverName = "";
	private String pathImage = "";
	private byte[] byteImage;
	public Socket socket;

	public void resetValue() {
		pathImage = "";
		byteImage = null;
		receiverName = "";
	}

	public void setUpSocket(Socket socketLogin) {
		thread = new Thread() {
			@Override
			public void run() {
				try {
					socket = socketLogin;
					System.out.println("successful connection :3");
					dos = new DataOutputStream(socket.getOutputStream());
					dis = new DataInputStream(socket.getInputStream());
					setTitle();
					receiveMessageFromServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private void receiveMessageFromServer() {
		try {
			while (true) {
				System.out.println(this.clientName + ": Dang doi o readLien");
				String message = dis.readUTF();
				if (message == null) {
					break;
				}
				System.out.println("Message from server: " + message);

				String[] rp = message.split("\\|>");
				String condition = rp[0];

				if (condition.equals("login notice")) {
					updateUImessage(rp[1], "", getTimeNow());
					continue;
				}

				if (condition.equals("update-list")) {
					onlineList = new ArrayList<>();
					String online = "";
					String[] onlineSplit = rp[1].split("\\+");
					for (int i = 0; i < onlineSplit.length; i++) {
						onlineList.add(onlineSplit[i]);
						online += onlineSplit[i] + " đang online \n";
					}
					System.out.println(online);
					onlineListArea.setText(online);
					updateCombobox();
					continue;
				}

				if (condition.equals("message")) {
					String[] contentRp = rp[2].split("\\-");
					String member = rp[1];
					if (member.equals("Send to all")) {
						byte[] imageDataNew = readImage();
						ImageIcon imageReceive = getImageIcon(imageDataNew);
						updateUImessage(contentRp[0] + ":", contentRp[1], imageReceive, contentRp[2]);
						continue;
					} else {
						byte[] imageDataNew = readImage();
						ImageIcon imageReceive = getImageIcon(imageDataNew);
						updateUImessage(contentRp[0] + " sent to you:", contentRp[1], imageReceive,
								contentRp[2]);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void btnSendEvent() {
		String messageContent = textFieldInput.getText();
		System.out.println("vi tri: " + comboBox.getSelectedIndex());
		if(messageContent.equals("") && pathImage.equals("")) {
			return;
		}
		if (comboBox.getSelectedIndex() == 0) {
			String strToSend = "Send to all" + "|>" + clientName + "-" + messageContent + "-" + getTimeNow();
			ImageIcon imageSend = getImageIcon(byteImage);
			updateUImessage("You: ", messageContent, imageSend, getTimeNow());
			try {
				writeMessage(strToSend, byteImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			String paner = (String) comboBox.getSelectedItem();
			String strToSend = paner + "|>" + clientName + "-" + messageContent + "-" + getTimeNow();
			ImageIcon imageSend = getImageIcon(byteImage);
			updateUImessage("You send to " + paner, messageContent, imageSend, getTimeNow());
			try {
				writeMessage(strToSend, byteImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//		 reset
		textFieldInput.setText("");
		imageSendLabel.setIcon(null);
		resetValue();
		btnCloseImage.setVisible(false);
	}

	public void btnCloseImageEvent() {
		updateChangeFile("", "");
		btnCloseImage.setVisible(false);
		imageSendLabel.setIcon(null);
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

	private void updateChangeFile(String path, String name) {
		if (path != "") {
			this.pathImage = path;
			System.out.println(path);
			ImageIcon imageChoosed = new ImageIcon(new ImageIcon(path).getImage()
					.getScaledInstance(imageSendLabel.getWidth(), imageSendLabel.getHeight(), Image.SCALE_SMOOTH));
			imageSendLabel.setIcon(imageChoosed);
			try {
				BufferedImage image = ImageIO.read(new File(path));
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				if (name.endsWith(".jpg")) {
					ImageIO.write(image, "jpg", byteArrayOutputStream);
				}
				if (name.endsWith(".png")) {
					ImageIO.write(image, "png", byteArrayOutputStream);
				}
				byteImage = byteArrayOutputStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			byteImage = null;
		}
	}

	private void updateUImessage(String name, String messageConten, ImageIcon icon, String time) {
		TimeImagePanel newPanel = new TimeImagePanel(name, messageConten, icon, time);
		model.addElement(newPanel);
	}

	private void updateUImessage(String name, String messageConten, String time) {
		TimeImagePanel newPanel = new TimeImagePanel(name, messageConten, null, time);
		model.addElement(newPanel);
		int lastIndex = list.getModel().getSize() - 1;
		list.setSelectedIndex(lastIndex);
		list.ensureIndexIsVisible(lastIndex);
	}

	private String getTimeNow() {
		LocalTime now = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String formattedTime = now.format(formatter);
		return formattedTime;
	}

	private void updateCombobox() {
		comboBox.removeAllItems();
		comboBox.addItem("Send to everyone");
		String idString = this.clientName;
		for (String e : onlineList) {
			if (!e.equals(idString)) {
				comboBox.addItem(e);
			}
		}
	}

	private void writeMessage(String message, byte[] imageData) throws IOException {
		System.out.println("Client send to server: " + message);
		dos.writeUTF(message);
		if (imageData != null) {
			dos.writeInt(imageData.length);
			dos.write(imageData);
			System.out.println("Đã gửi ảnh cho server");
		} else {
			System.out.println("Déo có ảnh");
			dos.writeInt(-1);
		}
		dos.flush();
		System.out.println("Ket thuc gui anh");
	}

	private byte[] readImage() throws IOException {
		System.out.println("đang chờ ở readInt");
		int imageSize = dis.readInt();
		System.out.println(imageSize);
		if (imageSize <= 0) {
			System.out.println("bỏ qua việc đọc dữ liệu ảnh");
			return null;
		}
		byte[] imageData = new byte[imageSize];
		System.out.println("Dang doi o readFully");
		dis.readFully(imageData);
		return imageData;
	}

	public ImageIcon getImageIcon(byte[] imageBytes) {
		ImageIcon icon = null;
		if (imageBytes != null) {
			icon = new ImageIcon(imageBytes);
		} else {
			return null;
		}

		return scaledImage(icon);
	}

	private ImageIcon scaledImage(ImageIcon icon) {
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		int maxWidth = 300;
		int maxHeight = 250;
		int minWidth = 70;
		int minHeight = 50;
		// Kiểm tra kích thước ảnh
		if (width > maxWidth || height > maxHeight) {
			double scale = Math.min(maxWidth / (double) width, maxHeight / (double) height);
			width = (int) (width * scale);
			height = (int) (height * scale);
			Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
			icon = new ImageIcon(img);
		} else if (width < minWidth || height < minHeight) {
			double scale = Math.max(minWidth / (double) width, minHeight / (double) height);
			width = (int) (width * scale);
			height = (int) (height * scale);
			Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
			icon = new ImageIcon(img);
		}
		return icon;
	}

	private void setTitle() {
		this.setTitle(clientName);
	}

	public Client() {
		this.setResizable(false);
		this.setVisible(true);
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
		list.setBackground(new Color(176, 198, 208));
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
		onlineListArea.setFont(new Font("#9Slide01 Tieu de ngan", Font.BOLD, 14));
		onlineListArea.setBackground(new Color(176, 198, 208));
		onlineListArea.setForeground(new Color(5, 43, 4));
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

//		setUpSocket(socket);
	}
}
