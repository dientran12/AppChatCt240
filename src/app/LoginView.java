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
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class LoginView extends JFrame {

	private JPanel contentPane;
	private JTextField inputUsername;
	public JButton btnLogin;
	private JPasswordField passwordField;
	
	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		LoginView frame = new LoginView();
	}
	
	Socket socket;
	
	
	LoginView _this;
	public LoginView() {
		_this = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 322, 373);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        String username = inputUsername.getText();
		        String password = new String(passwordField.getPassword());
		        System.out.println(username);
		        System.out.println(password);
		        if(username.isEmpty() || password.isEmpty()) {
		        	JOptionPane.showMessageDialog(_this, "Tài khoản hoặc mật khẩu không đúng.\n Đăng ký thất bại!!!");
		        }else {
//		        	Socket socket;
		    		try {
		    			socket = new Socket("localhost", 3333);
		    			System.out.println("Dang xac thuc");
		    			
		    			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		    			DataInputStream dis = new DataInputStream(socket.getInputStream());
		    			
		    			dos.writeUTF("login request|>"+username +"-"+ password);
		    			String response = dis.readUTF();
//		    			String rp[] =response.split("\\|");
		    			if(response.equals("success")) {
		    				
		    				Client clientView = new Client();
		    				clientView.clientName = username;
		    				clientView.setUpSocket(socket);
		    				dispose();
		    			}else {
		    				
		    			}
		    			System.out.println("ket thuc ket noi");
		    		} catch (IOException ex) {
		    			// TODO Auto-generated catch block
		    			ex.printStackTrace();
		    		}
				}
		        
			}
		});
		btnLogin.setBounds(187, 229, 102, 42);
		contentPane.add(btnLogin);
		
		JLabel lbSignIn = new JLabel("Sign In");
		lbSignIn.setForeground(new Color(0, 0, 255));
		lbSignIn.setFont(new Font("#9Slide01 Tieu de ngan", Font.BOLD, 24));
		lbSignIn.setHorizontalAlignment(SwingConstants.CENTER);
		lbSignIn.setBounds(103, 22, 108, 58);
		contentPane.add(lbSignIn);
		
		inputUsername = new JTextField();
		inputUsername.setToolTipText("");
		inputUsername.setBounds(120, 105, 169, 34);
		contentPane.add(inputUsername);
		inputUsername.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("User name");
		lblNewLabel.setBounds(25, 104, 67, 34);
		contentPane.add(lblNewLabel);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(25, 151, 67, 34);
		contentPane.add(lblPassword);
		
		JButton btnNewButton_1 = new JButton("Create an Account");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setBounds(25, 280, 137, 28);
		contentPane.add(btnNewButton_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(120, 151, 169, 34);
		contentPane.add(passwordField);
		this.setResizable(false);
		this.setVisible(true);
	}
}
