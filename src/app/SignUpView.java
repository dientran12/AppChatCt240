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
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class SignUpView extends JFrame {

	private JPanel contentPane;
	private JTextField inputUsername;
	public JButton btnSubmit;
	private JPasswordField passwordField;
	private JTextField textField;
	private boolean isSuccess = true;

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		SignUpView frame = new SignUpView();
	}
	

	public SignUpView() {
		SignUpView _this = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 384, 406);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 // Đăng nhập thất bại, hiển thị thông báo lỗi
				isSuccess = !isSuccess;
				if(isSuccess) {
					JOptionPane.showMessageDialog(_this, "Đăng ký thành công!!!");
				}else {
					JOptionPane.showMessageDialog(_this, "Có lỗi xảy ra. Đăng ký thất bại!!!");
				}
			}
		});
		btnSubmit.setBounds(187, 265, 159, 43);
		contentPane.add(btnSubmit);
		
		JLabel lbSignIn = new JLabel("Sign Up");
		lbSignIn.setForeground(new Color(0, 0, 255));
		lbSignIn.setFont(new Font("#9Slide01 Tieu de ngan", Font.BOLD, 24));
		lbSignIn.setHorizontalAlignment(SwingConstants.CENTER);
		lbSignIn.setBounds(134, 23, 108, 58);
		contentPane.add(lbSignIn);
		
		inputUsername = new JTextField();
		inputUsername.setToolTipText("");
		inputUsername.setBounds(177, 151, 169, 34);
		contentPane.add(inputUsername);
		inputUsername.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("User name");
		lblNewLabel.setBounds(25, 104, 67, 34);
		contentPane.add(lblNewLabel);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(25, 151, 67, 34);
		contentPane.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(177, 197, 169, 34);
		contentPane.add(passwordField);
		
		JLabel lblCom = new JLabel("Comfirm password");
		lblCom.setBounds(25, 197, 119, 34);
		contentPane.add(lblCom);
		
		textField = new JTextField();
		textField.setToolTipText("");
		textField.setColumns(10);
		textField.setBounds(177, 107, 169, 34);
		contentPane.add(textField);
		this.setResizable(false);
		this.setVisible(true);
	}
}
