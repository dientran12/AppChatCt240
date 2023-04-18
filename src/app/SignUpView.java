package app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.ActClient;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class SignUpView extends JFrame {

	private JPanel contentPane;
	public JButton btnSubmit;
	private JPasswordField comfirmInput;
	private JTextField usernameInput;
	private boolean isSuccess = true;
	private JTextField nicknameInput;
	private JPasswordField passwordInput;

	/**
	 * Launch the application.
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */

	public SignUpView() {
		SignUpView _this = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 383, 461);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameInput.getText();
				String password = new String(passwordInput.getPassword());
				String comfirm = new String(comfirmInput.getPassword());
				String nickname = nicknameInput.getText();

				if (username.isEmpty() || password.isEmpty() || comfirm.isEmpty() || nickname.isEmpty()) {
					JOptionPane.showMessageDialog(_this, "Bạn phải nhập đủ thông tin.");
				} else if (!password.equals(comfirm)) {
					JOptionPane.showMessageDialog(_this, "Mật khẩu không khớp", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					Socket socket;
					try {
						socket = new Socket("localhost", 3333);
						System.out.println("Dang xac thuc");
						
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						DataInputStream dis = new DataInputStream(socket.getInputStream());
						
						dos.writeUTF("sign up request|>"+username +"-"+ password + "-" + nickname);
						String response = dis.readUTF();
						if (response.equals("success")) {
							LoginView loginView = new LoginView();
							dispose();
						} else {
							JOptionPane.showMessageDialog(_this, "Tài khoản hoặc tên đã tồn tại!\n Đăng ký thất bại!!!", "Error", JOptionPane.ERROR_MESSAGE);
						}
						dos.close();
						dis.close();
						socket.close();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnSubmit.setBounds(103, 338, 152, 43);
		contentPane.add(btnSubmit);

		JLabel lbSignIn = new JLabel("Sign Up");
		lbSignIn.setForeground(new Color(0, 0, 255));
		lbSignIn.setFont(new Font("#9Slide01 Tieu de ngan", Font.BOLD, 24));
		lbSignIn.setHorizontalAlignment(SwingConstants.CENTER);
		lbSignIn.setBounds(126, 22, 108, 58);
		contentPane.add(lbSignIn);

		JLabel lblNewLabel = new JLabel("User name");
		lblNewLabel.setBounds(25, 104, 67, 34);
		contentPane.add(lblNewLabel);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(25, 151, 67, 34);
		contentPane.add(lblPassword);

		comfirmInput = new JPasswordField();
		comfirmInput.setBounds(177, 197, 169, 34);
		contentPane.add(comfirmInput);

		JLabel lblCom = new JLabel("Comfirm password");
		lblCom.setBounds(25, 197, 119, 34);
		contentPane.add(lblCom);

		usernameInput = new JTextField();
		usernameInput.setColumns(10);
		usernameInput.setBounds(177, 107, 169, 34);
		contentPane.add(usernameInput);

		nicknameInput = new JTextField();
		nicknameInput.setColumns(10);
		nicknameInput.setBounds(93, 282, 175, 34);
		contentPane.add(nicknameInput);

		JLabel lblNickName = new JLabel("Nick name");
		lblNickName.setBounds(145, 250, 67, 34);
		contentPane.add(lblNickName);

		passwordInput = new JPasswordField();
		passwordInput.setBounds(177, 154, 169, 34);
		contentPane.add(passwordInput);
		this.setResizable(false);
		this.setVisible(true);
	}
}
