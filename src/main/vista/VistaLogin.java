package main.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class VistaLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField pwdfPassword;
	private JButton btnLogin;

	public JTextField getTxtUsername() {
		return txtUsername;
	}
	
	public JPasswordField getPwdfPassword() {
		return pwdfPassword;
	}
	
	public JButton getBtnLogin() {
		return btnLogin;
	}
	
	public VistaLogin() {
		initComponents();
		setVisible(true);
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblLogin.setBounds(179, 21, 57, 38);
		contentPane.add(lblLogin);
		
		JLabel lblUsername = new JLabel("Nombre de usuario");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUsername.setBounds(154, 70, 121, 14);
		contentPane.add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtUsername.setBounds(154, 95, 121, 20);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Contraseña");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassword.setBounds(179, 126, 71, 14);
		contentPane.add(lblPassword);
		
		pwdfPassword = new JPasswordField();
		pwdfPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		pwdfPassword.setBounds(154, 151, 121, 20);
		contentPane.add(pwdfPassword);
		
		btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnLogin.setBounds(154, 186, 121, 23);
		contentPane.add(btnLogin);
	}

}
